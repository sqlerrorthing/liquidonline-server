package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriends
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketListenerRegistrar
import `fun`.sqlerrorthing.liquidonline.ws.listener.listeners.AuthPacketListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@Service
class WebSocketSessionStorageService(
    private val userService: UserService,
    private val packetListenerRegistrar: PacketListenerRegistrar,
    private val friendshipService: FriendshipService
) {
    private val sessions: MutableSet<WebSocketSession> = CopyOnWriteArraySet()
    private val authoredSessions: MutableMap<WebSocketSession, UserSession> = ConcurrentHashMap()

    fun sessionPacket(session: WebSocketSession, packet: Packet) {
        if (!sessions.contains(session)) {
            return
        }

        authoredSessions[session]?.let {
            if (packet !is C2SLogin) {
                authoredSessionPacket(session, it, packet)
                return
            }
        }
    }

    fun isInSession(wsSession: WebSocketSession): Boolean {
        return sessions.contains(wsSession)
    }

    fun authSessionPacket(wsSession: WebSocketSession, session: UserSession) {
        authoredSessions[wsSession] = session
    }

    fun findUserSession(entity: UserEntity): UserSession? {
        return authoredSessions.values.find { it.user == entity }
    }

    fun authoredSessionPacket(wsSession: WebSocketSession, session: UserSession, packet: Packet) {
        packetListenerRegistrar.dispatchPacket(
            wsSession, session, packet
        )
    }

    fun addSession(session: WebSocketSession) {
        sessions.add(session)
    }

    fun removeSession(session: WebSocketSession) {
        sessions.remove(session)

        authoredSessions[session]?.let { userSession ->
            authoredSessions.remove(session)

            userSession.user.lastLogin = Instant.now()
            userService.save(userSession.user)
        }
    }

    @Scheduled(fixedRate = 1000)
    fun sendFriends() {
        for ((connection, session) in authoredSessions) {
            val friends: List<FriendDto> = friendshipService.findUserFriends(session.user).map { friend ->
                findUserSession(friend)?.toFriendDto() ?: friend.toFriendDto()
            }

            connection.sendMessage(
                S2CFriends
                    .builder()
                    .friends(friends)
                    .build()
            )
        }
    }
}
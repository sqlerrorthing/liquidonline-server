package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendJoined
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendLeaved
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketListenerRegistrar
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

    val authoredSessionsIterator get() = authoredSessions.iterator()

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
        val selfFriendDto = session.toFriendDto()

        val joinPacket = S2CFriendJoined
            .builder()
            .friend(selfFriendDto)
            .build()

        val friendsSessions: List<WebSocketSession> = friendshipService.findUserFriends(session.user).mapNotNull { friend ->
            findUserSession(friend)?.session
        }

        friendsSessions.forEach { it.sendMessage(joinPacket) }
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

            val selfFriendDto = userSession.user.toFriendDto()

            val leavePacket = S2CFriendLeaved
                .builder()
                .friend(selfFriendDto)
                .build()

            val friendsSessions: List<WebSocketSession> = friendshipService.findUserFriends(userSession.user).mapNotNull { friend ->
                findUserSession(friend)?.session
            }

            friendsSessions.forEach { it.sendMessage(leavePacket) }
        }
    }
}
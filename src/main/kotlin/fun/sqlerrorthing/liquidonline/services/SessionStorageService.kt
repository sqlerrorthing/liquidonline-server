package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendJoined
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendLeaved
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketListenerRegistrar
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

@Service
class SessionStorageService(
    private val userService: UserService,
    private val packetListenerRegistrar: PacketListenerRegistrar,
    private val friendshipService: FriendshipService,
    @Lazy
    private val sessionTaskService: SessionTaskService
) {
    private val sessions: MutableSet<WebSocketSession> = CopyOnWriteArraySet()
    private val authoredSessions: MutableList<UserSession> = CopyOnWriteArrayList()

    fun sessionPacket(session: WebSocketSession, packet: Packet) {
        if (!sessions.contains(session)) {
            return
        }

        authoredSessions.find { it.wsSession == session }?.let {
            if (packet !is C2SLogin) {
                authoredSessionPacket(it, packet)
                return
            }
        }
    }

    fun isInSession(wsSession: WebSocketSession): Boolean {
        return sessions.contains(wsSession)
    }

    fun authSessionPacket(wsSession: WebSocketSession, session: UserSession) {
        authoredSessions.add(session)
        val selfFriendDto = session.toFriendDto()

        val joinPacket = S2CFriendJoined
            .builder()
            .friend(selfFriendDto)
            .build()

        val friendsSessions: List<WebSocketSession> = friendshipService.findUserFriends(session.user).mapNotNull { friend ->
            findUserSession(friend)?.wsSession
        }

        friendsSessions.forEach { it.sendPacket(joinPacket) }

        sessionTaskService.startSessionTasks(session)
    }

    fun findUserSession(entity: UserEntity): UserSession? {
        return authoredSessions.find { it.user == entity }
    }

    fun authoredSessionPacket(session: UserSession, packet: Packet) {
        packetListenerRegistrar.dispatchPacket(
            session, packet
        )
    }

    fun addSession(session: WebSocketSession) {
        sessions.add(session)
    }

    fun removeSession(session: WebSocketSession) {
        sessions.remove(session)

        authoredSessions.find { it.wsSession == session }?.let { userSession ->
            authoredSessions.remove(userSession)

            userSession.user.lastLogin = Instant.now()
            userService.save(userSession.user)

            val selfFriendDto = userSession.user.toFriendDto()

            val leavePacket = S2CFriendLeaved
                .builder()
                .friend(selfFriendDto)
                .build()

            val friendsSessions: List<WebSocketSession> = friendshipService.findUserFriends(userSession.user).mapNotNull { friend ->
                findUserSession(friend)?.wsSession
            }

            friendsSessions.forEach { it.sendPacket(leavePacket) }

            sessionTaskService.stopSessionTasks(userSession)
        }
    }
}
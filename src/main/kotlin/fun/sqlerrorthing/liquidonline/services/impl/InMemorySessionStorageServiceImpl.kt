package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacketToFriends
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendJoined
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendLeaved
import `fun`.sqlerrorthing.liquidonline.services.FriendshipService
import `fun`.sqlerrorthing.liquidonline.services.SessionStorageService
import `fun`.sqlerrorthing.liquidonline.services.SessionTaskService
import `fun`.sqlerrorthing.liquidonline.services.UserService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketListenerRegistrar
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

@Service
class InMemorySessionStorageServiceImpl(
    private val userService: UserService,
    private val packetListenerRegistrar: PacketListenerRegistrar,
    private val friendshipService: FriendshipService,
    @Lazy
    private val sessionTaskService: SessionTaskService
): SessionStorageService {
    private val sessions: MutableSet<WebSocketSession> = CopyOnWriteArraySet()
    private val authoredSessions: MutableList<UserSession> = CopyOnWriteArrayList()

    override fun sessionPacket(session: WebSocketSession, packet: Packet) {
        if (!sessions.contains(session)) {
            return
        }

        authoredSessions.find { it.wsSession == session }?.let {
            if (packet !is C2SLogin) {
                packetListenerRegistrar.dispatchPacket(
                    it, packet
                )
            }
        }
    }

    override fun isInSession(wsSession: WebSocketSession): Boolean {
        return sessions.contains(wsSession)
    }

    override fun authSession(session: UserSession) {
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

    override fun findUserSession(entity: UserEntity): UserSession? {
        return authoredSessions.find { it.user == entity }
    }

    override fun addSession(session: WebSocketSession) {
        sessions.add(session)
    }

    override fun removeSessionAndNotifyOtherUsers(session: WebSocketSession) {
        sessions.remove(session)

        authoredSessions.find { it.wsSession == session }?.let { userSession ->
            authoredSessions.remove(userSession)
            sessionTaskService.stopSessionTasks(userSession)

            userSession.user.lastLogin = Instant.now()
            userService.save(userSession.user)

            val selfFriendDto = userSession.user.toFriendDto()

            S2CFriendLeaved
                .builder()
                .friend(selfFriendDto)
                .build()
            .apply {
                userSession.sendPacketToFriends {
                    this
                }
            }
        }
    }
}
package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.services.*
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
    @Lazy
    private val sessionTaskService: SessionTaskService,
    private val onlineFriendsNotifierService: OnlineFriendsNotifierService
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

    override fun isInSession(session: WebSocketSession): Boolean {
        return sessions.contains(session)
    }

    override fun authSessionAndNotifyUserFriends(session: UserSession) {
        authoredSessions.add(session)
        onlineFriendsNotifierService.notifyFriendJoined(session)
    }

    override fun findUserSession(user: UserEntity): UserSession? {
        return authoredSessions.find { it.user == user }
    }

    override fun addSession(session: WebSocketSession) {
        sessions.add(session)
    }

    override fun removeSessionAndNotifyUserFriends(session: WebSocketSession) {
        sessions.remove(session)

        authoredSessions.find { it.wsSession == session }?.let { userSession ->
            authoredSessions.remove(userSession)
            sessionTaskService.stopSessionTasks(userSession)

            userService.save(userSession.user.apply {
                lastLogin = Instant.now()
            })

            onlineFriendsNotifierService.notifyFriendLeaved(userSession)
        }
    }
}

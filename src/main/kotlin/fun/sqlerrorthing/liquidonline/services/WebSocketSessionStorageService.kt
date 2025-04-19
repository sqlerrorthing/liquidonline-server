package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.extensions.toDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.packets.s2c.login.S2CConnected
import `fun`.sqlerrorthing.liquidonline.packets.s2c.login.S2CDisconnected
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.SkinValidator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

@Service
class WebSocketSessionStorageService(
    private val userService: UserService,
    private val skinValidator: SkinValidator,
) {
    private val sessions: MutableSet<WebSocketSession> = CopyOnWriteArraySet()
    private val authoredSessions: MutableMap<WebSocketSession, UserSession> = ConcurrentHashMap()

    fun sessionPacket(session: WebSocketSession, packet: Packet) {
        if (!sessions.contains(session)) {
            return
        }

        if (authoredSessions.contains(session)) {
            if (packet !is C2SLogin) {
                authoredSessionPacket(session, packet)
            }
        } else if (packet is C2SLogin) {
            val user = userService.findUserByToken(packet.token) ?: run {
                session.sendMessage(
                    S2CDisconnected
                        .builder()
                        .reason(S2CDisconnected.Reason.INVALID_TOKEN)
                        .build()
                )
                session.close(CloseStatus.BAD_DATA.withReason("Invalid token"))
                return
            }

            if (authoredSessions.values.find { it.user == user } != null) {
                session.sendMessage(
                    S2CDisconnected
                        .builder()
                        .reason(S2CDisconnected.Reason.ALREADY_CONNECTED)
                        .build()
                )
                session.close(CloseStatus.NORMAL.withReason("Already connected"))
                return
            }

            val head = skinValidator.parseHead(packet.skin) ?: run {
                session.sendMessage(
                    S2CDisconnected
                        .builder()
                        .reason(S2CDisconnected.Reason.INVALID_INITIAL_PLAYER_DATA)
                        .build()
                )
                session.close(CloseStatus.NORMAL.withReason("Invalid head"))
                return
            }

            val userSession = UserSession
                .builder()
                .user(user)
                .minecraftUsername(packet.minecraftUsername)
                .server(packet.server)
                .skin(head)
                .build()

            authoredSessions[session] = userSession
            session.attributes["user"] = userSession

            session.sendMessage(
                S2CConnected
                    .builder()
                    .account(user.toDto())
                    .build()
            )
        }
    }

    fun authoredSessionPacket(session: WebSocketSession, packet: Packet) {
        println("Session: $session, packet: $packet")
    }

    fun addSession(session: WebSocketSession) {
        sessions.add(session)
    }

    fun removeSession(session: WebSocketSession) {
        sessions.remove(session)
        authoredSessions.remove(session)
    }
}
package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.toDto
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.packets.s2c.login.S2CConnected
import `fun`.sqlerrorthing.liquidonline.packets.s2c.login.S2CDisconnected
import `fun`.sqlerrorthing.liquidonline.services.impl.UserService
import `fun`.sqlerrorthing.liquidonline.services.impl.SessionStorageService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.SkinValidator
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession

@Component
class AuthPacketListener(
    private val userService: UserService,
    private val sessionStorageService: SessionStorageService,
    private val skinValidator: SkinValidator
) {
    fun authConnection(session: WebSocketSession, packet: C2SLogin): UserSession? {
        val user = userService.findUserByToken(packet.token) ?: run {
            session.sendPacket(
                S2CDisconnected
                    .builder()
                    .reason(S2CDisconnected.Reason.INVALID_TOKEN)
                    .build()
            )
            session.close(CloseStatus.BAD_DATA.withReason("Invalid token"))
            return null
        }

        if (sessionStorageService.findUserSession(user) != null) {
            session.sendPacket(
                S2CDisconnected
                    .builder()
                    .reason(S2CDisconnected.Reason.ALREADY_CONNECTED)
                    .build()
            )
            session.close(CloseStatus.NORMAL.withReason("Already connected"))
            return null
        }

        val head = skinValidator.validateHead(packet.skin) ?: run {
            session.sendPacket(
                S2CDisconnected
                    .builder()
                    .reason(S2CDisconnected.Reason.INVALID_INITIAL_PLAYER_DATA)
                    .build()
            )
            session.close(CloseStatus.NORMAL.withReason("Invalid head"))
            return null
        }

        val userSession = UserSession
            .builder()
            .user(user)
            .minecraftUsername(packet.minecraftUsername)
            .server(packet.server)
            .skin(head)
            .wsSession(session)
            .build()

        session.attributes["user"] = userSession

        session.sendPacket(
            S2CConnected
                .builder()
                .account(user.toDto())
                .build()
        )

        return userSession
    }
}
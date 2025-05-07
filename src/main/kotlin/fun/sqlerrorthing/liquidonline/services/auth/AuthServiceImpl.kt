package `fun`.sqlerrorthing.liquidonline.services.auth

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.toDto
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.packets.s2c.login.S2CConnected
import `fun`.sqlerrorthing.liquidonline.packets.s2c.login.S2CDisconnected
import `fun`.sqlerrorthing.liquidonline.services.session.SessionStorageService
import `fun`.sqlerrorthing.liquidonline.services.user.UserService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.SkinValidator
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession

@Service
class AuthServiceImpl(
    private val userService: UserService,
    private val sessionStorageService: SessionStorageService,
    private val skinValidator: SkinValidator
) : AuthService {
    override fun authenticate(session: WebSocketSession, packet: C2SLogin) {
        if (!sessionStorageService.isInSession(session)) {
            return
        }

        val user = userService.findUserByToken(packet.token) ?: run {
            handleInvalidToken(session)
            return
        }

        if (sessionStorageService.findUserSession(user) != null) {
            handleAlreadyConnected(session)
            return
        }

        val head = skinValidator.validateHead(packet.skin) ?: run {
            handleInvalidSkin(session)
            return
        }

        createUserSession(session, packet, user, head).let {
            sessionStorageService.authSessionAndNotifyUserFriends(it)
        }
    }

    private fun handleInvalidToken(session: WebSocketSession) {
        session.sendPacket(
            S2CDisconnected.builder()
                .reason(S2CDisconnected.Reason.INVALID_TOKEN)
                .build()
        )

        session.close(CloseStatus.BAD_DATA.withReason("Invalid token"))
    }

    private fun handleAlreadyConnected(session: WebSocketSession) {
        session.sendPacket(
            S2CDisconnected.builder()
                .reason(S2CDisconnected.Reason.ALREADY_CONNECTED)
                .build()
        )

        session.close(CloseStatus.NORMAL.withReason("Already connected"))
    }

    private fun handleInvalidSkin(session: WebSocketSession) {
        session.sendPacket(
            S2CDisconnected.builder()
                .reason(S2CDisconnected.Reason.INVALID_INITIAL_PLAYER_DATA)
                .build()
        )

        session.close(CloseStatus.NORMAL.withReason("Invalid head"))
    }

    private fun createUserSession(
        session: WebSocketSession,
        packet: C2SLogin,
        user: UserEntity,
        head: ByteArray
    ): UserSession {
        val userSession = UserSession.builder()
            .user(user)
            .minecraftUsername(packet.minecraftUsername)
            .server(packet.server)
            .skin(head)
            .wsSession(session)
            .build()

        session.attributes["user"] = userSession

        session.sendPacket(
            S2CConnected.builder()
                .account(user.toDto())
                .build()
        )

        return userSession
    }
}

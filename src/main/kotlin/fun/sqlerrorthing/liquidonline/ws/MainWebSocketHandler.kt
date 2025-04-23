package `fun`.sqlerrorthing.liquidonline.ws

import com.fasterxml.jackson.databind.ObjectMapper
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.services.AuthService
import `fun`.sqlerrorthing.liquidonline.services.SessionStorageService
import jakarta.validation.Validator
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession

@Component
class MainWebSocketHandler(
    objectMapper: ObjectMapper,
    validator: Validator,
    private val sessionStorageService: SessionStorageService,
    private val authService: AuthService,
) : PacketWebSocketHandler(objectMapper, validator) {
    override fun handlePacket(session: WebSocketSession, packet: Packet) {
        if (!sessionStorageService.isInSession(session)) {
            return
        }

        if (packet is C2SLogin) {
            authService.authenticateUser(session, packet)?.let {
                sessionStorageService.authSessionAndNotifyUserFriends(it)
            }
        } else {
            sessionStorageService.sessionPacket(session, packet)
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessionStorageService.addSession(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionStorageService.removeSessionAndNotifyUserFriends(session)
    }
}

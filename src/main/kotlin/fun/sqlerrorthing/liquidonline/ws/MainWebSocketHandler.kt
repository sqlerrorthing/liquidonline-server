package `fun`.sqlerrorthing.liquidonline.ws

import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.packets.strategy.PacketSerializationStrategy
import `fun`.sqlerrorthing.liquidonline.services.auth.AuthService
import `fun`.sqlerrorthing.liquidonline.services.session.SessionStorageService
import jakarta.validation.Validator
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession

@Component
class MainWebSocketHandler(
    packetSerializationStrategy: PacketSerializationStrategy,
    validator: Validator,
    private val sessionStorageService: SessionStorageService,
    private val authService: AuthService,
) : PacketWebSocketHandler(packetSerializationStrategy, validator) {
    override fun handlePacket(session: WebSocketSession, packet: Packet) {
        if (packet is C2SLogin) {
            authService.authenticate(session, packet)
        } else {
            sessionStorageService.sessionPacket(session, packet)
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessionStorageService.addSession(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionStorageService.removeSession(session)
    }
}

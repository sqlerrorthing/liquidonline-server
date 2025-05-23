package `fun`.sqlerrorthing.liquidonline.ws

import com.fasterxml.jackson.databind.ObjectMapper
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.services.WebSocketSessionStorageService
import `fun`.sqlerrorthing.liquidonline.ws.listener.listeners.AuthPacketListener
import jakarta.validation.Validator
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession

@Component
class MainWebSocketHandler(
    objectMapper: ObjectMapper,
    validator: Validator,
    private val sessionStorageService: WebSocketSessionStorageService,
    private val authPacketListener: AuthPacketListener,
) : PacketWebSocketHandler(objectMapper, validator) {
    override fun handlePacket(session: WebSocketSession, packet: Packet) {
        if (!sessionStorageService.isInSession(session)) {
            return
        }

        if (packet is C2SLogin) {
            val userSession = authPacketListener.authConnection(session, packet) ?: return
            sessionStorageService.authSessionPacket(session, userSession)
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
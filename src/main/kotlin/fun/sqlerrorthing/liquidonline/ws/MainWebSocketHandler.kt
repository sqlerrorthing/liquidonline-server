package `fun`.sqlerrorthing.liquidonline.ws

import com.fasterxml.jackson.databind.ObjectMapper
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.services.WebSocketSessionStorageService
import jakarta.validation.Validator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession

@Component
class MainWebSocketHandler(
    objectMapper: ObjectMapper,
    validator: Validator,
    private val sessionStorageService: WebSocketSessionStorageService,
) : PacketWebSocketHandler(objectMapper, validator) {
    override fun handlePacket(session: WebSocketSession, packet: Packet) {
        sessionStorageService.sessionPacket(session, packet)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessionStorageService.addSession(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionStorageService.removeSession(session)
    }
}
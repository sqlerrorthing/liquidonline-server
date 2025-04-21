package `fun`.sqlerrorthing.liquidonline.ws

import com.fasterxml.jackson.databind.ObjectMapper
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.Packets
import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.packets.s2c.login.S2CDisconnected
import `fun`.sqlerrorthing.liquidonline.packets.s2c.login.S2CValidationFailure
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

abstract class PacketWebSocketHandler(
    private val objectMapper: ObjectMapper,
    private val validator: Validator
) : TextWebSocketHandler() {
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        runCatching {
            val deserialized = deserializePacket(message.payload)

            try {
                validatePacket(deserialized)
            } catch (e: ConstraintViolationException) {
                session.sendPacket(
                    S2CValidationFailure
                        .builder()
                        .details(
                            e.constraintViolations
                                .map { S2CValidationFailure.FailureDetail
                                    .builder()
                                    .path(it.propertyPath.toString())
                                    .message(it.message)
                                    .build()
                                }
                        )
                        .build()
                )

                if (deserialized is C2SLogin) {
                    session.sendPacket(
                        S2CDisconnected
                            .builder()
                            .reason(S2CDisconnected.Reason.INVALID_INITIAL_PLAYER_DATA)
                            .build()
                    )
                    session.close()
                }

                return
            }

            handlePacket(session, deserialized)
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun deserializePacket(payload: String): Packet {
        val root = objectMapper.readTree(payload)

        val id = root["id"].asInt().toByte()
        val packetClass: Class<out Packet> = Packets.PACKETS_WITH_ID[id] ?: throw IllegalArgumentException("Unknown packet id")

        return objectMapper.treeToValue(root["payload"], packetClass)
    }

    private fun validatePacket(packet: Packet) {
        val violations = validator.validate(packet)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }

    protected abstract fun handlePacket(session: WebSocketSession, packet: Packet)
}
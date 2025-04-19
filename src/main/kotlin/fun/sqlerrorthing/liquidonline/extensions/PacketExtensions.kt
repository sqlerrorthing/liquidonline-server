package `fun`.sqlerrorthing.liquidonline.extensions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

private val objectMapper = ObjectMapper()

fun WebSocketSession.sendMessage(message: Packet) {
    this.sendMessage(TextMessage(message.serialize(objectMapper)))
}

fun Packet.serialize(objectMapper: ObjectMapper): String {
    val message = objectMapper.createObjectNode()

    message.put("id", id().toInt())
    message.set<JsonNode>("payload", objectMapper.convertValue(this, JsonNode::class.java))

    return objectMapper.writeValueAsString(message)
}
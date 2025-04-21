package `fun`.sqlerrorthing.liquidonline.extensions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.utils.SpringContextHolder
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

private val objectMapper by lazy {
    SpringContextHolder.getBean(ObjectMapper::class.java)
}

fun WebSocketSession.sendPacket(message: Packet) {
    this.sendMessage(TextMessage(message.serialize()))
}

fun Packet.serialize(): String {
    val message = objectMapper!!.createObjectNode()

    message.put("id", id().toInt())
    message.set<JsonNode>("payload", objectMapper!!.convertValue(this, JsonNode::class.java))

    return objectMapper!!.writeValueAsString(message)
}
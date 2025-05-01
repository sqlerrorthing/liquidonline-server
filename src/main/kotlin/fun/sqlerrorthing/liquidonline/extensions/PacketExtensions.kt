package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.strategy.PacketSerializationStrategy
import `fun`.sqlerrorthing.liquidonline.utils.SpringContextHolder
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

private val packetSerializationStrategy by lazy {
    requireNotNull(SpringContextHolder.getBean(PacketSerializationStrategy::class.java))
}

fun WebSocketSession.sendPacket(message: Packet) {
    this.sendMessage(TextMessage(message.serialize()))
}

fun Packet.serialize(): String {
    return packetSerializationStrategy.serializePacketToString(this)
}

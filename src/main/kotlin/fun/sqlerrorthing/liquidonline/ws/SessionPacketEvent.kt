package `fun`.sqlerrorthing.liquidonline.ws

import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.web.socket.WebSocketSession

data class SessionPacketEvent<T : Packet>(
    val wsSession: WebSocketSession,
    val session: UserSession,
    val packet: T
)

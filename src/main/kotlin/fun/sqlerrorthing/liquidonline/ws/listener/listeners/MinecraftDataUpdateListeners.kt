package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdateMinecraftUsername
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketMessageListener
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
@WebSocketMessageListener
class MinecraftDataUpdateListeners {
    @PacketMessageListener
    fun handleMinecraftUsernameUpdateEvent(wsSession: WebSocketSession, session: UserSession, packet: C2SUpdateMinecraftUsername) {
        session.minecraftUsername = packet.username
    }
}
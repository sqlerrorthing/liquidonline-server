package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners.party

import `fun`.sqlerrorthing.liquidonline.services.PartyServiceWithNotifyPackets
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.stereotype.Component

@Component
@WebSocketMessageListener
class PartyInviteListeners(
    private val partyServiceWithNotifyPackets: PartyServiceWithNotifyPackets
) {
}
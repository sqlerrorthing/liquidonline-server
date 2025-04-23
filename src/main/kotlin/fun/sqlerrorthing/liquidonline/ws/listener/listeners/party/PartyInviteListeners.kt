package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners.party

import `fun`.sqlerrorthing.liquidonline.services.PartyService
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.stereotype.Component

@Component
@WebSocketMessageListener
@Suppress("unused")
class PartyInviteListeners(
    private val partyService: PartyService
)

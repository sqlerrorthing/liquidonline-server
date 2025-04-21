package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface PartyService {
    fun createParty(
        name: String,
        baseMember: UserSession,
        playData: PlayDto? = null
    ): Party
}
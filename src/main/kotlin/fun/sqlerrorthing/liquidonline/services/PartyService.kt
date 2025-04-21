package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface PartyService {
    fun createParty(
        name: String,
        baseMember: UserSession,
        playData: PlayDto? = null
    ): Party

    fun joinPartyMember(
        party: Party,
        user: UserSession,
        playData: PlayDto? = null
    ): PartyMember

    fun disboundAndNotifyPartyMembers(
        party: Party
    )

    fun kickPartyMember(
        party: Party,
        member: PartyMember
    )

    fun transferPartyOwnership(
        party: Party,
        newOwner: PartyMember
    )
}
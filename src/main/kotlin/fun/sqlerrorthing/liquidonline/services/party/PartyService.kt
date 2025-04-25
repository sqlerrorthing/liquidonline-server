package `fun`.sqlerrorthing.liquidonline.services.party

import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.exceptions.AlreadyInPartyException
import `fun`.sqlerrorthing.liquidonline.exceptions.AlreadyInThisPartyException
import `fun`.sqlerrorthing.liquidonline.exceptions.MemberInAnotherPartyException
import `fun`.sqlerrorthing.liquidonline.exceptions.NoEnoughPartyPermissions
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface PartyService {
    @Throws(
        AlreadyInPartyException::class
    )
    fun createParty(
        name: String,
        baseMember: UserSession,
        playData: PlayDto? = null
    ): Party

    @Throws(
        AlreadyInThisPartyException::class
    )
    fun joinPartyMember(
        party: Party,
        user: UserSession,
        playData: PlayDto? = null
    ): PartyMember

    @Throws(
        NoEnoughPartyPermissions::class
    )
    fun disbandPartyRequested(
        party: Party,
        requester: PartyMember
    )

    fun disbandParty(
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

    @Throws(
        MemberInAnotherPartyException::class
    )
    fun removePartyMember(
        party: Party,
        member: PartyMember,
    )
}

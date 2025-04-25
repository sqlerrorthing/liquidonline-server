package `fun`.sqlerrorthing.liquidonline.services.party

import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.session.InvitedMember
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession

@Suppress("TooManyFunctions")
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
        NotEnoughPartyPermissions::class
    )
    fun disbandPartyRequested(
        party: Party,
        requester: PartyMember
    )

    @Throws(
        UserNotFoundException::class,
        NotEnoughPartyPermissions::class,
        MemberInAnotherPartyException::class
    )
    fun createInvite(
        party: Party,
        sender: PartyMember,
        receiverUsername: String
    ): InvitedMember

    fun createInvite(
        party: Party,
        sender: PartyMember,
        receiver: UserSession
    ): InvitedMember

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

    fun inviteDeclined(
        party: Party,
        invite: InvitedMember
    )

    fun sessionDisconnected(
        session: UserSession
    )
}

package `fun`.sqlerrorthing.liquidonline.services.party

import `fun`.sqlerrorthing.liquidonline.dto.party.PartyDto
import `fun`.sqlerrorthing.liquidonline.dto.play.MarkerDto
import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.session.InvitedMember
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import java.util.*

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
        inviteUuid: UUID? = null,
        playData: PlayDto? = null
    ): PartyMember

    @Throws(
        NotEnoughPartyPermissionsExceptions::class
    )
    fun disbandPartyRequested(
        party: Party,
        requester: PartyMember
    )

    @Throws(
        UserNotFoundException::class,
        NotEnoughPartyPermissionsExceptions::class,
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

    fun kickPartyMember(
        party: Party,
        requester: PartyMember,
        memberId: Int
    )

    fun transferPartyOwnership(
        party: Party,
        requester: PartyMember,
        newOwnerId: Int
    )

    fun transferPartyOwnership(
        party: Party,
        newOwner: PartyMember
    )

    fun memberPlayDataUpdate(
        party: Party,
        member: PartyMember,
        playData: PlayDto?
    )

    fun attackEntity(
        party: Party,
        member: PartyMember,
        entityId: Int
    )

    @Throws(
        MemberInAnotherPartyException::class,
        PartyMemberInMarkersRateLimitException::class
    )
    fun partyMarker(
        party: Party,
        member: PartyMember,
        marker: MarkerDto
    )

    @Throws(
        MemberInAnotherPartyException::class
    )
    fun removePartyMember(
        party: Party,
        member: PartyMember,
    )

    fun inviteAccepted(
        inviteUuid: UUID,
        requester: UserSession,
        playData: PlayDto?
    ): PartyDto

    fun inviteAccepted(
        party: Party,
        requester: UserSession,
        invite: InvitedMember,
        playData: PlayDto?
    )

    fun inviteDeclined(
        requester: UserSession,
        inviteUuid: UUID
    )

    fun inviteDeclined(
        party: Party,
        requester: UserSession,
        invite: InvitedMember
    )

    fun sessionDisconnected(
        session: UserSession
    )
}

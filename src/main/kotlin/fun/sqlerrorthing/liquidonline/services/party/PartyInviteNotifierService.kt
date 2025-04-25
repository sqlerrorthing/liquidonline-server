package `fun`.sqlerrorthing.liquidonline.services.party

import `fun`.sqlerrorthing.liquidonline.session.InvitedMember
import `fun`.sqlerrorthing.liquidonline.session.Party

interface PartyInviteNotifierService {
    fun notifyReceiverAndPartyMembersAboutNewInvite(
        party: Party,
        invite: InvitedMember
    )

    fun notifyInviteDeclined(
        party: Party,
        invite: InvitedMember
    )
}

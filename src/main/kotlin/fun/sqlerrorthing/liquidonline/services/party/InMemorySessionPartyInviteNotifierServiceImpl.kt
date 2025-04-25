package `fun`.sqlerrorthing.liquidonline.services.party

import `fun`.sqlerrorthing.liquidonline.extensions.id
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacketToMembers
import `fun`.sqlerrorthing.liquidonline.extensions.toInvitedMemberDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CNewPartyMemberInvite
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyInviteDeclined
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyInviteReceived
import `fun`.sqlerrorthing.liquidonline.session.InvitedMember
import `fun`.sqlerrorthing.liquidonline.session.Party
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class InMemorySessionPartyInviteNotifierServiceImpl: PartyInviteNotifierService {
    @Async
    override fun notifyReceiverAndPartyMembersAboutNewInvite(
        party: Party,
        invite: InvitedMember
    ) {
        invite.invited.sendPacket(
            S2CPartyInviteReceived.builder()
                .inviteUuid(invite.uuid)
                .senderId(invite.sender.id)
                .partyName(party.name)
                .build()
        )

        party.sendPacketToMembers(
            S2CNewPartyMemberInvite.builder()
                .invitedMember(invite.toInvitedMemberDto())
                .build()
        )
    }

    @Async
    override fun notifyInviteDeclined(
        party: Party,
        invite: InvitedMember
    ) {
        party.sendPacketToMembers(
            S2CPartyInviteDeclined.builder()
                .invitedMember(invite.toInvitedMemberDto())
                .build()
        )
    }

}
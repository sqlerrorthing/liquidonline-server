package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.extensions.*
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyInviteRevoked
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyKicked
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyMemberJoined
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyMemberLeaved
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyOwnerTransferred
import `fun`.sqlerrorthing.liquidonline.services.PartyNotifierService
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import org.springframework.stereotype.Service

@Service
class InMemoryPartyNotifierService : PartyNotifierService {
    override fun notifyPartyMemberJoined(party: Party, joinedMember: PartyMember) {
        S2CPartyMemberJoined.builder()
            .member(joinedMember.toPartyMemberDto())
            .build()
        .apply {
            party.sendPacketToMembers { partyMember ->
                takeIf { joinedMember != partyMember }
            }
        }
    }

    override fun notifyPartyMemberLeaved(party: Party, leavedMember: PartyMember) {
        party.sendPacketToMembers {
            S2CPartyMemberLeaved.builder()
                .memberId(leavedMember.id)
                .build()
        }
    }

    override fun notifyRevokedAllPartyInvites(party: Party) {
        party.sendPacketToInvitedMembers { invite ->
            S2CPartyInviteRevoked.builder()
                .inviteUuid(invite.uuid)
                .build()
        }
    }

    override fun notifyPartyOwnerTransferred(party: Party, newOwner: PartyMember) {
        party.sendPacketToMembers(
            S2CPartyOwnerTransferred.builder()
                .newOwnerId(newOwner.id)
                .build()
        )
    }

    override fun notifyKickedMember(member: PartyMember, reason: S2CPartyKicked.Reason) {
        member.sendPacket(
            S2CPartyKicked.builder()
                .reason(reason)
                .build()
        )
    }
}

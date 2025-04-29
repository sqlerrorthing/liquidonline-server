package `fun`.sqlerrorthing.liquidonline.services.party

import `fun`.sqlerrorthing.liquidonline.dto.play.MarkerDto
import `fun`.sqlerrorthing.liquidonline.extensions.*
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.*
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.*

@Service
@Suppress("TooManyFunctions")
class InMemorySessionsPartyNotifierService : PartyNotifierService {
    @Async
    override fun notifyPartyMemberJoined(party: Party, inviteUuid: UUID?, joinedMember: PartyMember) {
        S2CPartyMemberJoined.builder()
            .inviteUuid(inviteUuid)
            .member(joinedMember.toPartyMemberDto())
            .build()
        .apply {
            party.sendPacketToMembers { partyMember ->
                takeIf { joinedMember != partyMember }
            }
        }
    }

    @Async
    override fun notifyPartyMemberLeaved(party: Party, leavedMember: PartyMember) {
        party.sendPacketToMembers {
            S2CPartyMemberLeaved.builder()
                .memberId(leavedMember.id)
                .build()
        }
    }

    @Async
    override fun notifyRevokedAllPartyInvites(party: Party) {
        party.sendPacketToInvitedMembers { invite ->
            S2CPartyInviteRevoked.builder()
                .inviteUuid(invite.uuid)
                .build()
        }
    }

    @Async
    override fun notifyPartyOwnerTransferred(party: Party, newOwner: PartyMember) {
        party.sendPacketToMembers(
            S2CPartyOwnerTransferred.builder()
                .newOwnerId(newOwner.id)
                .build()
        )
    }

    @Async
    override fun notifyKickedMember(member: PartyMember, reason: S2CPartyKicked.Reason) {
        member.sendPacket(
            S2CPartyKicked.builder()
                .reason(reason)
                .build()
        )
    }

    @Async
    override fun notifyPartyMembersPartyDisband(party: Party) {
        party.sendPacketToMembers(
            S2CPartyKicked.builder()
                .reason(S2CPartyKicked.Reason.DISBANDED)
                .build()
        )
    }

    @Async
    override fun notifyPartyMemberMinecraftUsernameUpdate(party: Party, member: PartyMember) {
        S2CPartyMemberStatusUpdate.builder()
            .memberId(member.id)
            .minecraftUsername(member.userSession.minecraftUsername)
            .build()
        .apply {
            party.sendPacketToMembers { partyMember ->
                takeIf { member != partyMember }
            }
        }
    }

    @Async
    override fun notifyPartyMemberUsernameUpdate(party: Party, member: PartyMember) {
        S2CPartyMemberStatusUpdate.builder()
            .memberId(member.id)
            .username(member.userSession.user.username)
            .build()
        .apply {
            party.sendPacketToMembers { partyMember ->
                takeIf { member != partyMember }
            }
        }
    }

    @Async
    override fun notifyPartyMemberSkinUpdate(party: Party, member: PartyMember) {
        S2CPartyMemberStatusUpdate.builder()
            .memberId(member.id)
            .skin(member.userSession.skin)
            .build()
        .apply {
            party.sendPacketToMembers { partyMember ->
                takeIf { member != partyMember }
            }
        }
    }

    @Async
    override fun notifyPartyMemberPlayDataUpdate(
        party: Party,
        member: PartyMember
    ) {
        S2CPartyMemberPlayUpdate.builder()
            .memberId(member.id)
            .data(member.playData)
            .build()
        .apply {
            party.sendPacketToMembers { partyMember ->
                takeIf { member != partyMember }
            }
        }
    }

    @Async
    override fun notifyNewMarker(
        party: Party,
        member: PartyMember,
        marker: MarkerDto
    ) {
        party.sendPacketToMembers(
            S2CPartyNewMarker.builder()
                .memberId(member.id)
                .marker(marker)
                .build()
        )
    }
}

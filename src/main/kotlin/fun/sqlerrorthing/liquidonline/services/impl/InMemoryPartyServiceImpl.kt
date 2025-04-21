package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.extensions.createPartyMember
import `fun`.sqlerrorthing.liquidonline.extensions.id
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacketToInvitedMembers
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacketToMembers
import `fun`.sqlerrorthing.liquidonline.extensions.toPartyMemberDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyInviteRevoked
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyKicked
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyMemberJoined
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyMemberLeaved
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyOwnerTransferred
import `fun`.sqlerrorthing.liquidonline.services.PartyService
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

@Service
class InMemoryPartyServiceImpl : PartyService {
    private val parties: MutableList<Party> = CopyOnWriteArrayList()

    override fun createParty(
        name: String,
        creator: UserSession,
        playData: PlayDto?,
    ): Party {
        val member = creator.createPartyMember(playData = playData)

        return Party.builder()
            .uuid(UUID.randomUUID())
            .name(name)
            .owner(member)
            .build()
        .apply {
            creator.activeParty = this
            this.members.add(member)
            parties.add(this)
        }
    }

    override fun joinPartyMember(
        party: Party,
        user: UserSession,
        playData: PlayDto?
    ): PartyMember {
        val member = user.createPartyMember(
            colorPosition = party.members.size-1,
            playData = playData
        )

        val notifyPacket = S2CPartyMemberJoined.builder()
            .member(member.toPartyMemberDto())
            .build()

        party.sendPacketToMembers { notifyPacket }
        party.members.add(member)

        user.activeParty = party

        return member
    }

    override fun disboundAndNotifyPartyMembers(
        party: Party
    ) {
        val kickPacket = S2CPartyKicked.builder()
            .reason(S2CPartyKicked.Reason.DISBANDED)
            .build()

        party.sendPacketToMembers { kickPacket }
        removeParty(party)
    }

    override fun kickPartyMember(
        party: Party,
        member: PartyMember
    ) {
        removePartyMember(party, member)

        member.sendPacket(
            S2CPartyKicked.builder()
                .reason(S2CPartyKicked.Reason.KICKED)
                .build()
        )
    }

    fun removePartyMember(
        party: Party,
        member: PartyMember,
    ) {
        member.userSession.activeParty = null
        party.members.removeIf { it == member }

        if (party.members.isEmpty()) {
            removeParty(party)
            return
        } else if (party.owner == member) {
            transferPartyOwnership(party, party.members[0])
        }

        S2CPartyMemberLeaved.builder()
            .memberId(member.id)
            .build()
        .let {
            party.sendPacketToMembers { _ -> it }
        }
    }

    override fun transferPartyOwnership(
        party: Party,
        newOwner: PartyMember
    ) {
        party.owner = newOwner

        S2CPartyOwnerTransferred.builder()
            .newOwnerId(newOwner.id)
            .build()
        .let {
            party.sendPacketToMembers { _ -> it }
        }
    }

    private fun removeParty(
        party: Party
    ) {
        party.sendPacketToInvitedMembers { invite ->
            S2CPartyInviteRevoked.builder()
                .inviteUuid(invite.uuid)
                .build()
        }

        party.members.forEach {
            member -> member.userSession.activeParty = null
        }

        party.members.clear()

        parties.removeIf { it == party }
    }
}
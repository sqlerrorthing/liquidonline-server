package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.extensions.*
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.*
import `fun`.sqlerrorthing.liquidonline.services.PartyNotifierService
import `fun`.sqlerrorthing.liquidonline.services.PartyService
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Service
class InMemoryPartyServiceWithNotifierServiceImpl(
    private val partyNotifierService: PartyNotifierService
) : PartyService {
    private val parties: MutableList<Party> = CopyOnWriteArrayList()

    override fun createParty(
        name: String,
        baseMember: UserSession,
        playData: PlayDto?,
    ): Party {
        val member = baseMember.createPartyMember(playData = playData)

        return Party.builder()
            .uuid(UUID.randomUUID())
            .name(name)
            .owner(member)
            .build()
        .apply {
            baseMember.activeParty = this
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

        party.members.add(member)
        user.activeParty = party

        partyNotifierService.notifyPartyMemberJoined(party, member)
        return member
    }

    override fun disbandmentNotifyPartyMembers(
        party: Party
    ) {
        party.members.forEach {
            partyNotifierService.notifyKickedMember(it, S2CPartyKicked.Reason.DISBANDED)
        }

        removeParty(party)
    }

    override fun kickPartyMember(
        party: Party,
        member: PartyMember
    ) {
        removePartyMember(party, member)

        partyNotifierService.notifyKickedMember(member, S2CPartyKicked.Reason.KICKED)
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

        partyNotifierService.notifyPartyMemberLeaved(party, member)
    }

    override fun transferPartyOwnership(
        party: Party,
        newOwner: PartyMember
    ) {
        party.owner = newOwner
        partyNotifierService.notifyPartyOwnerTransferred(party, newOwner)
    }

    private fun removeParty(
        party: Party
    ) {
        party.members.forEach { it.userSession.activeParty = null }
        party.members.clear()

        partyNotifierService.notifyRevokedAllPartyInvites(party)
        party.invitedMembers.clear()

        parties.removeIf { it == party }
    }
}
package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.extensions.*
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.*
import `fun`.sqlerrorthing.liquidonline.services.PartyNotifierService
import `fun`.sqlerrorthing.liquidonline.services.PartyService
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Service
class InMemoryPartyServiceWithNotifierServiceImpl(
    private val partyNotifierService: PartyNotifierService
) : PartyService {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val parties: MutableList<Party> = CopyOnWriteArrayList()

    override fun createParty(
        name: String,
        baseMember: UserSession,
        playData: PlayDto?,
    ): Party {
        val member = baseMember.createPartyMember(playData = playData)

        val party = Party.builder()
            .uuid(UUID.randomUUID())
            .name(name)
            .owner(member)
            .build()
        .apply {
            baseMember.activeParty = this
            this.members.add(member)
            parties.add(this)
        }

        logger.info("Party created: name='{}', owner='{}'", name, member.userSession.user.username)
        return party
    }

    override fun joinPartyMember(
        party: Party,
        user: UserSession,
        playData: PlayDto?
    ): PartyMember {
        require(party.hasMembers()) {
            "At least one participant must be in the party. " +
            "A party without a single participant cannot exist."
        }

        require(user.activeParty != party) {
            "User is already in this party"
        }

        val member = user.createPartyMember(
            colorPosition = party.members.size-1,
            playData = playData
        )

        party.members.add(member)
        user.activeParty = party

        partyNotifierService.notifyPartyMemberJoined(party, member)
        logger.info("User '{}' joined party '{}'", user.user.username, party.name)
        return member
    }

    override fun disbandmentNotifyPartyMembers(
        party: Party
    ) {
        party.members.forEach {
            partyNotifierService.notifyKickedMember(it, S2CPartyKicked.Reason.DISBANDED)
        }

        logger.info("Party '{}' disbanded by owner '{}'", party.name, party.owner.userSession.user.username)
        removeParty(party)
    }

    override fun kickPartyMember(
        party: Party,
        member: PartyMember
    ) {
        removePartyMember(party, member)
        partyNotifierService.notifyKickedMember(member, S2CPartyKicked.Reason.KICKED)

        logger.info("User '{}' was kicked from party '{}'", member.userSession.user.username, party.name)
    }

    fun removePartyMember(
        party: Party,
        member: PartyMember,
    ) {
        require(member.userSession.activeParty == party) {
            "The user is not a member of this party"
        }

        val wasOwner = party.owner == member

        if (party.members.size - 1 == 0) {
            logger.info(
                "Last member '{}' left party '{}'. Party will be removed.",
                member.userSession.user.username,
                party.name
            )
            removeParty(party)
            return
        }

        member.userSession.activeParty = null
        party.members.remove(member)

        if (wasOwner) {
            transferPartyOwnership(party, party.members.first())
        }

        partyNotifierService.notifyPartyMemberLeaved(party, member)
        logger.info("User '{}' left party '{}'", member.userSession.user.username, party.name)
    }

    override fun transferPartyOwnership(
        party: Party,
        newOwner: PartyMember
    ) {
        party.owner = newOwner
        partyNotifierService.notifyPartyOwnerTransferred(party, newOwner)

        logger.info("Ownership of party '{}' transferred to '{}'", party.name, newOwner.userSession.user.username)
    }

    private fun removeParty(
        party: Party
    ) {
        party.members.forEach { it.userSession.activeParty = null }
        party.members.clear()

        partyNotifierService.notifyRevokedAllPartyInvites(party)
        party.invitedMembers.clear()

        parties.remove(party)

        logger.info("Party '{}' fully removed", party.name)
    }
}
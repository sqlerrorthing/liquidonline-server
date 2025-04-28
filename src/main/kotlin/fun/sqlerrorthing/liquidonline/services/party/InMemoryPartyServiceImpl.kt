package `fun`.sqlerrorthing.liquidonline.services.party

import `fun`.sqlerrorthing.liquidonline.dto.party.PartyDto
import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.extensions.*
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyKicked
import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendshipService
import `fun`.sqlerrorthing.liquidonline.services.session.SessionStorageService
import `fun`.sqlerrorthing.liquidonline.session.InvitedMember
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.require
import `fun`.sqlerrorthing.liquidonline.utils.requireNotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Service
@Suppress("TooManyFunctions")
class InMemoryPartyServiceImpl(
    private val partyNotifierService: PartyNotifierService,
    private val sessionStorageService: SessionStorageService,
    private val friendshipService: FriendshipService,
    private val partyInviteNotifierService: PartyInviteNotifierService
) : PartyService {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val parties: MutableList<Party> = CopyOnWriteArrayList()

    override fun createParty(
        name: String,
        baseMember: UserSession,
        playData: PlayDto?,
    ): Party {
        require(baseMember.activeParty == null) {
            AlreadyInPartyException
        }

        val member = baseMember.createPartyMember(playData = playData)

        val party = Party.builder()
            .uuid(UUID.randomUUID())
            .name(name)
            .owner(member)
            .build()
        .apply {
            baseMember.activeParty = this to member
            this.members.add(member)
            parties.add(this)
        }

        logger.info("Party created: name='{}', owner='{}'", name, member.userSession.user.username)
        return party
    }

    override fun joinPartyMember(
        party: Party,
        user: UserSession,
        inviteUuid: UUID?,
        playData: PlayDto?
    ): PartyMember {
        require(party.hasMembers()) {
            PartyHasNoMembers
        }

        require(user.activeParty?.first != party) {
            AlreadyInPartyException
        }

        val member = user.createPartyMember(
            colorPosition = party.members.size-1,
            playData = playData
        )

        party.members.add(member)
        user.activeParty = party to member

        partyNotifierService.notifyPartyMemberJoined(party, inviteUuid, member)
        logger.info("User '{}' joined party '{}'", user.user.username, party.name)
        return member
    }

    override fun disbandPartyRequested(
        party: Party,
        requester: PartyMember
    ) {
        require(party.owner == requester) {
            NotEnoughPartyPermissionsExceptions
        }

        disbandParty(party)
    }

    override fun createInvite(
        party: Party,
        sender: PartyMember,
        receiverUsername: String
    ): InvitedMember {
        val receiver = requireNotNull(sessionStorageService.findUserSession(receiverUsername)) {
            UserNotFoundException
        }

        return createInvite(party, sender, receiver)
    }

    override fun createInvite(
        party: Party,
        sender: PartyMember,
        receiver: UserSession
    ): InvitedMember {
        require(party.isInParty(sender)) {
            MemberInAnotherPartyException
        }

        require(party.owner == sender || party.isPublic) {
            NotEnoughPartyPermissionsExceptions
        }

        require(friendshipService.areFriends(sender.userSession.user, receiver.user)) {
            NotEnoughPartyPermissionsExceptions
        }

        require(!party.isInParty(receiver)) {
            AlreadyInPartyException
        }

        require(!party.isInvited(receiver)) {
            AlreadyInvitedException
        }

        return InvitedMember.builder()
            .invited(receiver)
            .sender(sender.userSession)
            .build()
        .also {
            party.invitedMembers.add(it)
            partyInviteNotifierService.notifyReceiverAndPartyMembersAboutNewInvite(party, it)
        }
    }

    override fun disbandParty(
        party: Party
    ) {
        partyNotifierService.notifyPartyMembersPartyDisband(party)

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

    override fun kickPartyMember(
        party: Party,
        requester: PartyMember,
        memberId: Int
    ) {
        require(party.owner == requester) {
            NotEnoughPartyPermissionsExceptions
        }

        val member = requireNotNull(party.findMemberById(memberId)) {
            PartyMemberNotFoundException
        }

        kickPartyMember(party, member)
    }

    override fun transferPartyOwnership(
        party: Party,
        requester: PartyMember,
        newOwnerId: Int
    ) {
        require(party.owner == requester) {
            NotEnoughPartyPermissionsExceptions
        }

        val member = requireNotNull(party.findMemberById(newOwnerId)) {
            PartyMemberNotFoundException
        }

        transferPartyOwnership(party, member)
    }

    override fun removePartyMember(
        party: Party,
        member: PartyMember,
    ) {
        require(party.isInParty(member)) {
            MemberInAnotherPartyException
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

    override fun sessionDisconnected(session: UserSession) {
        parties.mapNotNull { party ->
            party.invitedMembers
                .find { it.invited == session }
                ?.let { party to it }
        }.forEach { (party, invite) ->
            inviteDeclined(party, session, invite)
        }

        session.activeParty?.let { (party, member) ->
            removePartyMember(party, member)
        }
    }

    override fun inviteDeclined(
        party: Party,
        requester: UserSession,
        invite: InvitedMember
    ) {
        if (requester == invite.sender || requester == party.owner) {
            partyInviteNotifierService.notifyReceiverInviteRevoked(invite)
        } else {
            require(requester == invite.invited) {
                InviteNotFoundException
            }
        }

        party.invitedMembers.remove(invite)
        partyInviteNotifierService.notifyInviteDeclined(party, invite)
    }

    override fun inviteDeclined(requester: UserSession, inviteUuid: UUID) {
        val (party, invite) = parties.findPartyByInviteUuid(inviteUuid)
            ?: throw InviteNotFoundException

        inviteDeclined(party, requester, invite)
    }

    override fun inviteAccepted(
        inviteUuid: UUID,
        requester: UserSession,
        playData: PlayDto?
    ): PartyDto {
        val (party, invite) = parties.findPartyByInviteUuid(inviteUuid)
            ?: throw InviteNotFoundException

        inviteAccepted(party, requester, invite, playData)
        return party.toPartyDto()
    }

    override fun inviteAccepted(
        party: Party,
        requester: UserSession,
        invite: InvitedMember,
        playData: PlayDto?
    ) {
        require(requester == invite.invited) {
            InviteNotFoundException
        }

        require(requester.activeParty == null) {
            MemberInAnotherPartyException
        }

        require(party.isNextPartyMemberSlotFree) {
            party.invitedMembers.remove(invite)
            partyInviteNotifierService.notifyInviteDeclined(party, invite)

            PartyMembersLimitException
        }

        party.invitedMembers.remove(invite)

        joinPartyMember(
            party,
            requester,
            invite.uuid,
            playData
        )
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

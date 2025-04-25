package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.dto.party.PartyDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.session.InvitedMember
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession

fun Party.toPartyDto(): PartyDto {
    return PartyDto.builder()
        .id(this.uuid)
        .name(this.name)
        .partyPublic(this.isPublic)
        .ownerId(this.owner.userSession.user.id)
        .members(this.members.map { it.toPartyMemberDto() })
        .invitedMembers(this.invitedMembers.map { it.toInvitedMemberDto() })
        .build()
}

fun Party.sendPacketToMembers(builder: (member: PartyMember) -> Packet?) {
    members.forEach { member ->
        builder(member)?.let { packet ->
            member.sendPacket(packet)
        }
    }
}

fun Party.sendPacketToMembers(packet: Packet?) {
    packet?.let {
        sendPacketToMembers { packet }
    }
}

fun Party.isInParty(member: PartyMember): Boolean {
    return members.find { it == member } != null
}

fun Party.isInParty(user: UserSession): Boolean {
    return user.activeParty?.first == this
}

fun Party.isInvited(session: UserSession): Boolean {
    return invitedMembers.find { it.invited == session } != null
}

fun Party.hasMembers(): Boolean = members.isNotEmpty()

fun Party.sendPacketToInvitedMembers(builder: (invite: InvitedMember) -> Packet?) {
    invitedMembers.forEach { invite ->
        builder(invite)?.let { packet ->
            invite.invited.sendPacket(packet)
        }
    }
}

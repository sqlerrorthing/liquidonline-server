@file:Suppress("TooManyFunctions")
package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.dto.party.PartyDto
import `fun`.sqlerrorthing.liquidonline.dtos.PartySettingsDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.party.C2SPartyUpdateSettings
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartySettingsUpdated
import `fun`.sqlerrorthing.liquidonline.session.InvitedMember
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import java.util.*

fun Party.toPartyDto(): PartyDto {
    return PartyDto.builder()
        .id(this.uuid)
        .name(this.name)
        .partyPublic(this.isPublic)
        .maxMembers(this.maxMembers)
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

fun C2SPartyUpdateSettings.toPartySettingsDto(): PartySettingsDto {
    return PartySettingsDto.builder()
        .partyPublic(this.isPartyPublic)
        .build()
}

fun Party.toPartySettingsDto(): PartySettingsDto {
    return PartySettingsDto.builder()
        .partyPublic(this.isPublic)
        .build()
}

fun PartySettingsDto.applyToParty(party: Party) {
    party.isPublic = this.isPartyPublic
}

fun PartySettingsDto.toUpdatedSettingsPacket(): S2CPartySettingsUpdated {
    return S2CPartySettingsUpdated.builder()
        .partyPublic(this.isPartyPublic)
        .build()
}

fun Party.isInParty(member: PartyMember): Boolean {
    return members.find { it == member } != null
}

fun Party.isInParty(user: UserSession): Boolean {
    return user.activeParty?.first == this
}

fun Party.findMemberById(memberId: Int): PartyMember? {
    return members.find { it.id == memberId }
}

val Party.isNextPartyMemberSlotFree get() = members.size + 1 <= maxMembers

fun List<Party>.findPartyByInviteUuid(inviteUuid: UUID): Pair<Party, InvitedMember>? {
    forEach { party ->
        party.invitedMembers.firstOrNull { invite ->
            invite.uuid == inviteUuid
        }?.let {
            return party to it
        }
    }

    return null
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

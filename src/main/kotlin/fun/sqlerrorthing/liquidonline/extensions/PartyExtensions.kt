package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.dto.party.PartyDto
import `fun`.sqlerrorthing.liquidonline.session.Party

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
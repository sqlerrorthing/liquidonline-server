package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.dto.party.InvitedMemberDto
import `fun`.sqlerrorthing.liquidonline.session.InvitedMember

fun InvitedMember.toInvitedMemberDto(): InvitedMemberDto {
    return InvitedMemberDto.builder()
        .inviteUuid(this.uuid)
        .username(this.invited.user.username)
        .senderId(this.sender.id)
        .build()
}

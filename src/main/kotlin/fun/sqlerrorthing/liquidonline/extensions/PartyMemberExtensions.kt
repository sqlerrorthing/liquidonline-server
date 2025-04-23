package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.dto.party.PartyMemberDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import java.util.Base64

fun PartyMember.sendPacket(packet: Packet) {
    userSession.sendPacket(packet)
}

val PartyMember.id get() = this.userSession.user.id

fun PartyMember.toPartyMemberDto(): PartyMemberDto {
    return PartyMemberDto.builder()
        .memberId(this.userSession.user.id)
        .username(this.userSession.user.name)
        .minecraftUsername(this.userSession.minecraftUsername)
        .skin(Base64.getEncoder().encodeToString(this.userSession.skin))
        .color(this.color)
        .playData(this.playData)
        .build()
}

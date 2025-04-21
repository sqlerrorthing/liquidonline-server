package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.dto.UserAccountDto
import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.services.FriendshipService
import `fun`.sqlerrorthing.liquidonline.services.SessionStorageService
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.Colors
import `fun`.sqlerrorthing.liquidonline.utils.SpringContextHolder

private val friendshipService by lazy {
    requireNotNull(SpringContextHolder.getBean(FriendshipService::class.java))
}

private val sessionStorageService by lazy {
    requireNotNull(SpringContextHolder.getBean(SessionStorageService::class.java))
}

private val colors by lazy {
    requireNotNull(SpringContextHolder.getBean(Colors::class.java))
}

fun UserSession.sendPacketToFriends(builder: (friend: UserSession) -> Packet) {
    friendshipService.findUserFriends(this.user)
        .mapNotNull { sessionStorageService.findUserSession(it) }
        .forEach { friend ->
            friend.sendPacket(builder(friend))
        }
}

fun UserSession.createPartyMember(colorPosition: Int = 0, playData: PlayDto? = null): PartyMember {
    return PartyMember.builder()
        .userSession(this)
        .color(colors.getColor(colorPosition))
        .playData(playData)
        .build()
}

fun UserSession.sendPacketToPartyMembers(builder: (member: PartyMember) -> Packet) {
    activeParty?.members?.forEach { member ->
       if (member.userSession.user.id != this.user.id) {
           member.sendPacket(builder(member))
       }
    }
}

fun PartyMember.sendPacketToPartyMembers(builder: (member: PartyMember) -> Packet) {
    userSession.sendPacketToPartyMembers(builder)
}

fun UserSession.sendPacket(packet: Packet) {
    wsSession.sendPacket(packet)
}

fun UserEntity.toDto(): UserAccountDto = UserAccountDto.builder()
    .id(this.id)
    .username(this.username)
    .build()

fun UserEntity.toFriendDto(): FriendDto = FriendDto.builder()
    .id(this.id)
    .username(this.username)
    .online(false)
    .lastOnline(this.lastLogin)
    .build()
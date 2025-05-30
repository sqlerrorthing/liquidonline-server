package `fun`.sqlerrorthing.liquidonline.extensions

import com.fasterxml.jackson.databind.ObjectMapper
import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.dto.UserAccountDto
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.services.FriendshipService
import `fun`.sqlerrorthing.liquidonline.services.WebSocketSessionStorageService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.SpringContextHolder

private val friendshipService by lazy {
    SpringContextHolder.getBean(FriendshipService::class.java)!!
}

private val webSocketSessionStorageService by lazy {
    SpringContextHolder.getBean(WebSocketSessionStorageService::class.java)!!
}

fun UserSession.sendPacketToFriends(builder: (friend: UserSession) -> Packet) {
    friendshipService.findUserFriends(this.user)
        .mapNotNull { webSocketSessionStorageService.findUserSession(it) }
        .forEach { friend ->
            friend.sendMessage(builder(friend))
        }
}

fun UserSession.sendMessage(packet: Packet) {
    wsSession.sendMessage(packet)
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
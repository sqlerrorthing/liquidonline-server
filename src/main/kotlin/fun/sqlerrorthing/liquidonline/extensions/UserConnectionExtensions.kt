package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.session.UserSession

fun UserSession.toFriendDto(): FriendDto = FriendDto.builder()
    .id(this.user.id)
    .username(this.user.username)
    .online(true)
    .minecraftUsername(this.minecraftUsername)
    .skin(this.skin)
    .server(this.server)
    .build()

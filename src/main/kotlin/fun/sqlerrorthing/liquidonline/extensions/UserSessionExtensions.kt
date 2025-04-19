package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.dto.UserAccountDto
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity

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
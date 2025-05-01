package `fun`.sqlerrorthing.liquidonline.extensions

import `fun`.sqlerrorthing.liquidonline.dtos.TokenDto
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity

fun UserEntity.toTokenDto(): TokenDto {
    return TokenDto.builder()
        .token(this.token)
        .build()
}
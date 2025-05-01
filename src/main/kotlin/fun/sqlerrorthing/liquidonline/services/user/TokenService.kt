package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity

interface TokenService {
    fun generateToken(userEntity: UserEntity): String
}
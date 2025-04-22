package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity

interface UserService {
    fun findUserById(id: Int): UserEntity?

    fun findUserByUsername(username: String): UserEntity?

    fun findUserByToken(token: String): UserEntity?

    fun save(user: UserEntity)
}
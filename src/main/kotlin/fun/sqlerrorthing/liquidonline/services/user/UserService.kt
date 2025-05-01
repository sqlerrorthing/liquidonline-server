package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity

interface UserService {
    fun registerUser(username: String, password: String): UserEntity

    fun findUserById(id: Int): UserEntity?

    fun findUserByUsername(username: String): UserEntity?

    fun findUserByToken(token: String): UserEntity?

    fun findUserByUsernameAndPassword(username: String, rawPassword: String): UserEntity?

    fun regenerateUserToken(user: UserEntity)

    fun save(user: UserEntity)
}

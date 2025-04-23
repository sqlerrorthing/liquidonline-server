package `fun`.sqlerrorthing.liquidonline.repository

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository : JpaRepository<UserEntity, Int> {
    fun findByUsername(username: String): UserEntity?

    fun findByUsernameAndPasswordHash(username: String, passwordHash: String): UserEntity?

    fun findByToken(token: String): UserEntity?
}

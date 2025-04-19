package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.repository.UsersRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UsersRepository
) {
    fun findUserByToken(token: String): UserEntity? {
        return repository.findByToken(token)
    }
}
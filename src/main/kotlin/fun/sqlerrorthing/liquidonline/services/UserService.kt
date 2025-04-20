package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.repository.UsersRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val repository: UsersRepository
) {
    @Transactional(readOnly = true)
    fun findUserById(id: Int): UserEntity? {
        return repository.findByIdOrNull(id)
    }

    @Transactional(readOnly = true)
    fun findUserByUsername(username: String): UserEntity? {
        return repository.findByUsername(username)
    }

    @Transactional(readOnly = true)
    fun findUserByToken(token: String): UserEntity? {
        return repository.findByToken(token)
    }

    @Transactional
    fun save(user: UserEntity) {
        repository.save(user)
    }
}
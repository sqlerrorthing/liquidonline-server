package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.repository.UsersRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RepositoryUserService(
    private val repository: UsersRepository
): UserService {
    @Transactional(readOnly = true)
    override fun findUserById(id: Int): UserEntity? {
        return repository.findByIdOrNull(id)
    }

    @Transactional(readOnly = true)
    override fun findUserByUsername(username: String): UserEntity? {
        return repository.findByUsername(username)
    }

    @Transactional(readOnly = true)
    override fun findUserByToken(token: String): UserEntity? {
        return repository.findByToken(token)
    }

    @Transactional
    override fun save(user: UserEntity) {
        repository.save(user)
    }
}

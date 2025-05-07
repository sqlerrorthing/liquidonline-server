package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.exceptions.UsernameAlreadyTakenException
import `fun`.sqlerrorthing.liquidonline.repository.UsersRepository
import `fun`.sqlerrorthing.liquidonline.utils.require
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RepositoryUserServiceImpl(
    private val repository: UsersRepository,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder
): UserService {
    @Transactional
    override fun registerUser(
        username: String,
        password: String
    ): UserEntity {
        require(findUserByUsername(username) == null, UsernameAlreadyTakenException)

        var user = UserEntity.builder()
            .username(username)
            .passwordHash(passwordEncoder.encode(password))
            .build()

        user = repository.save(user) // determine the user id
        user.token = tokenService.generateToken(user)

        return repository.save(user) // save with token
    }

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

    @Transactional(readOnly = true)
    override fun findUserByUsernameAndPassword(
        username: String,
        rawPassword: String
    ): UserEntity? {
        val user = findUserByUsername(username)
            ?: return null

        return if (passwordEncoder.matches(rawPassword, user.passwordHash)) {
            user
        } else {
            null
        }
    }

    @Transactional
    override fun save(user: UserEntity) {
        repository.save(user)
    }

    @Transactional
    override fun regenerateUserToken(user: UserEntity) {
        user.token = tokenService.generateToken(user)
        save(user)
    }
}

package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
) {
    @Transactional(readOnly = true)
    fun findUserFriends(user: UserEntity): List<UserEntity> {
        return friendshipRepository.findUserFriends(user)
    }

    @Transactional(readOnly = true)
    fun areFriends(user1: UserEntity, user2: UserEntity): Boolean {
        return friendshipRepository.areFriends(user1, user2)
    }

    @Transactional(readOnly = true)
    fun findFriendship(user1: UserEntity, user2: UserEntity): FriendshipEntity? {
        return friendshipRepository.findFriendship(user1, user2)
    }

    @Transactional
    fun createFriendship(sender: UserEntity, receiver: UserEntity) {
        FriendshipEntity
            .builder()
            .user1(sender)
            .user2(receiver)
            .build()
        .also { friendshipRepository.save(it) }
    }

    fun brokeFriendship(friendshipEntity: FriendshipEntity) {
        friendshipRepository.delete(friendshipEntity)
    }
}
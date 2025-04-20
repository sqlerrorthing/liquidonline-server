package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRepository
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRequestRepository
import org.springframework.stereotype.Service

@Service
class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
    private val friendshipRequestRepository: FriendshipRequestRepository,
    private val userService: UserService,
) {
    fun findUserFriends(user: UserEntity): List<UserEntity> {
        return friendshipRepository.findUserFriends(user)
    }

    fun areFriends(user1: UserEntity, user2: UserEntity): Boolean {
        return friendshipRepository.areFriends(user1, user2)
    }
}
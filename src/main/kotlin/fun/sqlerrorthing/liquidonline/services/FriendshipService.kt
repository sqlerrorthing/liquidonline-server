package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipEntity
import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2COutgoingFriendRequest
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRepository
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRequestRepository
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
) {
    fun findUserFriends(user: UserEntity): List<UserEntity> {
        return friendshipRepository.findUserFriends(user)
    }

    fun areFriends(user1: UserEntity, user2: UserEntity): Boolean {
        return friendshipRepository.areFriends(user1, user2)
    }

    fun findFriendship(user1: UserEntity, user2: UserEntity): FriendshipEntity? {
        return friendshipRepository.findFriendship(user1, user2)
    }

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
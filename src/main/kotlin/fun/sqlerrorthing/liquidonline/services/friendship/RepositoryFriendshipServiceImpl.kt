package `fun`.sqlerrorthing.liquidonline.services.friendship

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RepositoryFriendshipServiceImpl(
    private val friendshipRepository: FriendshipRepository,
): FriendshipService {
    @Transactional(readOnly = true)
    override fun findUserFriends(user: UserEntity): List<UserEntity> {
        return friendshipRepository.findUserFriends(user)
    }

    @Transactional(readOnly = true)
    override fun areFriends(user1: UserEntity, user2: UserEntity): Boolean {
        return friendshipRepository.areFriends(user1, user2)
    }

    @Transactional(readOnly = true)
    override fun findFriendship(user1: UserEntity, user2: UserEntity): FriendshipEntity? {
        return friendshipRepository.findFriendship(user1, user2)
    }

    @Transactional
    override fun createFriendship(sender: UserEntity, receiver: UserEntity): FriendshipEntity {
        return FriendshipEntity
            .builder()
            .user1(sender)
            .user2(receiver)
            .build()
        .apply { friendshipRepository.save(this) }
    }

    override fun brokeFriendship(friendship: FriendshipEntity) {
        friendshipRepository.delete(friendship)
    }
}

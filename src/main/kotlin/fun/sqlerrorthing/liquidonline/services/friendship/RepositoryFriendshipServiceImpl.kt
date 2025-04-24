package `fun`.sqlerrorthing.liquidonline.services.friendship

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.exceptions.UserNotFoundException
import `fun`.sqlerrorthing.liquidonline.exceptions.WasNoFriendship
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRepository
import `fun`.sqlerrorthing.liquidonline.services.user.UserService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RepositoryFriendshipServiceImpl(
    private val friendshipRepository: FriendshipRepository,
    private val userService: UserService,
    private val friendsNotifierService: FriendsNotifierService,
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

    @Transactional
    override fun brokeFriendship(friendship: FriendshipEntity) {
        friendshipRepository.delete(friendship)
    }

    @Transactional
    override fun brokeFriendship(
        requester: UserSession,
        friendId: Int
    ) {
        val friend = userService.findUserById(friendId) ?: throw UserNotFoundException

        val friendship = findFriendship(requester.user, friend)
            ?: throw WasNoFriendship

        brokeFriendship(friendship)

        friendsNotifierService.notifyFriendWithFriendshipBrokenIfFriendOnline(
            friend,
            requester
        )
    }
}

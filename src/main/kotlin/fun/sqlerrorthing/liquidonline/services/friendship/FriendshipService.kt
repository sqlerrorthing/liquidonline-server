package `fun`.sqlerrorthing.liquidonline.services.friendship

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.exceptions.UserNotFoundException
import `fun`.sqlerrorthing.liquidonline.exceptions.WasNoFriendship
import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface FriendshipService {
    fun findUserFriends(user: UserEntity): List<UserEntity>

    fun areFriends(
        user1: UserEntity,
        user2: UserEntity,
    ): Boolean

    fun findFriendship(
        user1: UserEntity,
        user2: UserEntity,
    ): FriendshipEntity?

    fun createFriendship(
        sender: UserEntity,
        receiver: UserEntity
    ): FriendshipEntity

    fun brokeFriendship(
        friendship: FriendshipEntity
    )

    @Throws(
        UserNotFoundException::class,
        WasNoFriendship::class
    )
    fun brokeFriendship(
        requester: UserSession,
        friendId: Int
    ): UserEntity
}

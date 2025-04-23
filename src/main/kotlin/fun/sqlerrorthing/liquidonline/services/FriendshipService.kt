package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity

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
}

package `fun`.sqlerrorthing.liquidonline.exceptions

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity

object FriendRequestToSelfException : RuntimeException() {
    private fun readResolve(): Any = FriendRequestToSelfException
}

object AlreadyFriendsException : RuntimeException() {
    private fun readResolve(): Any = AlreadyFriendsException
}

class ReverseFriendRequestExistsException(
    val reverseRequest: FriendshipRequestEntity,
    val receiver: UserEntity
) : RuntimeException()

object AlreadyRequestedException : RuntimeException() {
    private fun readResolve(): Any = AlreadyRequestedException
}

object FriendRequestNotFoundException : RuntimeException() {
    private fun readResolve(): Any = FriendRequestNotFoundException
}

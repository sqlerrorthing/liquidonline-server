package `fun`.sqlerrorthing.liquidonline.exceptions

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import java.lang.RuntimeException

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
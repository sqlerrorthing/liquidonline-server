package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.web.socket.WebSocketSession
import kotlin.jvm.Throws

interface FriendshipRequestService {
    fun findBySenderAndReceiver(
        sender: UserEntity,
        receiver: UserEntity
    ): FriendshipRequestEntity?

    fun findAllBySender(
        sender: UserEntity
    ): List<FriendshipRequestEntity>

    fun findAllByReceiver(
        receiver: UserEntity
    ): List<FriendshipRequestEntity>

    fun createFriendRequest(
        sender: UserEntity,
        receiver: UserEntity,
    ): FriendshipRequestEntity

    fun findFriendRequest(
        id: Int
    ): FriendshipRequestEntity?

    fun acceptFriendRequest(
        request: FriendshipRequestEntity
    )

    fun rejectFriendRequest(
        request: FriendshipRequestEntity
    )

    @Throws(
        FriendRequestToSelfException::class,
        AlreadyFriendsException::class,
        UserNotFoundException::class,
        ReverseFriendRequestExistsException::class,
        AlreadyRequestedException::class
    )
    fun sendFriendRequest(
        user: UserEntity,
        receiverUsername: String
    ): FriendshipRequestEntity
}

package `fun`.sqlerrorthing.liquidonline.services.friendship

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.session.UserSession

@Suppress("TooManyFunctions")
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

    fun rejectFriendRequestBySender(
        request: FriendshipRequestEntity
    )

    /**
     * @return the friendship request and
     * `true` if the sender rejected the request. `false` if the receiver of the request rejected it.
     */
    @Throws(
        FriendRequestNotFoundException::class
    )
    fun respondRejectFriendRequest(
        user: UserSession,
        requestId: Int
    ): Pair<FriendshipRequestEntity, Boolean>

    @Throws(
        FriendRequestNotFoundException::class
    )
    fun respondAcceptFriendRequest(
        user: UserSession,
        requestId: Int
    ): FriendshipRequestEntity

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

package `fun`.sqlerrorthing.liquidonline.services.friendship

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface FriendshipRequestsNotifierService {
    fun notifyOutgoingFriendRequestWasAccepted(
        requestId: Int,
        receiver: UserSession,
        newFriend: UserSession
    )

    fun notifyOutgoingFriendRequestWasAcceptedIfReceiverOnline(
        requestId: Int,
        receiver: UserEntity,
        newFriend: UserSession
    )

    fun notifyOutgoingFriendRequestWasRejected(
        requestId: Int,
        sender: UserSession,
        receiver: UserEntity
    )

    fun notifyOutgoingFriendRequestWasRejectedIfSenderOnline(
        request: FriendshipRequestEntity
    )

    fun notifyReceiverNewFriendRequestIfReceiverOnline(
        requestEntity: FriendshipRequestEntity
    )

    fun notifyIncomingFriendRequestRejected(
        requestId: Int,
        receiver: UserSession,
        sender: UserEntity
    )

    fun notifyIncomingFriendRequestRejectedIfReceiverOnline(
        request: FriendshipRequestEntity
    )
}
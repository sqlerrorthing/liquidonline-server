package `fun`.sqlerrorthing.liquidonline.services.friendship

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.onlineSession
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CIncomingFriendRequestRejected
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CNewIncomingFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2COutgoingFriendRequest
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service

@Service
class InMemoryFriendshipRequestsNotifierService: FriendshipRequestsNotifierService {
    override fun notifyOutgoingFriendRequestWasAccepted(
        requestId: Int,
        receiver: UserSession,
        newFriend: UserSession
    ) {
        receiver.sendPacket(
            S2COutgoingFriendRequest.builder()
                .requestId(requestId)
                .to(newFriend.user.username)
                .status(S2COutgoingFriendRequest.Status.ACCEPTED)
                .friend(newFriend.toFriendDto())
                .build()
        )
    }

    override fun notifyOutgoingFriendRequestWasAcceptedIfReceiverOnline(
        requestId: Int,
        receiver: UserEntity,
        newFriend: UserSession
    ) {
        receiver.onlineSession?.let {
            notifyOutgoingFriendRequestWasAccepted(
                requestId,
                it,
                newFriend
            )
        }
    }

    override fun notifyOutgoingFriendRequestWasRejected(
        requestId: Int,
        sender: UserSession,
        receiver: UserEntity
    ) {
        sender.sendPacket(
            S2COutgoingFriendRequest.builder()
                .requestId(requestId)
                .to(receiver.username)
                .status(S2COutgoingFriendRequest.Status.REJECT)
                .build()
        )
    }

    override fun notifyOutgoingFriendRequestWasRejectedIfSenderOnline(
        request: FriendshipRequestEntity,
    ) {
        request.sender.onlineSession?.let {
            notifyOutgoingFriendRequestWasRejected(
                request.id,
                it,
                request.receiver
            )
        }
    }

    override fun notifyReceiverNewFriendRequestIfReceiverOnline(requestEntity: FriendshipRequestEntity) {
        requestEntity.receiver.onlineSession?.sendPacket(
            S2CNewIncomingFriendRequest.builder()
                .from(requestEntity.sender.username)
                .requestId(requestEntity.id)
                .build()
        )
    }

    override fun notifyIncomingFriendRequestRejected(
        requestId: Int,
        receiver: UserSession,
        sender: UserEntity
    ) {
        receiver.sendPacket(
            S2CIncomingFriendRequestRejected.builder()
                .requestId(requestId)
                .from(sender.username)
                .build()
        )
    }

    override fun notifyIncomingFriendRequestRejectedIfReceiverOnline(request: FriendshipRequestEntity) {
        request.receiver.onlineSession?.let {
            notifyIncomingFriendRequestRejected(
                request.id,
                it,
                request.sender
            )
        }
    }
}
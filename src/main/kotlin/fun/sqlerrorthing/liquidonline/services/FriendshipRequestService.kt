package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CNewIncomingFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2COutgoingFriendRequest
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRequestRepository
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class FriendshipRequestService(
    private val friendshipRequestRepository: FriendshipRequestRepository,
    private val friendshipService: FriendshipService
) {
    fun findBySenderAndReceiver(sender: UserEntity, receiver: UserEntity): FriendshipRequestEntity? {
        return friendshipRequestRepository.findBySenderAndReceiver(sender, receiver)
    }

    fun createFriendRequest(sender: UserEntity, receiver: UserEntity, receiverSession: WebSocketSession?): FriendshipRequestEntity {
        var request = FriendshipRequestEntity
            .builder()
            .sender(sender)
            .receiver(receiver)
            .build()

        request = friendshipRequestRepository.save(request)

        receiverSession?.sendMessage(
            S2CNewIncomingFriendRequest
                .builder()
                .from(sender.username)
                .requestId(request.id)
                .build()
        )

        return request
    }

    fun findFriendRequest(
        id: Int
    ): FriendshipRequestEntity? {
        return friendshipRequestRepository.findByIdOrNull(id)
    }

    fun acceptFriendRequest(
        request: FriendshipRequestEntity,
        senderSession: UserSession? = null,
        receiverSession: UserSession? = null
    ) {
        senderSession?.sendMessage(
            S2COutgoingFriendRequest
                .builder()
                .to(request.receiver.username)
                .status(S2COutgoingFriendRequest.Status.ACCEPTED)
                .friend(receiverSession?.toFriendDto() ?: request.receiver.toFriendDto())
                .build()
        )

        friendshipService.createFriendship(
            sender = request.sender,
            receiver = request.receiver,
        )

        friendshipRequestRepository.delete(request)
    }

    fun rejectFriendRequest(
        request: FriendshipRequestEntity,
        senderSession: UserSession? = null,
    ) {
        senderSession?.sendMessage(
            S2COutgoingFriendRequest
                .builder()
                .to(request.receiver.username)
                .status(S2COutgoingFriendRequest.Status.REJECT)
                .build()
        )

        friendshipRequestRepository.delete(request)
    }
}
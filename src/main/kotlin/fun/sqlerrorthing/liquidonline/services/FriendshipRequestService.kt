package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CNewIncomingFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CNewIncomingFriendRequest.S2CNewIncomingFriendRequestBuilder
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRequestRepository
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class FriendshipRequestService(
    private val friendshipRequestRepository: FriendshipRequestRepository,
) {
    fun findBySenderAndReceiver(sender: UserEntity, receiver: UserEntity): FriendshipRequestEntity? {
        return friendshipRequestRepository.findBySenderAndReceiver(sender, receiver)
    }

    fun createFriendRequest(sender: UserEntity, receiver: UserEntity, receiverSession: WebSocketSession?) {
        val request = FriendshipRequestEntity
            .builder()
            .sender(sender)
            .receiver(receiver)
            .build()

        friendshipRequestRepository.save(request)

        receiverSession?.sendMessage(
            S2CNewIncomingFriendRequest
                .builder()
                .from(sender.username)
                .build()
        )
    }
}
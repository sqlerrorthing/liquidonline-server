package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CNewIncomingFriendRequest
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRequestRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.WebSocketSession

@Service
class FriendshipRequestService(
    private val friendshipRequestRepository: FriendshipRequestRepository,
    private val friendshipService: FriendshipService
) {
    @Transactional(readOnly = true)
    fun findBySenderAndReceiver(sender: UserEntity, receiver: UserEntity): FriendshipRequestEntity? {
        return friendshipRequestRepository.findBySenderAndReceiver(sender, receiver)
    }

    @Transactional(readOnly = true)
    fun findAllBySender(sender: UserEntity): List<FriendshipRequestEntity> {
        return friendshipRequestRepository.findAllBySender(sender)
    }

    @Transactional(readOnly = true)
    fun findAllByReceiver(receiver: UserEntity): List<FriendshipRequestEntity> {
        return friendshipRequestRepository.findAllByReceiver(receiver)
    }

    @Transactional
    fun createFriendRequest(sender: UserEntity, receiver: UserEntity, receiverSession: WebSocketSession?): FriendshipRequestEntity {
        var request = FriendshipRequestEntity
            .builder()
            .sender(sender)
            .receiver(receiver)
            .build()

        request = friendshipRequestRepository.save(request)

        receiverSession?.sendPacket(
            S2CNewIncomingFriendRequest
                .builder()
                .from(sender.username)
                .requestId(request.id)
                .build()
        )

        return request
    }

    @Transactional(readOnly = true)
    fun findFriendRequest(
        id: Int
    ): FriendshipRequestEntity? {
        return friendshipRequestRepository.findByIdOrNull(id)
    }

    @Transactional
    fun acceptFriendRequest(
        request: FriendshipRequestEntity
    ) {
        friendshipService.createFriendship(
            sender = request.sender,
            receiver = request.receiver,
        )

        friendshipRequestRepository.delete(request)
    }

    @Transactional
    fun rejectFriendRequest(
        request: FriendshipRequestEntity
    ) {
        friendshipRequestRepository.delete(request)
    }
}
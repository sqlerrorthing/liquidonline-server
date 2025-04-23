package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CNewIncomingFriendRequest
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRequestRepository
import `fun`.sqlerrorthing.liquidonline.services.FriendshipRequestService
import `fun`.sqlerrorthing.liquidonline.services.FriendshipService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.WebSocketSession

@Service
class RepositoryFriendshipRequestServiceImpl(
    private val friendshipRequestRepository: FriendshipRequestRepository,
    private val friendshipService: FriendshipService
): FriendshipRequestService {
    @Transactional(readOnly = true)
    override fun findBySenderAndReceiver(sender: UserEntity, receiver: UserEntity): FriendshipRequestEntity? {
        return friendshipRequestRepository.findBySenderAndReceiver(sender, receiver)
    }

    @Transactional(readOnly = true)
    override fun findAllBySender(sender: UserEntity): List<FriendshipRequestEntity> {
        return friendshipRequestRepository.findAllBySender(sender)
    }

    @Transactional(readOnly = true)
    override fun findAllByReceiver(receiver: UserEntity): List<FriendshipRequestEntity> {
        return friendshipRequestRepository.findAllByReceiver(receiver)
    }

    @Transactional
    override fun createFriendRequestAndNotifyReceiverIfOnline(
        sender: UserEntity,
        receiver: UserEntity,
        receiverSession: WebSocketSession?
    ): FriendshipRequestEntity {
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
    override fun findFriendRequest(
        id: Int
    ): FriendshipRequestEntity? {
        return friendshipRequestRepository.findByIdOrNull(id)
    }

    @Transactional
    override fun acceptFriendRequest(
        request: FriendshipRequestEntity
    ) {
        friendshipService.createFriendship(
            sender = request.sender,
            receiver = request.receiver,
        )

        friendshipRequestRepository.delete(request)
    }

    @Transactional
    override fun rejectFriendRequest(
        request: FriendshipRequestEntity
    ) {
        friendshipRequestRepository.delete(request)
    }
}

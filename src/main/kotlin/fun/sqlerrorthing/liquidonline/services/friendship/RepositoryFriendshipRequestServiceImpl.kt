package `fun`.sqlerrorthing.liquidonline.services.friendship

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.extensions.onlineSession
import `fun`.sqlerrorthing.liquidonline.repository.FriendshipRequestRepository
import `fun`.sqlerrorthing.liquidonline.services.user.UserService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Suppress("TooManyFunctions")
class RepositoryFriendshipRequestServiceImpl(
    private val friendshipRequestRepository: FriendshipRequestRepository,
    private val friendshipService: FriendshipService,
    private val userService: UserService,
    private val friendshipRequestNotifierService: FriendshipRequestNotifierService
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
    override fun createFriendRequest(
        sender: UserEntity,
        receiver: UserEntity,
    ): FriendshipRequestEntity {
        return FriendshipRequestEntity
            .builder()
            .sender(sender)
            .receiver(receiver)
            .build()
        .let {
            friendshipRequestRepository.save(it)
        }
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

        request.sender.onlineSession?.let {
            friendshipRequestNotifierService.notifyOutgoingFriendRequestWasAcceptedIfReceiverOnline(
                request.id,
                request.receiver,
                it
            )
        }

        friendshipRequestRepository.delete(request)
    }

    @Transactional
    override fun rejectFriendRequest(
        request: FriendshipRequestEntity
    ) {
        friendshipRequestNotifierService.notifyOutgoingFriendRequestWasRejectedIfSenderOnline(
            request
        )

        friendshipRequestRepository.delete(request)
    }

    @Transactional
    override fun sendFriendRequest(user: UserEntity, receiverUsername: String): FriendshipRequestEntity {
        if (user.username == receiverUsername) {
            throw FriendRequestToSelfException
        }

        val receiver = userService.findUserByUsername(receiverUsername) ?: throw UserNotFoundException

        if (friendshipService.areFriends(user, receiver)) {
            throw AlreadyFriendsException
        }

        findBySenderAndReceiver(receiver, user)?.let { request ->
            throw ReverseFriendRequestExistsException(request, receiver)
        }

        findBySenderAndReceiver(user, receiver)?.let {
            throw AlreadyRequestedException
        }

        return createFriendRequest(
            user,
            receiver
        ).apply {
            friendshipRequestNotifierService.notifyReceiverNewFriendRequestIfReceiverOnline(
                this
            )
        }
    }

    override fun rejectFriendRequestBySender(request: FriendshipRequestEntity) {
        friendshipRequestRepository.delete(request)
        friendshipRequestNotifierService.notifyIncomingFriendRequestRejectedIfReceiverOnline(request)
    }

    @Transactional
    override fun respondRejectFriendRequest(
        user: UserSession,
        requestId: Int,
    ): Pair<FriendshipRequestEntity, Boolean> {
        val request = findFriendRequest(requestId)
            ?: throw FriendRequestNotFoundException

        if (request.receiver != user) {
            if (request.sender == user) {
                rejectFriendRequestBySender(request)
                return request to true
            }

            throw FriendRequestNotFoundException
        }

        rejectFriendRequest(request)
        return request to false
    }

    @Transactional
    override fun respondAcceptFriendRequest(
        user: UserSession,
        requestId: Int
    ): FriendshipRequestEntity {
        val request = findFriendRequest(requestId)
            ?.takeIf { it.receiver == user.user }
            ?: throw FriendRequestNotFoundException

        return request.apply {
            acceptFriendRequest(request)
        }
    }
}

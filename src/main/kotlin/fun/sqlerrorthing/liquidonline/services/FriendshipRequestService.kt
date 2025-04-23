package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import org.springframework.web.socket.WebSocketSession

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

    fun createFriendRequestAndNotifyReceiverIfOnline(
        sender: UserEntity,
        receiver: UserEntity,
        receiverSession: WebSocketSession?
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
}

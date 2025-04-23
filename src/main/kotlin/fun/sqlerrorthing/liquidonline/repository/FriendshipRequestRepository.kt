package `fun`.sqlerrorthing.liquidonline.repository

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipRequestEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository


interface FriendshipRequestRepository : JpaRepository<FriendshipRequestEntity, Int> {
    fun findBySenderAndReceiver(sender: UserEntity, receiver: UserEntity): FriendshipRequestEntity?

    fun findAllBySender(sender: UserEntity): List<FriendshipRequestEntity>

    fun findAllByReceiver(receiver: UserEntity): List<FriendshipRequestEntity>
}

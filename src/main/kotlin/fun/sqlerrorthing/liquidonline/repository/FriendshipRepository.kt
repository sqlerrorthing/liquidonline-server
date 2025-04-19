package `fun`.sqlerrorthing.liquidonline.repository

import `fun`.sqlerrorthing.liquidonline.entities.FriendshipEntity
import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendshipRepository : JpaRepository<FriendshipEntity, Int> {
    @Query("""
        SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
        FROM FriendshipEntity f
        WHERE (f.user1 = :user1 AND f.user2 = :user2)
        OR (f.user1 = :user2 AND f.user2 = :user1)
    """) fun areFriends(
        @Param("user1") user1: UserEntity,
        @Param("user2") user2: UserEntity
    ): Boolean

    @Query("""
        SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END 
        FROM FriendshipEntity f
        WHERE (f.user1.username = :user1 AND f.user2.username = :user2)
        OR (f.user1.username = :user2 AND f.user2.username = :user1)
    """) fun areFriends(
        @Param("user1") username1: String,
        @Param("user2") username2: String
    ): Boolean

    @Query("""
        SELECT CASE WHEN f.user1 = :user THEN f.user2 ELSE f.user1 END
        FROM FriendshipEntity f
        WHERE f.user1 = :user OR f.user2 = :user
    """) fun findUserFriends(
        @Param("user") user: UserEntity
    ): List<UserEntity>
}
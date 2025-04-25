package `fun`.sqlerrorthing.liquidonline.services.friendship

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface FriendNotifierService {
    fun notifyFriendJoined(
        joinedSession: UserSession
    )

    fun notifyFriendLeaved(
        disconnectedSession: UserSession
    )

    fun notifyFriendsWithUsernameUpdate(
        updatedSession: UserSession
    )

    fun notifyFriendsWithMinecraftUsernameUpdate(
        updatedSession: UserSession
    )

    fun notifyFriendsWithSkinUpdate(
        updatedSession: UserSession
    )

    fun notifyFriendsWithServerUpdate(
        updatedSession: UserSession
    )

    fun notifyFriendWithFriendshipBroken(
        friend: UserSession,
        requester: UserSession
    )

    fun notifyFriendWithFriendshipBrokenIfFriendOnline(
        friend: UserEntity,
        requester: UserSession
    )
}

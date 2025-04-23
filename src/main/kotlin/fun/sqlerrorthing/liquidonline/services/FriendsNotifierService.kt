package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface FriendsNotifierService {
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
}

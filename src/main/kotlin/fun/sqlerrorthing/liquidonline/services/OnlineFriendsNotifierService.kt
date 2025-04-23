package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface OnlineFriendsNotifierService {
    fun notifyFriendJoined(joinedSession: UserSession)

    fun notifyFriendLeaved(disconnectedSession: UserSession)
}
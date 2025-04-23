package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.extensions.sendPacketToFriends
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendJoined
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendLeaved
import `fun`.sqlerrorthing.liquidonline.services.OnlineFriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Component

@Component
class InMemoryOnlineFriendsNotifierServiceImpl: OnlineFriendsNotifierService {
    override fun notifyFriendJoined(joinedSession: UserSession) {
        joinedSession.user.toFriendDto().also {
            joinedSession.sendPacketToFriends(
                S2CFriendJoined.builder()
                    .friend(it)
                    .build()
            )
        }
    }

    override fun notifyFriendLeaved(disconnectedSession: UserSession) {
        disconnectedSession.user.toFriendDto().also {
            disconnectedSession.sendPacketToFriends(
                S2CFriendLeaved.builder()
                    .friend(it)
                    .build()
            )
        }
    }
}
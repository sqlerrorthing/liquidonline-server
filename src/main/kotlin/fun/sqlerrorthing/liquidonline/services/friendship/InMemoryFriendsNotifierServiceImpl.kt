package `fun`.sqlerrorthing.liquidonline.services.friendship

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.extensions.onlineSession
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacketToFriends
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendJoined
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendLeaved
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendShipBroken
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendStatusUpdate
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Component
import java.util.*

@Component
class InMemoryFriendsNotifierServiceImpl: FriendsNotifierService {
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

    override fun notifyFriendsWithUsernameUpdate(updatedSession: UserSession) {
        updatedSession.sendPacketToFriends(
            S2CFriendStatusUpdate.builder()
                .friendId(updatedSession.user.id)
                .username(updatedSession.user.username)
                .build()
        )
    }

    override fun notifyFriendsWithMinecraftUsernameUpdate(updatedSession: UserSession) {
        updatedSession.sendPacketToFriends(
            S2CFriendStatusUpdate.builder()
                .friendId(updatedSession.user.id)
                .minecraftUsername(updatedSession.minecraftUsername)
                .build()
        )
    }

    override fun notifyFriendsWithSkinUpdate(updatedSession: UserSession) {
        updatedSession.sendPacketToFriends(
            S2CFriendStatusUpdate.builder()
                .friendId(updatedSession.user.id)
                .skin(Base64.getEncoder().encodeToString(updatedSession.skin))
                .build()
        )
    }

    override fun notifyFriendsWithServerUpdate(updatedSession: UserSession) {
        updatedSession.sendPacketToFriends(
            S2CFriendStatusUpdate.builder()
                .friendId(updatedSession.user.id)
                .server(updatedSession.server)
                .build()
        )
    }

    override fun notifyFriendWithFriendshipBroken(
        friend: UserSession,
        requester: UserSession
    ) {
        friend.sendPacket(
            S2CFriendShipBroken.builder()
                .with(requester.user.id)
                .build()
        )
    }

    override fun notifyFriendWithFriendshipBrokenIfFriendOnline(
        friend: UserEntity,
        requester: UserSession
    ) {
        friend.onlineSession?.let {
            notifyFriendWithFriendshipBroken(
                it,
                requester
            )
        }
    }
}

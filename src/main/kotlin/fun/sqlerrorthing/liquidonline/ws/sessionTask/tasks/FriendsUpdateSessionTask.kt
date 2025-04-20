package `fun`.sqlerrorthing.liquidonline.ws.sessionTask.tasks

import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriends
import `fun`.sqlerrorthing.liquidonline.services.FriendshipService
import `fun`.sqlerrorthing.liquidonline.services.WebSocketSessionStorageService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.sessionTask.SessionTask
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class FriendsUpdateSessionTask(
    private val friendshipService: FriendshipService,
    private val webSocketSessionStorageService: WebSocketSessionStorageService
) : SessionTask(Duration.ofMillis(500), Duration.ofSeconds(10)) {
    override fun run(session: UserSession) {
        val friends: List<FriendDto> = friendshipService.findUserFriends(session.user).map { friend ->
            webSocketSessionStorageService.findUserSession(friend)?.toFriendDto() ?: friend.toFriendDto()
        }

        session.sendMessage(
            S2CFriends
                .builder()
                .friends(friends)
                .build()
        )
    }
}
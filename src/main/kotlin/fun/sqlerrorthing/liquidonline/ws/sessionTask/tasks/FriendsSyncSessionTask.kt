package `fun`.sqlerrorthing.liquidonline.ws.sessionTask.tasks

import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriends
import `fun`.sqlerrorthing.liquidonline.services.FriendshipService
import `fun`.sqlerrorthing.liquidonline.services.SessionStorageService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.sessionTask.SessionTask
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class FriendsSyncSessionTask(
    private val friendshipService: FriendshipService,
    private val sessionStorageService: SessionStorageService
): SessionTask(Duration.ofMillis(500), Duration.ofSeconds(10)) {
    override fun run(session: UserSession) {
        val friends: List<FriendDto> = friendshipService.findUserFriends(session.user).map { friend ->
            sessionStorageService.findUserSession(friend)?.toFriendDto() ?: friend.toFriendDto()
        }

        session.sendPacket(
            S2CFriends
                .builder()
                .friends(friends)
                .build()
        )
    }
}

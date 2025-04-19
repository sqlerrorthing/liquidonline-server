package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriends
import `fun`.sqlerrorthing.liquidonline.services.FriendshipService
import `fun`.sqlerrorthing.liquidonline.services.WebSocketSessionStorageService
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@WebSocketMessageListener
class FriendsListeners(
    private val friendshipService: FriendshipService,
    private val webSocketSessionStorageService: WebSocketSessionStorageService
) {
    @Scheduled(fixedRate = 10000)
    fun syncFriends() {
        for ((connection, session) in webSocketSessionStorageService.authoredSessionsIterator) {
            val friends: List<FriendDto> = friendshipService.findUserFriends(session.user).map { friend ->
                webSocketSessionStorageService.findUserSession(friend)?.toFriendDto() ?: friend.toFriendDto()
            }

            connection.sendMessage(
                S2CFriends
                    .builder()
                    .friends(friends)
                    .build()
            )
        }
    }
}
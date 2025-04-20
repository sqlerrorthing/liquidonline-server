package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SSendFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendRequestResult
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriends
import `fun`.sqlerrorthing.liquidonline.services.FriendshipRequestService
import `fun`.sqlerrorthing.liquidonline.services.FriendshipService
import `fun`.sqlerrorthing.liquidonline.services.UserService
import `fun`.sqlerrorthing.liquidonline.services.WebSocketSessionStorageService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketMessageListener
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@WebSocketMessageListener
class FriendsListeners(
    private val userService: UserService,
    private val friendshipService: FriendshipService,
    private val friendshipRequestService: FriendshipRequestService,
    private val webSocketSessionStorageService: WebSocketSessionStorageService
) {
    @PacketMessageListener
    private fun sendFriendRequest(userSession: UserSession, packet: C2SSendFriendRequest): S2CFriendRequestResult {
        if (userSession.user.username == packet.username) {
            return S2CFriendRequestResult
                .builder()
                .status(S2CFriendRequestResult.Status.SENT_TO_SELF)
                .build()
        }

        val receiver = userService.findUserByUsername(packet.username) ?: return S2CFriendRequestResult
            .builder()
            .status(S2CFriendRequestResult.Status.NOT_FOUND)
            .build()

        if (friendshipService.areFriends(userSession.user, receiver)) {
            return S2CFriendRequestResult
                .builder()
                .status(S2CFriendRequestResult.Status.ALREADY_FRIENDS)
                .build()
        }

        friendshipRequestService.findBySenderAndReceiver(userSession.user, receiver)?.let {
            return S2CFriendRequestResult
                .builder()
                .status(S2CFriendRequestResult.Status.ALREADY_REQUESTED)
                .build()
        }

        friendshipRequestService.createFriendRequest(userSession.user, receiver, webSocketSessionStorageService.findUserSession(receiver)?.wsSession)

        return S2CFriendRequestResult
            .builder()
            .status(S2CFriendRequestResult.Status.REQUESTED)
            .build()
    }

    @Scheduled(fixedRate = 10000)
    private fun syncFriends() {
        for (session in webSocketSessionStorageService.authoredSessionsIterator) {
            val friends: List<FriendDto> = friendshipService.findUserFriends(session.user).map { friend ->
                webSocketSessionStorageService.findUserSession(friend)?.toFriendDto() ?: friend.toFriendDto()
            }

            session.wsSession.sendMessage(
                S2CFriends
                    .builder()
                    .friends(friends)
                    .build()
            )
        }
    }
}
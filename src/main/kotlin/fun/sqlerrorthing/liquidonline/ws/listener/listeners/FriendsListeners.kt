package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.dto.FriendDto
import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SRespondFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SSendFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SStopBeingFriends
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendRequestResult
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendShipBroken
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriends
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CStopBeingFriendsResult
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

        val receiver = userService.findUserByUsername(packet.username)
            ?: return S2CFriendRequestResult
                .builder()
                .status(S2CFriendRequestResult.Status.NOT_FOUND)
                .build()

        if (friendshipService.areFriends(userSession.user, receiver)) {
            return S2CFriendRequestResult
                .builder()
                .status(S2CFriendRequestResult.Status.ALREADY_FRIENDS)
                .build()
        }

        friendshipRequestService.findBySenderAndReceiver(receiver, userSession.user)?.let {
            // The user sent a friend request
            // to someone who already has this friend request pending.
            // Then we consider that he accepted this friend request.

            friendshipRequestService.acceptFriendRequest(
                request = it,
                senderSession = webSocketSessionStorageService.findUserSession(receiver),
                receiverSession = userSession
            )

            return S2CFriendRequestResult
                .builder()
                .status(S2CFriendRequestResult.Status.ACCEPTED)
                .build()
        }

        friendshipRequestService.findBySenderAndReceiver(userSession.user, receiver)?.let {
            return S2CFriendRequestResult
                .builder()
                .status(S2CFriendRequestResult.Status.ALREADY_REQUESTED)
                .build()
        }

        val request = friendshipRequestService
            .createFriendRequest(userSession.user, receiver, webSocketSessionStorageService.findUserSession(receiver)?.wsSession)

        return S2CFriendRequestResult
            .builder()
            .status(S2CFriendRequestResult.Status.REQUESTED)
            .requestId(request.id)
            .build()
    }

    @PacketMessageListener
    private fun stopBeingFriends(userSession: UserSession, packet: C2SStopBeingFriends): S2CStopBeingFriendsResult {
        val friend = userService.findUserById(packet.friendId)
            ?: return S2CStopBeingFriendsResult
                .builder()
                .status(S2CStopBeingFriendsResult.Status.WAS_NO_FRIENDSHIP)
                .build()

        val friendship = friendshipService.findFriendship(userSession.user, friend)
            ?: return S2CStopBeingFriendsResult
                .builder()
                .status(S2CStopBeingFriendsResult.Status.WAS_NO_FRIENDSHIP)
                .build()

        friendshipService.brokeFriendship(friendship)

        webSocketSessionStorageService.findUserSession(friend)?.sendMessage(
            S2CFriendShipBroken
                .builder()
                .with(userSession.user.id)
                .build()
        )

        return S2CStopBeingFriendsResult
            .builder()
            .status(S2CStopBeingFriendsResult.Status.SUCCESS)
            .build()
    }

    @PacketMessageListener
    private fun respondFriendRequest(userSession: UserSession, packet: C2SRespondFriendRequest) {
        val request = friendshipRequestService.findFriendRequest(packet.requestId)
            ?.takeIf { it.receiver == userSession.user }
            ?: return

        val senderSession = webSocketSessionStorageService.findUserSession(request.sender)

        when (packet.status) {
            C2SRespondFriendRequest.Status.ACCEPTED -> friendshipRequestService.acceptFriendRequest(request, senderSession, userSession)
            C2SRespondFriendRequest.Status.REJECT -> friendshipRequestService.rejectFriendRequest(request, senderSession)
        }
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
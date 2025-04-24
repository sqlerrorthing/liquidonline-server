package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.extensions.onlineSession
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SRespondFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SSendFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SStopBeingFriends
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendRequestResult
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2COutgoingFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CRespondFriendRequestResult
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CStopBeingFriendsResult
import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendshipRequestService
import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendshipService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketMessageListener
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.stereotype.Component

@Component
@WebSocketMessageListener
class FriendsListeners(
    private val friendshipService: FriendshipService,
    private val friendshipRequestService: FriendshipRequestService,
) {
    @PacketMessageListener
    @Suppress("ReturnCount", "unused")
    private fun sendFriendRequest(userSession: UserSession, packet: C2SSendFriendRequest): S2CFriendRequestResult {
        return try {
            val request = friendshipRequestService.sendFriendRequest(
                userSession.user,
                packet.username
            )

            S2CFriendRequestResult.builder()
                .status(S2CFriendRequestResult.Status.REQUESTED)
                .requestId(request.id)
                .build()
        } catch (_: FriendRequestToSelfException) {
            S2CFriendRequestResult.builder()
                .status(S2CFriendRequestResult.Status.SENT_TO_SELF)
                .build()
        } catch (_: UserNotFoundException) {
            S2CFriendRequestResult.builder()
                .status(S2CFriendRequestResult.Status.NOT_FOUND)
                .build()
        } catch (_: AlreadyFriendsException) {
            S2CFriendRequestResult.builder()
                .status(S2CFriendRequestResult.Status.ALREADY_FRIENDS)
                .build()
        } catch (_: AlreadyRequestedException) {
            S2CFriendRequestResult.builder()
                .status(S2CFriendRequestResult.Status.ALREADY_REQUESTED)
                .build()
        } catch (e: ReverseFriendRequestExistsException) {
            e.reverseRequest.let { request ->
                friendshipRequestService.acceptFriendRequest(request)

                S2CFriendRequestResult.builder()
                    .requestId(request.id)
                    .status(S2CFriendRequestResult.Status.ACCEPTED)
                    .build()
            }
        }
    }

    @PacketMessageListener
    @Suppress("ReturnCount", "unused")
    private fun removeFriend(userSession: UserSession, packet: C2SStopBeingFriends): S2CStopBeingFriendsResult {
        return try {
            friendshipService.brokeFriendship(userSession, packet.friendId)

            S2CStopBeingFriendsResult.builder()
                .status(S2CStopBeingFriendsResult.Status.SUCCESS)
                .build()
        } catch (ex: Exception) {
            when (ex) {
                is UserNotFoundException,
                is WasNoFriendship -> {
                    S2CStopBeingFriendsResult
                        .builder()
                        .status(S2CStopBeingFriendsResult.Status.WAS_NO_FRIENDSHIP)
                        .build()
                }

                else -> throw ex
            }
        }
    }

    @PacketMessageListener
    @Suppress("unused")
    private fun respondFriendRequest(userSession: UserSession, packet: C2SRespondFriendRequest): Packet {
        return try {
            when (packet.status) {
                C2SRespondFriendRequest.Status.ACCEPTED -> {
                    val request =
                        friendshipRequestService.respondAcceptFriendRequest(userSession, packet.requestId)

                    S2CRespondFriendRequestResult.builder()
                        .status(S2CRespondFriendRequestResult.Status.SUCCESS)
                        .friend(request.sender.onlineSession?.toFriendDto() ?: request.sender.toFriendDto())
                        .build()
                }

                C2SRespondFriendRequest.Status.REJECT -> {
                    val (request, isRejectedBySender) =
                        friendshipRequestService.respondRejectFriendRequest(userSession, packet.requestId)

                    if (isRejectedBySender) {
                        S2COutgoingFriendRequest.builder()
                            .requestId(request.id)
                            .to(request.receiver.username)
                            .status(S2COutgoingFriendRequest.Status.REJECT)
                            .build()
                    } else {
                        S2CRespondFriendRequestResult.builder()
                            .status(S2CRespondFriendRequestResult.Status.SUCCESS)
                            .build()
                    }
                }
            }
        } catch (_: FriendRequestNotFoundException) {
            S2CRespondFriendRequestResult.builder()
                .status(S2CRespondFriendRequestResult.Status.REQUEST_NOT_FOUND)
                .build()
        }
    }
}

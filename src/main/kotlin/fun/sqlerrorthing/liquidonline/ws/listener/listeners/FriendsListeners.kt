package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.extensions.onlineSession
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.toFriendDto
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SRespondFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SSendFriendRequest
import `fun`.sqlerrorthing.liquidonline.packets.c2s.friends.C2SStopBeingFriends
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.*
import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendshipRequestService
import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendshipService
import `fun`.sqlerrorthing.liquidonline.services.session.SessionStorageService
import `fun`.sqlerrorthing.liquidonline.services.user.UserService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketMessageListener
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.stereotype.Component

@Component
@WebSocketMessageListener
class FriendsListeners(
    private val userService: UserService,
    private val friendshipService: FriendshipService,
    private val friendshipRequestService: FriendshipRequestService,
    private val sessionStorageService: SessionStorageService,
    private val friendsNotifierService: FriendsNotifierService
) {
    @PacketMessageListener
    @Suppress("ReturnCount", "unused")
    private fun sendFriendRequest(userSession: UserSession, packet: C2SSendFriendRequest): S2CFriendRequestResult {
        return try {
            val request = friendshipRequestService.sendFriendRequest(
                userSession.user,
                packet.username
            )

            request.receiver.onlineSession?.sendPacket(
                S2CNewIncomingFriendRequest.builder()
                    .from(userSession.user.username)
                    .requestId(request.id)
                    .build()
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
                .status(S2CFriendRequestResult.Status.ALREADY_FRIENDS)
                .build()
        } catch (e: ReverseFriendRequestExistsException) {
            e.reverseRequest.let { request ->
                friendshipRequestService.acceptFriendRequest(request)

                e.receiver.onlineSession?.sendPacket(
                    S2COutgoingFriendRequest.builder()
                        .requestId(request.id)
                        .to(request.receiver.username)
                        .status(S2COutgoingFriendRequest.Status.ACCEPTED)
                        .friend(userSession.toFriendDto())
                        .build()
                )

                S2CFriendRequestResult.builder()
                    .requestId(request.id)
                    .status(S2CFriendRequestResult.Status.ACCEPTED)
                    .build()
            }
        }
    }

    @PacketMessageListener
    @Suppress("ReturnCount", "unused")
    private fun stopBeingFriends(userSession: UserSession, packet: C2SStopBeingFriends): S2CStopBeingFriendsResult {
        return try {
            friendshipService.brokeFriendship(userSession, packet.friendId).let { friend ->
                friendsNotifierService.notifyFriendWithFriendshipBrokenIfFriendOnline(
                    friend,
                    userSession
                )
            }

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
        val request = friendshipRequestService.findFriendRequest(packet.requestId)
            ?: return S2CRespondFriendRequestResult.builder()
                .status(S2CRespondFriendRequestResult.Status.REQUEST_NOT_FOUND)
                .build()

        val isReceiver = request.receiver == userSession.user
        val isSender = request.sender == userSession.user

        if (!isReceiver) {
            if (isSender && packet.status == C2SRespondFriendRequest.Status.REJECT) {
                friendshipRequestService.rejectFriendRequest(request)

                sessionStorageService.findUserSession(request.receiver)?.sendPacket(
                    S2CIncomingFriendRequestRejected.builder()
                        .requestId(request.id)
                        .from(userSession.user.username)
                        .build()
                )

                return S2COutgoingFriendRequest.builder()
                    .requestId(request.id)
                    .to(request.receiver.username)
                    .status(S2COutgoingFriendRequest.Status.REJECT)
                    .build()
            }

            return S2CRespondFriendRequestResult.builder()
                .status(S2CRespondFriendRequestResult.Status.REQUEST_NOT_FOUND)
                .build()
        }

        val senderSession = sessionStorageService.findUserSession(request.sender)

        when (packet.status) {
            C2SRespondFriendRequest.Status.ACCEPTED -> {
                friendshipRequestService.acceptFriendRequest(request)

                senderSession?.sendPacket(
                    S2COutgoingFriendRequest.builder()
                        .requestId(request.id)
                        .to(request.receiver.username)
                        .status(S2COutgoingFriendRequest.Status.ACCEPTED)
                        .friend(userSession.toFriendDto())
                        .build()
                )
            }
            C2SRespondFriendRequest.Status.REJECT -> {
                friendshipRequestService.rejectFriendRequest(request)

                senderSession?.sendPacket(
                    S2COutgoingFriendRequest.builder()
                        .requestId(request.id)
                        .to(request.receiver.username)
                        .status(S2COutgoingFriendRequest.Status.REJECT)
                        .build()
                )
            }
        }

        return S2CRespondFriendRequestResult.builder()
            .status(S2CRespondFriendRequestResult.Status.SUCCESS).apply {
                if (packet.status == C2SRespondFriendRequest.Status.ACCEPTED) {
                    friend(senderSession?.toFriendDto() ?: request.sender.toFriendDto())
                }
            }
            .build()
    }
}

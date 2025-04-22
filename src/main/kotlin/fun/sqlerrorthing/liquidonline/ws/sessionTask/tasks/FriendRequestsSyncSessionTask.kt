package `fun`.sqlerrorthing.liquidonline.ws.sessionTask.tasks

import `fun`.sqlerrorthing.liquidonline.dto.FriendRequestDto
import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendRequests
import `fun`.sqlerrorthing.liquidonline.services.impl.FriendshipRequestService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.sessionTask.SessionTask
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class FriendRequestsSyncSessionTask (
    private val friendshipRequestService: FriendshipRequestService
): SessionTask(Duration.ofMillis(250), Duration.ofSeconds(5)) {
    override fun run(session: UserSession) {
        val incoming = friendshipRequestService.findAllByReceiver(session.user)
            .map {
                FriendRequestDto
                    .builder()
                    .requestId(it.id)
                    .username(it.sender.username)
                    .build()
            }

        val outgoing = friendshipRequestService.findAllBySender(session.user)
            .map {
                FriendRequestDto
                    .builder()
                    .requestId(it.id)
                    .username(it.receiver.username)
                    .build()
            }

        session.sendPacket(
            S2CFriendRequests
                .builder()
                .incoming(incoming)
                .outgoing(outgoing)
                .build()
        )
    }
}
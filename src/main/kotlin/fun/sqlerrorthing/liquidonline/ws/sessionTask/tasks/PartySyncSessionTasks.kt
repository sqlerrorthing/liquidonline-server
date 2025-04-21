package `fun`.sqlerrorthing.liquidonline.ws.sessionTask.tasks

import `fun`.sqlerrorthing.liquidonline.extensions.sendPacket
import `fun`.sqlerrorthing.liquidonline.extensions.toPartyDto
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyMemberStatusUpdate
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartySync
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.sessionTask.SessionTask
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PartySyncSessionTasks : SessionTask(Duration.ofSeconds(2)) {
    override fun run(session: UserSession) {
        session.activeParty.let {
            session.sendPacket(
                S2CPartySync.builder()
                    .party(it?.toPartyDto())
                    .build()
            )
        }
    }
}
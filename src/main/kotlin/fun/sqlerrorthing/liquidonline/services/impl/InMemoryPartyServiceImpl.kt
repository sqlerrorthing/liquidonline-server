package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.dto.play.PlayDto
import `fun`.sqlerrorthing.liquidonline.extensions.createPartyMember
import `fun`.sqlerrorthing.liquidonline.services.PartyService
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

@Service
class InMemoryPartyServiceImpl : PartyService {
    private val parties: MutableList<Party> = CopyOnWriteArrayList()

    override fun createParty(
        name: String,
        creator: UserSession,
        playData: PlayDto?,
    ): Party {
        val member = creator.createPartyMember(playData = playData)

        return Party.builder()
            .uuid(UUID.randomUUID())
            .name(name)
            .owner(member)
            .build()
        .apply {
            creator.activeParty = this
            this.members.add(member)
        }
    }
}
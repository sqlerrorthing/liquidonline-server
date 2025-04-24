package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.services.party.PartyNotifierService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service

@Service
class MinecraftUsernameServiceImpl(
    private val friendsNotifierService: FriendsNotifierService,
    private val partyNotifierService: PartyNotifierService
): MinecraftUsernameService {
    override fun updateUsername(session: UserSession, newUsername: String): Boolean {
        return if (newUsername != session.minecraftUsername) {
            session.minecraftUsername = newUsername

            friendsNotifierService.notifyFriendsWithMinecraftUsernameUpdate(session)

            session.activeParty?.let { (party, member) ->
                partyNotifierService.notifyPartyMemberMinecraftUsernameUpdate(party, member)
            }

            true
        } else {
            false
        }
    }
}

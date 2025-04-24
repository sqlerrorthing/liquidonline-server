package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service

@Service
class MinecraftUsernameServiceImpl(
    private val friendsNotifierService: FriendsNotifierService
): MinecraftUsernameService {
    override fun updateUsername(session: UserSession, newUsername: String): Boolean {
        return if (newUsername != session.minecraftUsername) {
            session.minecraftUsername = newUsername
            friendsNotifierService.notifyFriendsWithMinecraftUsernameUpdate(session)
            true
        } else {
            false
        }
    }
}

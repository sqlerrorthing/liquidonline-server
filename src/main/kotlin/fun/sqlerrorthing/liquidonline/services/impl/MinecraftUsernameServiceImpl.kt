package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.services.FriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.services.MinecraftUsernameService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service

@Service
class MinecraftUsernameServiceImpl(
    private val friendsNotifierService: FriendsNotifierService
): MinecraftUsernameService {
    override fun updateUsername(session: UserSession, newUsername: String) {
        if (newUsername != session.minecraftUsername) {
            session.minecraftUsername = newUsername
            friendsNotifierService.notifyFriendsWithMinecraftUsernameUpdate(session)
        }
    }
}

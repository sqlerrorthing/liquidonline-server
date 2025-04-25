package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendNotifierService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service

@Service
class MinecraftServerServiceImpl(
    private val friendNotifierService: FriendNotifierService,
): MinecraftServerService {
    override fun updateServer(session: UserSession, newServer: String?): Boolean {
        return if (newServer != session.server) {
            session.server = newServer
            friendNotifierService.notifyFriendsWithServerUpdate(session)
            true
        } else {
            false
        }
    }
}

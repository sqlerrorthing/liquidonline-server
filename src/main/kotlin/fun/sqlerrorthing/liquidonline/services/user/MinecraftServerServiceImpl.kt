package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.stereotype.Service

@Service
class MinecraftServerServiceImpl(
    private val friendsNotifierService: FriendsNotifierService,
): MinecraftServerService {
    override fun updateServer(session: UserSession, newServer: String?) {
        if (newServer != session.server) {
            session.server = newServer
            friendsNotifierService.notifyFriendsWithServerUpdate(session)
        }
    }
}

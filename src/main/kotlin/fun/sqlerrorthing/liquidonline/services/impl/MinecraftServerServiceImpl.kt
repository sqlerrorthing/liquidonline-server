package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.services.FriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.services.MinecraftServerService
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

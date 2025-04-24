package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface MinecraftServerService {
    fun updateServer(session: UserSession, newServer: String?): Boolean
}

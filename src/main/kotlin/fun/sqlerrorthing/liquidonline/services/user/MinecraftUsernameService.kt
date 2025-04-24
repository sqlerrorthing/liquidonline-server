package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface MinecraftUsernameService {
    fun updateUsername(session: UserSession, newUsername: String)
}

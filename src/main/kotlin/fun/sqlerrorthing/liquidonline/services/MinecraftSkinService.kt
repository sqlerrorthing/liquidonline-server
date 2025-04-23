package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface MinecraftSkinService {
    fun updateSkin(session: UserSession, newSkin: String)
}

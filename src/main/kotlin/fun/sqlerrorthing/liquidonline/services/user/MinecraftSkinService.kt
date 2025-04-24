package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface MinecraftSkinService {
    fun updateSkin(session: UserSession, newSkin: String): Boolean

    fun updateSkin(session: UserSession, newSkin: ByteArray): Boolean
}

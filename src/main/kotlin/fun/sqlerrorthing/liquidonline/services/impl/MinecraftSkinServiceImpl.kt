package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.services.FriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.services.MinecraftSkinService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.SkinValidator
import org.springframework.stereotype.Service

@Service
class MinecraftSkinServiceImpl(
    private val skinValidator: SkinValidator,
    private val friendsNotifierService: FriendsNotifierService
): MinecraftSkinService {
    override fun updateSkin(session: UserSession, newSkin: String) {
        skinValidator.validateHead(newSkin)?.let {
            if (!session.skin.contentEquals(it)) {
                session.skin = it
                friendsNotifierService.notifyFriendsWithSkinUpdate(session)
            }
        }
    }
}
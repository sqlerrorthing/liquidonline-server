package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.services.friendship.FriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.SkinValidator
import org.springframework.stereotype.Service
import java.util.*

@Service
class MinecraftSkinServiceImpl(
    private val skinValidator: SkinValidator,
    private val friendsNotifierService: FriendsNotifierService
): MinecraftSkinService {
    override fun updateSkin(session: UserSession, newSkin: String): Boolean {
        return runCatching {
            updateSkin(session, Base64.getDecoder().decode(newSkin))
        }.getOrDefault(false)
    }

    override fun updateSkin(
        session: UserSession,
        newSkin: ByteArray
    ): Boolean {
        skinValidator.validateHead(newSkin)?.let {
            if (!session.skin.contentEquals(it)) {
                session.skin = it
                friendsNotifierService.notifyFriendsWithSkinUpdate(session)
                return true
            }
        }

        return false
    }
}

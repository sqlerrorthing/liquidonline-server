package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdateMinecraftUsername
import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdatePlayingServer
import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdateSkin
import `fun`.sqlerrorthing.liquidonline.services.FriendsNotifierService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.SkinValidator
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketMessageListener
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.stereotype.Component

@Component
@WebSocketMessageListener
class MinecraftDataUpdateListeners(
    private val skinValidator: SkinValidator,
    private val friendsNotifierService: FriendsNotifierService
) {
    @PacketMessageListener
    fun handleMinecraftUsernameUpdateEvent(session: UserSession, packet: C2SUpdateMinecraftUsername) {
        packet.username.takeIf { it != session.minecraftUsername }
            ?.let {
                session.minecraftUsername = it
                friendsNotifierService.notifyFriendsWithMinecraftUsernameUpdate(session)
            }
    }

    @PacketMessageListener
    fun handleMinecraftPlayingServerUpdateEvent(session: UserSession, packet: C2SUpdatePlayingServer) {
        packet.server.takeIf { it != session.server }
            ?.let {
                session.server = packet.server
                friendsNotifierService.notifyFriendsWithServerUpdate(session)
        }
    }

    @PacketMessageListener
    fun handleHeadSkinUpdateEvent(session: UserSession, packet: C2SUpdateSkin) {
        skinValidator.validateHead(packet.skin)
            ?.takeIf { !session.skin.contentEquals(it) }
            ?.let {
                session.skin = it
                friendsNotifierService.notifyFriendsWithSkinUpdate(session)
            }
    }
}

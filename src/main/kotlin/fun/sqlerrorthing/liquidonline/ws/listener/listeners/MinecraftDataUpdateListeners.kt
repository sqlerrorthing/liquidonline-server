package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdateMinecraftUsername
import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdatePlayingServer
import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdateSkin
import `fun`.sqlerrorthing.liquidonline.services.user.MinecraftServerService
import `fun`.sqlerrorthing.liquidonline.services.user.MinecraftSkinService
import `fun`.sqlerrorthing.liquidonline.services.user.MinecraftUsernameService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketMessageListener
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.stereotype.Component

@Component
@WebSocketMessageListener
class MinecraftDataUpdateListeners(
    private val minecraftUsernameService: MinecraftUsernameService,
    private val minecraftServerService: MinecraftServerService,
    private val minecraftSkinService: MinecraftSkinService
) {
    @PacketMessageListener
    fun handleMinecraftUsernameUpdateEvent(session: UserSession, packet: C2SUpdateMinecraftUsername) {
        minecraftUsernameService.updateUsername(session, packet.username)
    }

    @PacketMessageListener
    fun handleMinecraftPlayingServerUpdateEvent(session: UserSession, packet: C2SUpdatePlayingServer) {
        minecraftServerService.updateServer(session, packet.server)
    }

    @PacketMessageListener
    fun handleHeadSkinUpdateEvent(session: UserSession, packet: C2SUpdateSkin) {
        minecraftSkinService.updateSkin(session, packet.skin)
    }
}

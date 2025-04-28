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
class MinecraftDataUpdateListener(
    private val minecraftUsernameService: MinecraftUsernameService,
    private val minecraftServerService: MinecraftServerService,
    private val minecraftSkinService: MinecraftSkinService
) {
    @PacketMessageListener
    @Suppress("unused")
    fun handleMinecraftUsernameUpdateEvent(session: UserSession, packet: C2SUpdateMinecraftUsername) {
        minecraftUsernameService.updateUsername(session, packet.username)
    }

    @PacketMessageListener
    @Suppress("unused")
    fun handleMinecraftPlayingServerUpdateEvent(session: UserSession, packet: C2SUpdatePlayingServer) {
        minecraftServerService.updateServer(session, packet.server)
    }

    @PacketMessageListener
    @Suppress("unused")
    fun handleHeadSkinUpdateEvent(session: UserSession, packet: C2SUpdateSkin) {
        minecraftSkinService.updateSkin(session, packet.skin)
    }
}

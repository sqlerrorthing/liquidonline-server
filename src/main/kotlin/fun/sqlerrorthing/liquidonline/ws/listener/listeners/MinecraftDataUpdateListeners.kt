package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.extensions.sendPacketToFriends
import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdateMinecraftUsername
import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdatePlayingServer
import `fun`.sqlerrorthing.liquidonline.packets.c2s.update.C2SUpdateSkin
import `fun`.sqlerrorthing.liquidonline.packets.s2c.friends.S2CFriendStatusUpdate
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.utils.SkinValidator
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketMessageListener
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.Base64

@Component
@WebSocketMessageListener
class MinecraftDataUpdateListeners(
    private val skinValidator: SkinValidator
) {
    @PacketMessageListener
    fun handleMinecraftUsernameUpdateEvent(session: UserSession, packet: C2SUpdateMinecraftUsername) {
        if (session.minecraftUsername == packet.username) {
            return
        }

        session.minecraftUsername = packet.username

        val updatePacket = S2CFriendStatusUpdate
            .builder()
            .friendId(session.user.id)
            .minecraftUsername(packet.username)
            .build()

        session.sendPacketToFriends { updatePacket }
    }

    @PacketMessageListener
    fun handleMinecraftPlayingServerUpdateEvent(session: UserSession, packet: C2SUpdatePlayingServer) {
        if (session.server == packet.server) {
            return
        }

        session.server = packet.server

        val updatePacket = S2CFriendStatusUpdate
            .builder()
            .friendId(session.user.id)
            .server(packet.server)
            .build()

        session.sendPacketToFriends { updatePacket }
    }

    @PacketMessageListener
    fun handleHeadSkinUpdateEvent(session: UserSession, packet: C2SUpdateSkin) {
        skinValidator.validateHead(packet.skin)?.let {
            if (session.skin.contentEquals(it)) {
                return
            }

            session.skin = it

            val updatePacket = S2CFriendStatusUpdate
                .builder()
                .friendId(session.user.id)
                .skin(Base64.getEncoder().encodeToString(it))
                .build()

            session.sendPacketToFriends { updatePacket }
        }
    }
}

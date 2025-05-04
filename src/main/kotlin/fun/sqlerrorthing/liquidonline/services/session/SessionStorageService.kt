package `fun`.sqlerrorthing.liquidonline.services.session

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.web.socket.WebSocketSession

/**
 * TODO: Create AuthoredSessionStorageService
 */
interface SessionStorageService {
    fun sessionPacket(
        session: WebSocketSession,
        packet: Packet
    )

    fun authSessionAndNotifyUserFriends(
        session: UserSession
    )

    fun isInSession(
        session: WebSocketSession
    ): Boolean

    fun findUserSession(
        user: UserEntity
    ): UserSession?

    fun findUserSession(
        username: String
    ): UserSession?

    fun addSession(
        session: WebSocketSession
    )

    fun removeSession(
        session: WebSocketSession
    )
}

package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import org.springframework.web.socket.WebSocketSession

interface AuthService {
    fun authenticateUser(session: WebSocketSession, packet: C2SLogin): UserSession?
}

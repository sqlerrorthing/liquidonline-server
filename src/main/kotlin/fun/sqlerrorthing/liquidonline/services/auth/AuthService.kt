package `fun`.sqlerrorthing.liquidonline.services.auth

import `fun`.sqlerrorthing.liquidonline.packets.c2s.login.C2SLogin
import org.springframework.web.socket.WebSocketSession

interface AuthService {
    fun authenticate(session: WebSocketSession, packet: C2SLogin)
}

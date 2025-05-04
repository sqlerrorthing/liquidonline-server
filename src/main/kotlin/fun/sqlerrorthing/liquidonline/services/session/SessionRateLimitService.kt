package `fun`.sqlerrorthing.liquidonline.services.session

import org.springframework.web.socket.WebSocketSession

interface SessionRateLimitService {
    /**
     * @return true if the connection is allowed, otherwise returns false
     */
    fun sessionConnected(session: WebSocketSession): Boolean

    /**
     * @return true if the message can proceed, false if skip the message
     */
    fun onPreMessage(session: WebSocketSession): Boolean

    fun sessionDisconnected(session: WebSocketSession)
}
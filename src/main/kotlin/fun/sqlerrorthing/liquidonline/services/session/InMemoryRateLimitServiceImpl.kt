package `fun`.sqlerrorthing.liquidonline.services.session

import `fun`.sqlerrorthing.liquidonline.properties.PacketRateLimitConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class InMemoryRateLimitServiceImpl(
    private val packetRateLimitConfigurationProperties: PacketRateLimitConfigurationProperties
): SessionRateLimitService {
    private val messagesTimestamps: MutableMap<String, MutableList<Long>> = ConcurrentHashMap()

    override fun sessionConnected(session: WebSocketSession): Boolean {
        messagesTimestamps[session.id] = mutableListOf()
        return true
    }

    override fun onPreMessage(session: WebSocketSession): Boolean {
        val now = Instant.now().toEpochMilli()
        val timestamps = messagesTimestamps.computeIfAbsent(session.id) { mutableListOf() }

        timestamps.add(now)
        timestamps.removeIf { it < now - packetRateLimitConfigurationProperties.window.toMillis() }

        val violation = timestamps.size > packetRateLimitConfigurationProperties.max

        if (violation) {
            session.close(CloseStatus.POLICY_VIOLATION)
        }

        return !violation
    }

    override fun sessionDisconnected(session: WebSocketSession) {
        messagesTimestamps.remove(session.id)
    }
}
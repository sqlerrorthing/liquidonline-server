package `fun`.sqlerrorthing.liquidonline.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import kotlin.properties.Delegates

@ConfigurationProperties(prefix = "server.packets.limit")
class PacketRateLimitConfigurationProperties {
    var max: Int by Delegates.notNull()
    var window: Duration by Delegates.notNull()
}

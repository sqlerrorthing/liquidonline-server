package `fun`.sqlerrorthing.liquidonline.config

import `fun`.sqlerrorthing.liquidonline.packets.strategy.PacketSerializationStrategy
import `fun`.sqlerrorthing.liquidonline.packets.strategy.impl.jackson.json.JacksonJsonPacketSerializationStrategy
import `fun`.sqlerrorthing.liquidonline.packets.strategy.impl.netty.compilertime.CompilerTimeByteBufPacketSerializationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PacketSerializationConfig {
    @Bean
    fun packetSerializationStrategy(): PacketSerializationStrategy {
        return JacksonJsonPacketSerializationStrategy()
    }
}

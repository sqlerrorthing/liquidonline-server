package `fun`.sqlerrorthing.liquidonline.config

import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import `fun`.sqlerrorthing.liquidonline.config.adapters.RegisterInObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig(
    val moduleComponents: List<RegisterInObjectMapper<*>>,
) {
    @Bean
    @Suppress("UNCHECKED_CAST")
    fun objectMapper(): ObjectMapper {
        val module = SimpleModule().apply {
            for (component in moduleComponents) {
                when (component) {
                    is JsonSerializer<*> -> {
                        addSerializer(component.clazz as Class<Any>, component as JsonSerializer<Any>)
                    }
                    is JsonDeserializer<*> -> {
                        addDeserializer(component.clazz as Class<Any>, component as JsonDeserializer<Any>)
                    }
                }
            }
        }

        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(module)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}
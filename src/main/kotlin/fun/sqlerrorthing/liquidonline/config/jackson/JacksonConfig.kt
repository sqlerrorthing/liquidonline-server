package `fun`.sqlerrorthing.liquidonline.config.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import `fun`.sqlerrorthing.liquidonline.config.jackson.adapters.RegisterInObjectMapper
import `fun`.sqlerrorthing.liquidonline.config.jackson.introspectors.AnnotationIntrospector
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
            .setAnnotationIntrospector(AnnotationIntrospector())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(JavaTimeModule())
            .registerModule(module)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    }
}
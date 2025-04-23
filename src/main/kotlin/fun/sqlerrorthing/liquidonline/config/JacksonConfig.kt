package `fun`.sqlerrorthing.liquidonline.config

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import `fun`.sqlerrorthing.liquidonline.config.adapters.RegisterInObjectMapper
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder


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

        return JsonMapper()
            .registerModule(JavaTimeModule())
            .registerModule(module)
            .registerModule(parameterNamesModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Bean
    fun parameterNamesModule() = ParameterNamesModule(JsonCreator.Mode.PROPERTIES)

    @Bean
    fun customizer(vararg modules: Module): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder -> builder.modulesToInstall(*modules) }
    }
}
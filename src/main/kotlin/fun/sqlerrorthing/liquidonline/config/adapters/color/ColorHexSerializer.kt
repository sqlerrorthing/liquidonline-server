package `fun`.sqlerrorthing.liquidonline.config.adapters.color

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import `fun`.sqlerrorthing.liquidonline.config.adapters.RegisterInObjectMapper
import org.springframework.stereotype.Component
import java.awt.Color

@Component
@Suppress("ImplicitDefaultLocale")
class ColorHexSerializer : JsonSerializer<Color>(), RegisterInObjectMapper<Color> {
    override fun serialize(color: Color, gen: JsonGenerator, serializers: SerializerProvider) {
        val hex = String.format("%02X%02X%02X", color.red, color.green, color.blue)
        gen.writeString(hex)
    }

    override val clazz: Class<Color>
        get() = Color::class.java
}

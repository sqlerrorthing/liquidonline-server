package `fun`.sqlerrorthing.liquidonline.config.adapters.color

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import `fun`.sqlerrorthing.liquidonline.config.adapters.RegisterInObjectMapper
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class ColorHexDeserializer : JsonDeserializer<Color>(), RegisterInObjectMapper<Color> {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext?): Color {
        val hex: String = parser.text
        val rgb = hex.toInt(16)

        return Color((rgb shr 16) and 0xFF, (rgb shr 8) and 0xFF, rgb and 0xFF)
    }

    override val clazz: Class<Color>
        get() = Color::class.java
}
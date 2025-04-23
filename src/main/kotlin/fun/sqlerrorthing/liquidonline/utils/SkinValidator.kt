package `fun`.sqlerrorthing.liquidonline.utils

import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

private val PNG_SIGNATURE = byteArrayOf(
    0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
)

@Component
class SkinValidator {
    /**
     * Takes as input a base64 string with a png of a head (16x16) from a skin.
     * Returns null if the skin does not meet the requirements
     */
    fun validateHead(head: String): ByteArray? {
        return runCatching {
            val imageBytes = Base64.getDecoder().decode(head)

            if (!imageBytes.take(8).toByteArray().contentEquals(PNG_SIGNATURE)) {
                return null
            }

            val image: BufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes)) ?: return null

            if (image.width != 16 || image.height != 16) {
                return null
            }

            ByteArrayOutputStream().apply {
                if (!ImageIO.write(image, "png", this)) {
                    return null
                }
            }.toByteArray()
        }.getOrNull()
    }
}

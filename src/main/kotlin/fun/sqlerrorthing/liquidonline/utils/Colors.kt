package `fun`.sqlerrorthing.liquidonline.utils

import org.springframework.stereotype.Component
import java.awt.Color

@Component
class Colors {
    private val colors = arrayOf(
        Color(30, 144, 255),
        Color(46, 204, 133),
        Color(78, 205, 196),
        Color(255, 107, 107),
        Color(175, 79, 214),
        Color(255, 202, 58),
        Color(121, 47, 206),
        Color(255, 7, 60),
        Color(96, 69, 228),
        Color(2, 169, 68),
        Color(188, 191, 16),
    )

    val size get() = colors.size

    fun getColor(position: Int = 0): Color {
        return colors[position % size]
    }
}
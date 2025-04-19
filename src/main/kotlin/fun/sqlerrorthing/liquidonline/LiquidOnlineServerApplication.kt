package `fun`.sqlerrorthing.liquidonline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LiquidOnlineServerApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<LiquidOnlineServerApplication>(*args)
}

package `fun`.sqlerrorthing.liquidonline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan("fun.sqlerrorthing.liquidonline.properties")
class LiquidOnlineServerApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<LiquidOnlineServerApplication>(*args)
}

package `fun`.sqlerrorthing.liquidonline.config

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ValidationConfig {
    @Bean
    fun validator(): Validator =
        Validation.buildDefaultValidatorFactory().validator
}
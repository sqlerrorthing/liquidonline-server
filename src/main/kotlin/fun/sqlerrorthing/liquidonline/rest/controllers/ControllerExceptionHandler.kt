package `fun`.sqlerrorthing.liquidonline.rest.controllers

import `fun`.sqlerrorthing.liquidonline.dtos.ExceptionDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ControllerExceptionHandler {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ExceptionDto {
        return ExceptionDto.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Bad Request")
            .details(ex.bindingResult.fieldErrors.associate {
                it.field to (it.defaultMessage ?: "Invalid value provided")
            })
            .build()
    }

    @ExceptionHandler
    @ResponseBody
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ExceptionDto> {
        val status = ex.javaClass.getAnnotation(ResponseStatus::class.java)
            ?: run {
                logger.error(ex.message, ex)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ExceptionDto.builder()
                        .status(500)
                        .message("Internal Server Error")
                        .build()
                )
            }

        return ResponseEntity.status(status.value).body(
            ExceptionDto.builder()
                .status(status.value.value())
                .message(ex.message)
                .build()
        )
    }
}
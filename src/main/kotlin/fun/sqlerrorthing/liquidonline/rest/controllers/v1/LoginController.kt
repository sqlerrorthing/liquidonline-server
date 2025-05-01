package `fun`.sqlerrorthing.liquidonline.rest.controllers.v1

import `fun`.sqlerrorthing.liquidonline.dtos.AuthDto
import `fun`.sqlerrorthing.liquidonline.dtos.TokenDto
import `fun`.sqlerrorthing.liquidonline.exceptions.UserNotFoundException
import `fun`.sqlerrorthing.liquidonline.extensions.toTokenDto
import `fun`.sqlerrorthing.liquidonline.services.user.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class LoginController(private val userService: UserService) {
    @PostMapping("/auth")
    fun auth(@Valid @RequestBody authDto: AuthDto): TokenDto {
        val user = userService.findUserByUsernameAndPassword(
            authDto.username,
            authDto.password
        ) ?: throw UserNotFoundException

        return user.toTokenDto()
    }
}
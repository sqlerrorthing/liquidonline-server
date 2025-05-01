package `fun`.sqlerrorthing.liquidonline.rest.controllers.v1

import `fun`.sqlerrorthing.liquidonline.dtos.RegisterDto
import `fun`.sqlerrorthing.liquidonline.dtos.TokenDto
import `fun`.sqlerrorthing.liquidonline.extensions.toTokenDto
import `fun`.sqlerrorthing.liquidonline.services.user.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class RegisterController(private val userService: UserService) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody registerDto: RegisterDto): TokenDto {
        val user = userService.registerUser(registerDto.username, registerDto.password)
        return user.toTokenDto()
    }
}
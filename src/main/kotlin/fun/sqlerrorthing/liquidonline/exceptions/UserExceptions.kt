@file:Suppress("MatchingDeclarationName")
package `fun`.sqlerrorthing.liquidonline.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
object UserNotFoundException : RuntimeException(
    "User not found exception"
) {
    private fun readResolve(): Any = UserNotFoundException
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
object UsernameAlreadyTakenException : RuntimeException(
    "Username already taken"
) {
    private fun readResolve(): Any = UsernameAlreadyTakenException
}
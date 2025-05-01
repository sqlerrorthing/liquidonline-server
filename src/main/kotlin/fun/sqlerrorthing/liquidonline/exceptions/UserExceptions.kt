@file:Suppress("MatchingDeclarationName")
package `fun`.sqlerrorthing.liquidonline.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
object UserNotFoundException : RuntimeException(
    "User not found"
) {
    private fun readResolve(): Any = UserNotFoundException
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
object UsernameAlreadyTakenException : RuntimeException(
    "Username already taken"
) {
    private fun readResolve(): Any = UsernameAlreadyTakenException
}
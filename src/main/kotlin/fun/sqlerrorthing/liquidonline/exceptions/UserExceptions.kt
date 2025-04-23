package `fun`.sqlerrorthing.liquidonline.exceptions

object UserNotFoundException : RuntimeException() {
    private fun readResolve(): Any = UserNotFoundException
}
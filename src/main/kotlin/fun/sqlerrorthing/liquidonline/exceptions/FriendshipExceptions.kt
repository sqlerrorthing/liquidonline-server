@file:Suppress("MatchingDeclarationName")
package `fun`.sqlerrorthing.liquidonline.exceptions

object WasNoFriendship : RuntimeException() {
    private fun readResolve(): Any = WasNoFriendship
}

package `fun`.sqlerrorthing.liquidonline.exceptions

object AlreadyInPartyException: RuntimeException() {
    private fun readResolve(): Any = AlreadyInPartyException
}

object AlreadyInThisPartyException: RuntimeException() {
    private fun readResolve(): Any = AlreadyInThisPartyException
}

object PartyHasNoMembers: RuntimeException() {
    private fun readResolve(): Any = PartyHasNoMembers
}

object NoEnoughPartyPermissions: RuntimeException() {
    private fun readResolve(): Any = NoEnoughPartyPermissions
}

object MemberInAnotherPartyException: RuntimeException() {
    private fun readResolve(): Any = MemberInAnotherPartyException
}
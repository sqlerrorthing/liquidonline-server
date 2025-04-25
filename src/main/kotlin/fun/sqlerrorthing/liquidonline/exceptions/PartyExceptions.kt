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

object NotEnoughPartyPermissions: RuntimeException() {
    private fun readResolve(): Any = NotEnoughPartyPermissions
}

object MemberInAnotherPartyException: RuntimeException() {
    private fun readResolve(): Any = MemberInAnotherPartyException
}

object AlreadyInvitedException: RuntimeException() {
    private fun readResolve(): Any = AlreadyInvitedException
}


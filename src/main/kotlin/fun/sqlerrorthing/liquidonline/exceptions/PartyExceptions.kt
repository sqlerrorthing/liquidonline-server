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

object NotEnoughPartyPermissionsException: RuntimeException() {
    private fun readResolve(): Any = NotEnoughPartyPermissionsException
}

object MemberInAnotherPartyException: RuntimeException() {
    private fun readResolve(): Any = MemberInAnotherPartyException
}

object AlreadyInvitedException: RuntimeException() {
    private fun readResolve(): Any = AlreadyInvitedException
}

object InviteNotFoundException: RuntimeException() {
    private fun readResolve(): Any = InviteNotFoundException
}

object PartyMemberNotFoundException: RuntimeException() {
    private fun readResolve(): Any = PartyMemberNotFoundException
}

object PartyMembersLimitException: RuntimeException() {
    private fun readResolve(): Any = PartyMembersLimitException
}

object PartyMemberInMarkersRateLimitException: RuntimeException() {
    private fun readResolve(): Any = PartyMemberInMarkersRateLimitException
}

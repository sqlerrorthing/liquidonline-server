package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners

import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.extensions.toInvitedMemberDto
import `fun`.sqlerrorthing.liquidonline.extensions.toPartyDto
import `fun`.sqlerrorthing.liquidonline.packets.c2s.party.*
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.*
import `fun`.sqlerrorthing.liquidonline.services.party.PartyService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.listener.PacketMessageListener
import `fun`.sqlerrorthing.liquidonline.ws.listener.WebSocketMessageListener
import org.springframework.stereotype.Component

@Component
@WebSocketMessageListener
@Suppress("unused")
class PartyListener(
    private val partyService: PartyService
) {
    @PacketMessageListener
    fun createParty(userSession: UserSession, packet: C2SCreateParty): S2CCreatePartyResult {
        return try {
            val party = partyService.createParty(packet.name, userSession, packet.playData)

            S2CCreatePartyResult.builder()
                .result(S2CCreatePartyResult.Result.CREATED)
                .party(party.toPartyDto())
                .build()
        } catch (_: AlreadyInPartyException) {
            S2CCreatePartyResult.builder()
                .result(S2CCreatePartyResult.Result.ALREADY_IN_PARTY)
                .build()
        }
    }

    @PacketMessageListener
    fun leaveParty(userSession: UserSession, packet: C2SPartyLeave) {
        val (party, member) = userSession.activeParty ?: return
        partyService.removePartyMember(party, member)
    }

    @PacketMessageListener
    fun disbandParty(userSession: UserSession, packet: C2SPartyDisband) {
        val (party, member) = userSession.activeParty ?: return

        try {
            partyService.disbandPartyRequested(party, member)
        } catch (_: NotEnoughPartyPermissionsExceptions) {}
    }

    @PacketMessageListener
    fun invitePartyMember(userSession: UserSession, packet: C2SInvitePartyMember): S2CInvitePartyMemberResult {
        val (party, member) = userSession.activeParty ?: return S2CInvitePartyMemberResult.builder()
            .result(S2CInvitePartyMemberResult.Result.NOT_IN_A_PARTY)
            .build()

        return try {
            val invite = partyService.createInvite(party, member, packet.username)

            S2CInvitePartyMemberResult.builder()
                .result(S2CInvitePartyMemberResult.Result.INVITED)
                .invite(invite.toInvitedMemberDto())
                .build()
        } catch (ex: RuntimeException) {
            when (ex) {
                is UserNotFoundException -> S2CInvitePartyMemberResult.Result.NOT_FOUND
                is NotEnoughPartyPermissionsExceptions -> S2CInvitePartyMemberResult.Result.NOT_ENOUGH_RIGHTS
                is AlreadyInPartyException,
                is AlreadyInvitedException -> S2CInvitePartyMemberResult.Result.ALREADY_IN_A_PARTY
                else -> throw ex
            }.let { result ->
                S2CInvitePartyMemberResult.builder()
                    .result(result)
                    .build()
            }
        }
    }

    @PacketMessageListener
    fun inviteResponse(userSession: UserSession, packet: C2SPartyInviteResponse): S2CPartyInviteResponseStatus {
        return try {
            when (packet.response) {
                C2SPartyInviteResponse.Response.ACCEPTED -> {
                    val party = partyService.inviteAccepted(packet.inviteUuid, userSession, packet.play)

                    S2CPartyInviteResponseStatus.builder()
                        .party(party)
                        .result(S2CPartyInviteResponseStatus.Result.SUCCESS)
                        .build()
                }

                C2SPartyInviteResponse.Response.DECLINED -> {
                    partyService.inviteDeclined(userSession, packet.inviteUuid)

                    S2CPartyInviteResponseStatus.builder()
                        .result(S2CPartyInviteResponseStatus.Result.SUCCESS)
                        .build()
                }
            }
        } catch (ex: RuntimeException) {
            when (ex) {
                is InviteNotFoundException -> S2CPartyInviteResponseStatus.Result.INVITE_NOT_FOUND
                is PartyMembersLimitException -> S2CPartyInviteResponseStatus.Result.PARTY_MEMBERS_LIMIT
                is MemberInAnotherPartyException -> S2CPartyInviteResponseStatus.Result.ALREADY_IN_ANOTHER_PARTY
                else -> throw ex
            }.let { result ->
                S2CPartyInviteResponseStatus.builder()
                    .result(result)
                    .build()
            }
        }
    }

    @PacketMessageListener
    fun kickPartyMember(userSession: UserSession, packet: C2SKickPartyMember): S2CPartyMemberKickResult {
        return try {
            val (party, member) = requireNotNull(userSession.activeParty) {
                PartyMemberNotFoundException
            }

            partyService.kickPartyMember(party, member, packet.memberId)

            S2CPartyMemberKickResult.builder()
                .result(S2CPartyMemberKickResult.Result.KICKED)
                .build()
        } catch (ex: RuntimeException) {
            when (ex) {
                is NotEnoughPartyPermissionsExceptions -> S2CPartyMemberKickResult.Result.NO_ENOUGH_RIGHTS
                is PartyMemberNotFoundException -> S2CPartyMemberKickResult.Result.NOT_FOUND
                else -> throw ex
            }.let { result ->
                S2CPartyMemberKickResult.builder()
                    .result(result)
                    .build()
            }
        }
    }

    @PacketMessageListener
    fun transferOwnership(userSession: UserSession, packet: C2STransferPartyOwnership) {
        val (party, member) = userSession.activeParty ?: return

        try {
            partyService.transferPartyOwnership(party, member, packet.memberId)
        } catch (ex: RuntimeException) {
            when (ex) {
                is NotEnoughPartyPermissionsExceptions,
                is PartyMemberNotFoundException -> {}
                else -> throw ex
            }
        }
    }

    @PacketMessageListener
    fun partyPlayUpdate(userSession: UserSession, packet: C2SPartyPlayUpdate) {
        val (party, member) = userSession.activeParty ?: return

        try {
            partyService.memberPlayDataUpdate(party, member, packet.data)
        } catch (_: MemberInAnotherPartyException) {}
    }

    @PacketMessageListener
    fun partySetMarkerRequest(userSession: UserSession, packet: C2SPartySetMarker): S2CPartySetMarkerResult {
        return try {
            val (party, member) = requireNotNull(userSession.activeParty) {
                PartyMemberNotFoundException
            }

            partyService.partyMarker(party, member, packet.marker)

            S2CPartySetMarkerResult.builder()
                .result(S2CPartySetMarkerResult.Result.INSTALLED)
                .build()
        } catch (ex: Exception) {
            when (ex) {
                is PartyMemberNotFoundException,
                is MemberInAnotherPartyException -> S2CPartySetMarkerResult.Result.NOT_IN_A_PARTY
                is PartyMemberInMarkersRateLimitException -> S2CPartySetMarkerResult.Result.LIMIT
                else -> throw ex
            }.let { result ->
                S2CPartySetMarkerResult.builder()
                    .result(result)
                    .build()
            }
        }
    }
}

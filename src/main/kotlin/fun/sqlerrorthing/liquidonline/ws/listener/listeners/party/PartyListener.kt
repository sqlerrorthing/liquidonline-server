package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners.party

import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.extensions.toInvitedMemberDto
import `fun`.sqlerrorthing.liquidonline.extensions.toPartyDto
import `fun`.sqlerrorthing.liquidonline.packets.c2s.party.*
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CCreatePartyResult
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CInvitePartyMemberResult
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyInviteResponseStatus
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
    private fun createParty(userSession: UserSession, packet: C2SCreateParty): S2CCreatePartyResult {
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
    private fun leaveParty(userSession: UserSession, packet: C2SPartyLeave) {
        val (party, member) = userSession.activeParty ?: return

        try {
            partyService.removePartyMember(party, member)
        } catch (_: MemberInAnotherPartyException) {}
    }

    @PacketMessageListener
    private fun disbandParty(userSession: UserSession, packet: C2SPartyDisband) {
        val (party, member) = userSession.activeParty ?: return

        try {
            partyService.disbandPartyRequested(party, member)
        } catch (_: NotEnoughPartyPermissionsExceptions) {}
    }

    @PacketMessageListener
    private fun invitePartyMember(userSession: UserSession, packet: C2SInvitePartyMember): S2CInvitePartyMemberResult {
        val (party, member) = userSession.activeParty ?: return S2CInvitePartyMemberResult.builder()
            .result(S2CInvitePartyMemberResult.Result.NOT_IN_A_PARTY)
            .build()

        return try {
            val invite = partyService.createInvite(party, member, packet.username)

            S2CInvitePartyMemberResult.builder()
                .result(S2CInvitePartyMemberResult.Result.INVITED)
                .invite(invite.toInvitedMemberDto())
                .build()
        } catch (_: UserNotFoundException) {
            S2CInvitePartyMemberResult.builder()
                .result(S2CInvitePartyMemberResult.Result.NOT_FOUND)
                .build()
        } catch (_: NotEnoughPartyPermissionsExceptions) {
            S2CInvitePartyMemberResult.builder()
                .result(S2CInvitePartyMemberResult.Result.NOT_ENOUGH_RIGHTS)
                .build()
        } catch (_: AlreadyInPartyException) {
            S2CInvitePartyMemberResult.builder()
                .result(S2CInvitePartyMemberResult.Result.ALREADY_IN_A_PARTY)
                .build()
        } catch (_: AlreadyInvitedException) {
            S2CInvitePartyMemberResult.builder()
                .result(S2CInvitePartyMemberResult.Result.ALREADY_IN_A_PARTY)
                .build()
        }
    }

    @PacketMessageListener
    private fun inviteResponse(userSession: UserSession, packet: C2SPartyInviteResponse): S2CPartyInviteResponseStatus {
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
        } catch (_: InviteNotFoundException) {
            S2CPartyInviteResponseStatus.builder()
                .result(S2CPartyInviteResponseStatus.Result.INVITE_NOT_FOUND)
                .build()
        } catch (_: PartyMembersLimitException) {
            S2CPartyInviteResponseStatus.builder()
                .result(S2CPartyInviteResponseStatus.Result.PARTY_MEMBERS_LIMIT)
                .build()
        } catch (_: MemberInAnotherPartyException) {
            S2CPartyInviteResponseStatus.builder()
                .result(S2CPartyInviteResponseStatus.Result.ALREADY_IN_ANOTHER_PARTY)
                .build()
        }
    }
}

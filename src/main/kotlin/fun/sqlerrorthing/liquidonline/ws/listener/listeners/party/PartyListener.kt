package `fun`.sqlerrorthing.liquidonline.ws.listener.listeners.party

import `fun`.sqlerrorthing.liquidonline.exceptions.*
import `fun`.sqlerrorthing.liquidonline.extensions.toInvitedMemberDto
import `fun`.sqlerrorthing.liquidonline.extensions.toPartyDto
import `fun`.sqlerrorthing.liquidonline.packets.c2s.party.C2SCreateParty
import `fun`.sqlerrorthing.liquidonline.packets.c2s.party.C2SInvitePartyMember
import `fun`.sqlerrorthing.liquidonline.packets.c2s.party.C2SPartyDisband
import `fun`.sqlerrorthing.liquidonline.packets.c2s.party.C2SPartyLeave
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CCreatePartyResult
import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CInvitePartyMemberResult
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
        } catch (_: NotEnoughPartyPermissions) {}
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
        } catch (_: NotEnoughPartyPermissions) {
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
        } catch (_: MemberInAnotherPartyException) {
            error("Unreachable here")
        }
    }
}

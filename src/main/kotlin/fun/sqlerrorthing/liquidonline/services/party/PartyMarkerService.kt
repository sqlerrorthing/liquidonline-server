package `fun`.sqlerrorthing.liquidonline.services.party

import `fun`.sqlerrorthing.liquidonline.dto.play.MarkerDto
import `fun`.sqlerrorthing.liquidonline.exceptions.PartyMemberInMarkersRateLimitException
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember

interface PartyMarkerService {
    @Throws(PartyMemberInMarkersRateLimitException::class)
    fun addMarker(party: Party, member: PartyMember, marker: MarkerDto)
}

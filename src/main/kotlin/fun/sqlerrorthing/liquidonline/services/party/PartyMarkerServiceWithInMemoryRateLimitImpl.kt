package `fun`.sqlerrorthing.liquidonline.services.party

import `fun`.sqlerrorthing.liquidonline.dto.play.MarkerDto
import `fun`.sqlerrorthing.liquidonline.exceptions.PartyMemberInMarkersRateLimitException
import `fun`.sqlerrorthing.liquidonline.extensions.id
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class InMemoryPartyMarkerServiceImpl(
    private val partyNotifierService: PartyNotifierService
) : PartyMarkerService {
    private val memberMarkers = ConcurrentHashMap<Int, MemberMarkers>()


    override fun addMarker(
        party: Party,
        member: PartyMember,
        marker: MarkerDto
    ) {
        val currentTime = System.currentTimeMillis()
        val markers = memberMarkers.computeIfAbsent(member.id) { MemberMarkers() }
        val (lastTime, count) = markers

        if (System.currentTimeMillis() - lastTime > 1000) {
            markers.markerCount = 0
        }

        if (markers.markerCount >= 4) {
            if (currentTime - lastTime < 5000) {
                throw PartyMemberInMarkersRateLimitException
            } else {
                markers.markerCount = 0
            }
        }

        markers.lastMarkerTime = currentTime
        markers.markerCount = count + 1

        partyNotifierService.notifyNewMarker(party, member, marker)
    }
}

private data class MemberMarkers(
    var lastMarkerTime: Long = 0,
    var markerCount: Int = 0
)
package fun.sqlerrorthing.liquidonline.session;

import fun.sqlerrorthing.liquidonline.SharedConstants;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Party {
    @NotNull
    UUID uuid;

    @NotNull
    String name;

    @Builder.Default
    boolean isPublic = false;

    @NotNull
    PartyMember owner;

    @NotNull
    @Builder.Default
    List<PartyMember> members = new ArrayList<>(SharedConstants.MAX_PARTY_MEMBERS_LIMIT);

    @NotNull
    @Builder.Default
    List<InvitedMember> invitedMembers = new ArrayList<>(SharedConstants.MAX_PARTY_MEMBERS_LIMIT);

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Party party = (Party) other;
        return uuid.equals(party.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}

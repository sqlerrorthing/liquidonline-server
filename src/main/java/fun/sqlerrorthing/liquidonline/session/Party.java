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
}

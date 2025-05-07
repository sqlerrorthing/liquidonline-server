package fun.sqlerrorthing.liquidonline.session;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvitedMember {
    @NotNull
    @Builder.Default
    UUID uuid = UUID.randomUUID();

    @NotNull
    UserSession invited;

    @NotNull
    UserSession sender;
}

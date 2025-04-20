package fun.sqlerrorthing.liquidonline.session;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyMember {
    @NotNull
    UserSession userSession;

    @NotNull
    Color color;
}

package fun.sqlerrorthing.liquidonline.session;

import fun.sqlerrorthing.liquidonline.dto.play.PlayDto;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    PlayDto playData;

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        PartyMember that = (PartyMember) other;
        return userSession.equals(that.userSession);
    }

    @Override
    public int hashCode() {
        return userSession.hashCode();
    }
}

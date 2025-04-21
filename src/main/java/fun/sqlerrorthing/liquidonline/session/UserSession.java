package fun.sqlerrorthing.liquidonline.session;

import fun.sqlerrorthing.liquidonline.entities.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;


@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSession implements Principal {
    @NotNull
    UserEntity user;

    @NotNull
    String minecraftUsername;

    @Nullable
    String server;

    @NotNull
    byte[] skin; // 16x16 png head

    @NotNull
    WebSocketSession wsSession;

    @Nullable
    Party activeParty;

    @Override
    public String getName() {
        return user.getName();
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        UserSession userSession = (UserSession) other;
        return user.equals(userSession.user);
    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }
}

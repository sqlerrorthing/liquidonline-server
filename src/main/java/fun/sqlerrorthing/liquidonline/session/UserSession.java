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
    WebSocketSession session;

    @Override
    public String getName() {
        return user.getName();
    }
}

package fun.sqlerrorthing.liquidonline.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenDto {
    @NotNull
    @jakarta.validation.constraints.NotNull
    String token;
}

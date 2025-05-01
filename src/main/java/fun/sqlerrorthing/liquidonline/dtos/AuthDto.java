package fun.sqlerrorthing.liquidonline.dtos;

import fun.sqlerrorthing.liquidonline.SharedConstants;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthDto {
    @NotNull
    @jakarta.validation.constraints.NotNull
    @Pattern(
        regexp = SharedConstants.USERNAME_PATTERN,
        message = "The username does not validate against this regular expression: " + SharedConstants.USERNAME_PATTERN
    ) String username;

    @NotNull
    @jakarta.validation.constraints.NotNull
    @Size(min = 8, max = 64, message = "The password length must be between 8 and 64 characters")
    String password;
}

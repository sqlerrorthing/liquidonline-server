package fun.sqlerrorthing.liquidonline.dtos;

import fun.sqlerrorthing.liquidonline.SharedConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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
    @Min(value = 8, message = "The min password length is 8")
    @Max(value = 64, message = "The max password length is 64")
    String password;
}

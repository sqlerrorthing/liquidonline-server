package fun.sqlerrorthing.liquidonline.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExceptionDto {
    int status;

    String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, String> details;
}

package fun.sqlerrorthing.liquidonline.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartySettingsDto {
    boolean partyPublic;
}

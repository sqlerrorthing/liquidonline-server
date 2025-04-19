package fun.sqlerrorthing.liquidonline.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.security.Principal;

@Entity
@Table(name = "users")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity implements Principal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    Integer id;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^\\w{3,16}$", message = "Username must be alphanumeric and between 3 and 16 characters")
    @NotNull
    String username;

    @Column(nullable = false)
    String passwordHash;

    @Column(nullable = false)
    @Builder.Default
    String token = RandomStringUtils.secure().next(32, true, true);

    @Override
    public String getName() {
        return username;
    }
}

package fun.sqlerrorthing.liquidonline.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.security.Principal;
import java.time.Instant;

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

    @Column(nullable = false)
    @Builder.Default
    Instant lastLogin = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    Instant created = Instant.now();

    @Override
    public String getName() {
        return username;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        UserEntity that = (UserEntity) other;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}

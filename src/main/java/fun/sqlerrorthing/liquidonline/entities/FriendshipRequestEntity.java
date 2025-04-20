package fun.sqlerrorthing.liquidonline.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "friendship_requests")
public class FriendshipRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    UserEntity receiver;
}
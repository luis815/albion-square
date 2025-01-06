package studio.snowfox.albionsquare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "albion_online_spell", schema = "albion_square")
public class AlbionOnlineSpell {
    @Id
    @Column(name = "unique_name", nullable = false, length = Integer.MAX_VALUE)
    private String uniqueName;

    @NotNull
    @Column(name = "sha", nullable = false, length = Integer.MAX_VALUE)
    private String sha;

    @Column(
            name = "create_timestamp",
            columnDefinition = "TIMESTAMP WITH TIME ZONE",
            nullable = false,
            updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private OffsetDateTime createTimestamp;

    @Column(name = "update_timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private OffsetDateTime updateTimestamp;
}

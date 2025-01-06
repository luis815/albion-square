package studio.snowfox.albionsquare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "adp_meta_sync_log", schema = "albion_square")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdpMetaSyncLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "sha", nullable = false, length = Integer.MAX_VALUE)
    private String sha;

    @Column(name = "status", columnDefinition = "generic_status not null")
    @Enumerated(EnumType.STRING)
    private GenericStatus status;

    @Column(name = "raw_adp_items")
    @JdbcTypeCode(SqlTypes.SQLXML)
    private String rawAdpItems;

    @Column(name = "raw_adp_tmx")
    @JdbcTypeCode(SqlTypes.SQLXML)
    private String rawAdpTmx;

    @Column(name = "raw_adp_spells")
    @JdbcTypeCode(SqlTypes.SQLXML)
    private String rawAdpSpells;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(
            name = "create_timestamp",
            columnDefinition = "TIMESTAMP WITH TIME ZONE",
            nullable = false,
            updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp createTimestamp;

    @Column(name = "update_timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Timestamp updateTimestamp;
}

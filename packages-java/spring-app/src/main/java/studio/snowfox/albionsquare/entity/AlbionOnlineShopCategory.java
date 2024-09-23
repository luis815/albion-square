package studio.snowfox.albionsquare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "albion_online_shop_category", schema = "albion_square")
public class AlbionOnlineShopCategory {
    @EmbeddedId
    private AlbionOnlineShopCategoryId id;

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
    private Timestamp createTimestamp;

    @Column(name = "update_timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Timestamp updateTimestamp;
}

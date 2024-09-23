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
@Table(name = "albion_online_item", schema = "albion_square")
public class AlbionOnlineItem {
    @Id
    @Column(name = "unique_name", nullable = false, length = Integer.MAX_VALUE)
    private String uniqueName;

    @NotNull
    @Column(name = "tier", nullable = false)
    private Short tier;

    @NotNull
    @Column(name = "enchantment_level_0", nullable = false)
    private Boolean enchantmentLevel0 = false;

    @NotNull
    @Column(name = "enchantment_level_1", nullable = false)
    private Boolean enchantmentLevel1 = false;

    @NotNull
    @Column(name = "enchantment_level_2", nullable = false)
    private Boolean enchantmentLevel2 = false;

    @NotNull
    @Column(name = "enchantment_level_3", nullable = false)
    private Boolean enchantmentLevel3 = false;

    @NotNull
    @Column(name = "enchantment_level_4", nullable = false)
    private Boolean enchantmentLevel4 = false;

    @NotNull
    @Column(name = "max_quality", nullable = false)
    private Short maxQuality;

    @NotNull
    @Column(name = "shop_category", nullable = false, length = Integer.MAX_VALUE)
    private String shopCategory;

    @NotNull
    @Column(name = "shop_sub_category", nullable = false, length = Integer.MAX_VALUE)
    private String shopSubCategory;

    @NotNull
    @Column(name = "sha", nullable = false, length = Integer.MAX_VALUE)
    private String sha;

    @Column(name = "asset")
    private Boolean asset;

    @Column(name = "asset_src", length = Integer.MAX_VALUE)
    private String assetSrc;

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

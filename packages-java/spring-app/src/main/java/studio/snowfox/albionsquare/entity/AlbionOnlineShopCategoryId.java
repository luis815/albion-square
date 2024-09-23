package studio.snowfox.albionsquare.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Embeddable
public class AlbionOnlineShopCategoryId implements Serializable {
    private static final long serialVersionUID = 1726043781319600001L;

    @NotNull
    @Column(name = "shop_category", nullable = false, length = Integer.MAX_VALUE)
    private String shopCategory;

    @NotNull
    @Column(name = "shop_sub_category", nullable = false, length = Integer.MAX_VALUE)
    private String shopSubCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AlbionOnlineShopCategoryId entity = (AlbionOnlineShopCategoryId) o;
        return Objects.equals(this.shopSubCategory, entity.shopSubCategory)
                && Objects.equals(this.shopCategory, entity.shopCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopSubCategory, shopCategory);
    }
}

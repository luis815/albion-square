package studio.snowfox.albionsquare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.snowfox.albionsquare.entity.AlbionOnlineShopCategory;
import studio.snowfox.albionsquare.entity.AlbionOnlineShopCategoryId;

public interface AlbionOnlineShopCategoryRepository
        extends JpaRepository<AlbionOnlineShopCategory, AlbionOnlineShopCategoryId> {
    void deleteAllByShaNot(String shaNot);
}

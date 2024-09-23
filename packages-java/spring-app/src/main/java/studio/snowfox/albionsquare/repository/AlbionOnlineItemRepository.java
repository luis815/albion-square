package studio.snowfox.albionsquare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.snowfox.albionsquare.entity.AlbionOnlineItem;

public interface AlbionOnlineItemRepository extends JpaRepository<AlbionOnlineItem, Long> {
    public void deleteAllByShaNot(String shaNot);
}

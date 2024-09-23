package studio.snowfox.albionsquare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.snowfox.albionsquare.entity.AlbionOnlineLocalization;

public interface AlbionOnlineLocalizationRepository extends JpaRepository<AlbionOnlineLocalization, Long> {
    public void deleteAllByShaNot(String shaNot);
}

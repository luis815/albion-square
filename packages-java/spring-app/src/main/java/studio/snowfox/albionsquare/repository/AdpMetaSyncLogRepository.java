package studio.snowfox.albionsquare.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import studio.snowfox.albionsquare.entity.AdpMetaSyncLog;

public interface AdpMetaSyncLogRepository extends JpaRepository<AdpMetaSyncLog, Long> {
    Optional<AdpMetaSyncLog> findBySha(String sha);
}

package studio.snowfox.albionsquare.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import studio.snowfox.albionsquare.entity.AdpMetaLog;

public interface AdpMetaLogRepository extends JpaRepository<AdpMetaLog, Long> {
    public Optional<AdpMetaLog> findBySha(String sha);
}

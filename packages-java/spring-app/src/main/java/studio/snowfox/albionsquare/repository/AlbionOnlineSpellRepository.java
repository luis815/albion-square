package studio.snowfox.albionsquare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.snowfox.albionsquare.entity.AlbionOnlineSpell;

public interface AlbionOnlineSpellRepository extends JpaRepository<AlbionOnlineSpell, String> {
    void deleteAllByShaNot(String shaNot);
}

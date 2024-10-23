package studio.snowfox.albionsquare.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import studio.snowfox.albionsquare.service.GameDataSyncService;

@Component
@Log
@RequiredArgsConstructor
public class GameDataSyncSchedule {
    private final GameDataSyncService gameDataSyncService;

    @Scheduled(cron = "0 0 6 * * *")
    public void run() {
        log.info("Triggered scheduled ADP meta sync");
        this.gameDataSyncService.processAll();
    }
}

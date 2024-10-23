package studio.snowfox.albionsquare.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.snowfox.albionsquare.service.AdpMetaSyncService;
import studio.snowfox.albionsquare.service.AowAssetSyncService;
import studio.snowfox.albionsquare.service.GameDataSyncService;

@Log
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameDataSyncController {
    private final AdpMetaSyncService adpMetaSyncService;
    private final AowAssetSyncService aowAssetSyncService;
    private final GameDataSyncService gameDataSyncService;

    @PostMapping("/trigger-adp-meta-sync")
    public ResponseEntity<String> triggerAdpMetaSync() {
        log.info("Triggered ADP meta sync");
        this.adpMetaSyncService.processAllAsync();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/trigger-aow-asset-sync")
    public ResponseEntity<String> triggerAowAssetSync() {
        log.info("Triggered Albion Online Wiki asset sync");
        this.aowAssetSyncService.processAllAsync();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/trigger-game-data-sync")
    public ResponseEntity<String> triggerGameDataSync() {
        log.info("Triggered game data sync");
        this.gameDataSyncService.processAllAsync();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}

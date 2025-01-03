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

    @PostMapping("/adp-meta-sync")
    public ResponseEntity<String> adpMetaSync() {
        log.info("Game data ADP meta sync");
        this.adpMetaSyncService.loadAsync();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/aow-asset-sync")
    public ResponseEntity<String> aowAssetSync() {
        log.info("Game data Albion Online Wiki asset sync");
        this.aowAssetSyncService.processAllAsync();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/game-data-sync")
    public ResponseEntity<String> sync() {
        log.info("Game data Sync");
        this.gameDataSyncService.processAllAsync();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}

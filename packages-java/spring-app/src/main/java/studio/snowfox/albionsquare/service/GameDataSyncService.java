package studio.snowfox.albionsquare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Log
@Service
@RequiredArgsConstructor
public class GameDataSyncService {
    private final AdpMetaSyncService adpMetaSyncService;
    private final AowAssetSyncService aowAssetSyncService;

    public void processAll() {
        this.adpMetaSyncService.load();
        this.aowAssetSyncService.processAll();
    }

    @Async
    public void processAllAsync() {
        this.adpMetaSyncService.load();
        this.aowAssetSyncService.processAll();
    }
}

package studio.snowfox.albionsquare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import studio.snowfox.albionsquare.component.AdpMetaSyncComponent;

@Log
@Service
@RequiredArgsConstructor
public class AdpMetaSyncService {
    private static final String ADP_META_SYNC_LOAD_LOCK_KEY = "ADP_META_SYNC_PROCESS_ALL_LOCK_KEY";

    private final LockRegistry lockRegistry;
    private final AdpMetaSyncComponent adpMetaSyncComponent;

    public void load() {
        this.handleLoad();
    }

    @Async
    public void loadAsync() {
        this.handleLoad();
    }

    private void handleLoad() {
        try {
            this.lockRegistry.executeLocked(ADP_META_SYNC_LOAD_LOCK_KEY, () -> {
                log.info(String.format("Executing locked with %s", ADP_META_SYNC_LOAD_LOCK_KEY));
                this.adpMetaSyncComponent.load();
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

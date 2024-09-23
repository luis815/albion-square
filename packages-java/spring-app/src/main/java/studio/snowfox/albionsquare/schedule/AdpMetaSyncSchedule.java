package studio.snowfox.albionsquare.schedule;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import studio.snowfox.albionsquare.service.AdpMetaSyncService;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
@Log
@RequiredArgsConstructor
public class AdpMetaSyncSchedule {
    private final AdpMetaSyncService adpMetaSyncService;

    @Scheduled(cron = "0 0 6 * * *")
    public void run() throws JAXBException, URISyntaxException, IOException {
        log.info("Triggered scheduled ADP meta sync");
        adpMetaSyncService.processAll();
    }
}

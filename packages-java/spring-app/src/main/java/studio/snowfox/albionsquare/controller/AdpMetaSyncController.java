package studio.snowfox.albionsquare.controller;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.snowfox.albionsquare.service.AdpMetaSyncService;

@Log
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdpMetaSyncController {
    private final AdpMetaSyncService adpMetaSyncService;

    @PostMapping("/trigger-adp-meta-sync")
    public ResponseEntity<String> triggerAdpMetaSync() throws JAXBException, URISyntaxException, IOException {
        log.info("Triggered scheduled ADP meta sync");
        adpMetaSyncService.processAll();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}

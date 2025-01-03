package studio.snowfox.albionsquare.component;

import com.albion_online_data.ao_bin_dumps.Items;
import com.albion_online_data.ao_bin_dumps.Tmx;
import jakarta.xml.bind.JAXBContext;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import studio.snowfox.albionsquare.entity.AdpMetaSyncLog;
import studio.snowfox.albionsquare.entity.GenericStatus;
import studio.snowfox.albionsquare.json.GitHubCommitMetaJson;
import studio.snowfox.albionsquare.repository.AdpMetaSyncLogRepository;

@Log
@Component
@RequiredArgsConstructor
public class AdpMetaSyncComponent {
    private final AdpMetaSyncGitHubComponent adpMetaSyncGitHubComponent;
    private final AdpMetaSyncLogRepository adpMetaSyncLogRepository;
    private final AdpMetaSyncItemComponent adpMetaSyncItemComponent;
    private final AdpMetaSyncTmxComponent adpMetaSyncTmxComponent;

    public void load() {
        log.info("AdpMetaSyncComponent all start");

        GitHubCommitMetaJson latestGitHubCommitMetaJson;
        try {
            latestGitHubCommitMetaJson = this.adpMetaSyncGitHubComponent.fetchLatestGitHubCommitMeta();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        String sha = latestGitHubCommitMetaJson.getSha();

        AdpMetaSyncLog adpMetaSyncLog = this.adpMetaSyncLogRepository
                .findBySha(sha)
                .orElse(AdpMetaSyncLog.builder()
                        .sha(sha)
                        .status(GenericStatus.PENDING)
                        .build());

        switch (adpMetaSyncLog.getStatus()) {
            case SUCCESS -> {
                log.info("Latest ADP dumps already processed successfully");
                return;
            }
            case FAILED -> {
                log.info("Latest ADP dumps previously failed - re-trying");
            }
            case IN_PROGRESS -> {
                log.info("Latest ADP dumps are already in progress");
                return;
            }
            case PENDING -> {
                log.info("New ADP dumps available - will continue processing");
                this.adpMetaSyncLogRepository.save(adpMetaSyncLog);
            }
            default -> {
                throw new RuntimeException("Status not supported");
            }
        }

        adpMetaSyncLog.setStatus(GenericStatus.IN_PROGRESS);
        this.adpMetaSyncLogRepository.save(adpMetaSyncLog);

        try {
            log.info("Processing items");

            Items items = this.adpMetaSyncGitHubComponent.fetchItemsByCommitHash(sha);

            StringWriter itemStringWriter = new StringWriter();
            JAXBContext.newInstance(Items.class).createMarshaller().marshal(items, itemStringWriter);
            adpMetaSyncLog.setRawAdpItems(itemStringWriter.toString());

            this.adpMetaSyncItemComponent.load(items, sha);

            log.info("Processing tmx");

            Tmx tmx = this.adpMetaSyncGitHubComponent.fetchTmxByCommitHash(sha);

            StringWriter tmxStringWriter = new StringWriter();
            JAXBContext.newInstance(Tmx.class).createMarshaller().marshal(tmx, tmxStringWriter);
            adpMetaSyncLog.setRawAdpTmx(tmxStringWriter.toString());

            this.adpMetaSyncTmxComponent.load(tmx, sha);
        } catch (Exception e) {
            adpMetaSyncLog.setStatus(GenericStatus.FAILED);
            adpMetaSyncLog.setDescription(ExceptionUtils.getStackTrace(e));
            this.adpMetaSyncLogRepository.save(adpMetaSyncLog);
            throw new RuntimeException(e);
        }

        adpMetaSyncLog.setStatus(GenericStatus.SUCCESS);
        this.adpMetaSyncLogRepository.save(adpMetaSyncLog);

        log.info("AdpMetaSyncComponent all end");
    }
}

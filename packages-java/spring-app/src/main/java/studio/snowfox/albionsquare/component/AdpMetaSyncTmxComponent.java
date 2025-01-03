package studio.snowfox.albionsquare.component;

import com.albion_online_data.ao_bin_dumps.Tmx;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;
import studio.snowfox.albionsquare.repository.AdpMetaSyncTmxTuRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineLocalizationRepository;

@Log
@Component
@RequiredArgsConstructor
public class AdpMetaSyncTmxComponent {
    private final AdpMetaSyncTmxTuRepository adpMetaSyncTmxTuRepository;
    private final AlbionOnlineLocalizationRepository albionOnlineLocalizationRepository;
    private final Job adpMetaSyncTmxJob;
    private final JobLauncher jobLauncher;

    public void load(Tmx tmx, String sha)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
                    JobParametersInvalidException, JobRestartException {
        this.adpMetaSyncTmxTuRepository.deleteAll();
        this.adpMetaSyncTmxTuRepository.saveAll(tmx.getBody().getTu());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("sha", sha)
                .addLocalDateTime("local.date.time", LocalDateTime.now())
                .toJobParameters();

        JobExecution jobExecution = this.jobLauncher.run(this.adpMetaSyncTmxJob, jobParameters);

        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            throw new RuntimeException("Job execution failed");
        }

        this.albionOnlineLocalizationRepository.deleteAllByShaNot(sha);
    }
}

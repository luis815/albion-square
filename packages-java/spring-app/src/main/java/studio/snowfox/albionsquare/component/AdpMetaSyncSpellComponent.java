package studio.snowfox.albionsquare.component;

import com.albion_online_data.ao_bin_dumps.Spells;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;
import studio.snowfox.albionsquare.repository.AdpMetaSyncSpellObjectRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineSpellRepository;

@Log
@Component
@RequiredArgsConstructor
public class AdpMetaSyncSpellComponent {
    private final AdpMetaSyncSpellObjectRepository adpMetaSyncSpellObjectRepository;
    private final AlbionOnlineSpellRepository albionOnlineSpellRepository;
    private final Job adpMetasyncSpellJob;
    private final JobLauncher jobLauncher;

    public void load(Spells spells, String sha)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
                    JobParametersInvalidException, JobRestartException {
        this.adpMetaSyncSpellObjectRepository.deleteAll();
        this.adpMetaSyncSpellObjectRepository.saveAll(spells.getActivespellOrPassivespellOrTogglespell());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("sha", sha)
                .addLocalDateTime("local.date.time", LocalDateTime.now())
                .toJobParameters();

        JobExecution jobExecution = this.jobLauncher.run(this.adpMetasyncSpellJob, jobParameters);

        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            throw new RuntimeException("Job execution failed");
        }

        this.albionOnlineSpellRepository.deleteAllByShaNot(sha);
    }
}

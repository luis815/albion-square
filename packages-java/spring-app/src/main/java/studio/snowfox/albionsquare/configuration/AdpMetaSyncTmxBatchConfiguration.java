package studio.snowfox.albionsquare.configuration;

import com.albion_online_data.ao_bin_dumps.Tu;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import studio.snowfox.albionsquare.batch.listener.GenericChunkListener;
import studio.snowfox.albionsquare.batch.processor.AdpMetaSyncTmxProcessor;
import studio.snowfox.albionsquare.entity.AlbionOnlineLocalization;
import studio.snowfox.albionsquare.repository.AdpMetaSyncTmxTuRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineLocalizationRepository;

@Configuration
@RequiredArgsConstructor
public class AdpMetaSyncTmxBatchConfiguration {
    private final AdpMetaSyncTmxTuRepository adpMetaSyncTmxTuRepository;
    private final AlbionOnlineLocalizationRepository albionOnlineLocalizationRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobRepository jobRepository;

    @Bean
    public GenericChunkListener adpMetaSyncTmxGenericChuckListener() {
        return new GenericChunkListener();
    }

    @Bean
    @StepScope
    public ListItemReader<Tu> adpMetaSyncTmxListItemReader() {
        return new ListItemReader<>(this.adpMetaSyncTmxTuRepository.findAll());
    }

    @Bean
    @StepScope
    public AdpMetaSyncTmxProcessor adpMetaSyncTmxProcessor() {
        return new AdpMetaSyncTmxProcessor();
    }

    @Bean
    public RepositoryItemWriter<AlbionOnlineLocalization> adpMetaSyncTmxRepositoryItemWriter() {
        RepositoryItemWriter<AlbionOnlineLocalization> repositoryItemWriter = new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(this.albionOnlineLocalizationRepository);
        return repositoryItemWriter;
    }

    @Bean
    public Step adpMetaSyncTmxStep() {
        return new StepBuilder("sync-localization-step", this.jobRepository)
                .<Tu, AlbionOnlineLocalization>chunk(1000, this.platformTransactionManager)
                .listener(this.adpMetaSyncTmxGenericChuckListener())
                .reader(this.adpMetaSyncTmxListItemReader())
                .processor(this.adpMetaSyncTmxProcessor())
                .writer(this.adpMetaSyncTmxRepositoryItemWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(1000)
                .noRetry(Exception.class)
                .build();
    }

    @Bean
    public Job adpMetaSyncTmxJob() {
        return new JobBuilder("sync-localization-job", this.jobRepository)
                .start(this.adpMetaSyncTmxStep())
                .build();
    }
}

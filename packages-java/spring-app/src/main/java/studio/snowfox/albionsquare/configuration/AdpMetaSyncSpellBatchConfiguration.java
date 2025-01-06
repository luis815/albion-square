package studio.snowfox.albionsquare.configuration;

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
import studio.snowfox.albionsquare.batch.processor.AdpMetaSyncSpellProcessor;
import studio.snowfox.albionsquare.entity.AlbionOnlineSpell;
import studio.snowfox.albionsquare.repository.AdpMetaSyncSpellObjectRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineSpellRepository;

@Configuration
@RequiredArgsConstructor
public class AdpMetaSyncSpellBatchConfiguration {
    private final AdpMetaSyncSpellObjectRepository adpMetaSyncSpellObjectRepository;
    private final AlbionOnlineSpellRepository albionOnlineSpellRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobRepository jobRepository;

    @Bean
    @StepScope
    public GenericChunkListener genericChunkListener() {
        return new GenericChunkListener();
    }

    @Bean
    @StepScope
    public ListItemReader<Object> adpMetaSyncSpellListItemReader() {
        return new ListItemReader<>(this.adpMetaSyncSpellObjectRepository.findAll());
    }

    @Bean
    @StepScope
    public AdpMetaSyncSpellProcessor adpMetaSyncSpellProcessor() {
        return new AdpMetaSyncSpellProcessor();
    }

    @Bean
    public RepositoryItemWriter<AlbionOnlineSpell> adpMetaSyncSpellRepositoryItemWriter() {
        RepositoryItemWriter<AlbionOnlineSpell> repositoryItemWriter = new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(this.albionOnlineSpellRepository);
        return repositoryItemWriter;
    }

    @Bean
    public Step adpMetaSyncSpellStep() {
        return new StepBuilder("adp-meta-sync-spell-step", this.jobRepository)
                .<Object, AlbionOnlineSpell>chunk(1000, this.platformTransactionManager)
                .listener(this.genericChunkListener())
                .reader(this.adpMetaSyncSpellListItemReader())
                .processor(this.adpMetaSyncSpellProcessor())
                .writer(this.adpMetaSyncSpellRepositoryItemWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(1000)
                .noRetry(Exception.class)
                .build();
    }

    @Bean
    public Job adpMetasyncSpellJob() {
        return new JobBuilder("adp-meta-sync-spell-job", this.jobRepository)
                .start(this.adpMetaSyncSpellStep())
                .build();
    }
}

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
import studio.snowfox.albionsquare.batch.processor.AdpMetaSyncItemProcessor;
import studio.snowfox.albionsquare.entity.AlbionOnlineItem;
import studio.snowfox.albionsquare.repository.AdpMetaSyncItemObjectRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineItemRepository;

@Configuration
@RequiredArgsConstructor
public class AdpMetaSyncItemBatchConfiguration {
    private final AdpMetaSyncItemObjectRepository adpMetaSyncItemObjectRepository;
    private final AlbionOnlineItemRepository albionOnlineItemRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobRepository jobRepository;

    @Bean
    public GenericChunkListener adpMetaSyncItemGenericChunkListener() {
        return new GenericChunkListener();
    }

    @Bean
    @StepScope
    public ListItemReader<Object> adpMetaSyncItemListItemReader() {
        return new ListItemReader<>(this.adpMetaSyncItemObjectRepository.findAll());
    }

    @Bean
    @StepScope
    public AdpMetaSyncItemProcessor adpMetaSyncItemProcessor() {
        return new AdpMetaSyncItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<AlbionOnlineItem> adpMetaSyncItemRepositoryItemWriter() {
        RepositoryItemWriter<AlbionOnlineItem> repositoryItemWriter = new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(this.albionOnlineItemRepository);
        return repositoryItemWriter;
    }

    @Bean
    public Step adpMetaSyncItemStep() {
        return new StepBuilder("sync-items-step", this.jobRepository)
                .<Object, AlbionOnlineItem>chunk(1000, this.platformTransactionManager)
                .listener(this.adpMetaSyncItemGenericChunkListener())
                .reader(this.adpMetaSyncItemListItemReader())
                .processor(this.adpMetaSyncItemProcessor())
                .writer(this.adpMetaSyncItemRepositoryItemWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(1000)
                .noRetry(Exception.class)
                .build();
    }

    @Bean
    public Job adpMetaSyncItemJob() {
        return new JobBuilder("sync-items-job", this.jobRepository)
                .start(this.adpMetaSyncItemStep())
                .build();
    }
}

package studio.snowfox.albionsquare.component;

import com.albion_online_data.ao_bin_dumps.Items;
import com.albion_online_data.ao_bin_dumps.Shopcategory;
import com.albion_online_data.ao_bin_dumps.Shopsubcategory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;
import studio.snowfox.albionsquare.entity.AlbionOnlineShopCategory;
import studio.snowfox.albionsquare.entity.AlbionOnlineShopCategoryId;
import studio.snowfox.albionsquare.repository.AdpMetaSyncItemObjectRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineItemRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineShopCategoryRepository;

@Log
@Component
@RequiredArgsConstructor
public class AdpMetaSyncItemComponent {
    private final AlbionOnlineShopCategoryRepository albionOnlineShopCategoryRepository;
    private final AdpMetaSyncItemObjectRepository adpMetaSyncItemObjectRepository;
    private final AlbionOnlineItemRepository albionOnlineItemRepository;
    private final JobLauncher jobLauncher;
    private final Job adpMetaSyncItemJob;

    public void load(Items items, String sha)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
                    JobParametersInvalidException, JobRestartException {
        this.loadItemCategories(items, sha);
        this.loadItems(items, sha);
    }

    public void loadItemCategories(Items items, String sha) {
        List<AlbionOnlineShopCategory> albionOnlineShopCategoryList = new ArrayList<>();

        for (Shopcategory shopcategory : items.getShopcategories().getShopcategory()) {
            for (Shopsubcategory shopsubcategory : shopcategory.getShopsubcategory()) {
                AlbionOnlineShopCategoryId albionOnlineShopCategoryId = new AlbionOnlineShopCategoryId();

                albionOnlineShopCategoryId.setShopCategory(shopcategory.getId());
                albionOnlineShopCategoryId.setShopSubCategory(shopsubcategory.getId());

                AlbionOnlineShopCategory albionOnlineShopCategory = new AlbionOnlineShopCategory();

                albionOnlineShopCategory.setId(albionOnlineShopCategoryId);
                albionOnlineShopCategory.setSha(sha);

                albionOnlineShopCategoryList.add(albionOnlineShopCategory);
            }
        }

        this.albionOnlineShopCategoryRepository.saveAll(albionOnlineShopCategoryList);
        this.albionOnlineShopCategoryRepository.deleteAllByShaNot(sha);
    }

    public void loadItems(Items items, String sha)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
                    JobParametersInvalidException, JobRestartException {
        this.adpMetaSyncItemObjectRepository.deleteAll();

        List<Object> objectList = new ArrayList<>();

        objectList.add(items.getHideoutitem());
        objectList.add(items.getKilltrophy());
        objectList.addAll(items.getTrackingitem());
        objectList.addAll(items.getFarmableitem());
        objectList.addAll(items.getSiegebanner());
        objectList.addAll(items.getWeaponOrConsumablefrominventoryitemOrConsumableitem());

        this.adpMetaSyncItemObjectRepository.saveAll(objectList);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("sha", sha)
                .addLocalDateTime("local.date.time", LocalDateTime.now())
                .toJobParameters();

        JobExecution jobExecution = this.jobLauncher.run(this.adpMetaSyncItemJob, jobParameters);

        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            throw new RuntimeException("Job execution failed");
        }

        this.albionOnlineItemRepository.deleteAllByShaNot(sha);
    }
}

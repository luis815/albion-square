package studio.snowfox.albionsquare.service;

import io.github.mojtabaJ.cwebp.WebpConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import studio.snowfox.albionsquare.entity.AlbionOnlineItem;
import studio.snowfox.albionsquare.repository.AlbionOnlineItemRepository;

@Log
@Service
@RequiredArgsConstructor
public class AowAssetSyncService {
    @Value("${aws.s3.bucket}")
    private String bucket;

    private final AlbionOnlineItemRepository albionOnlineItemRepository;
    private final S3Client s3Client;

    public void processAll() {
        this.handleProcessAll();
    }

    @Async
    public void processAllAsync() {
        this.handleProcessAll();
    }

    private void handleProcessAll() {
        this.processItemAssets();
    }

    private void processItemAssets() {
        List<AlbionOnlineItem> albionOnlineItems = this.albionOnlineItemRepository.findAllByAssetIsNullOrAssetIsFalse();

        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix("albion-online/item")
                .build();

        ListObjectsV2Response listObjectsV2Response = this.s3Client.listObjectsV2(listObjectsV2Request);

        List<S3Object> s3ObjectSummaryList = listObjectsV2Response.contents();

        Map<String, S3Object> s3ObjectSummaryMap =
                s3ObjectSummaryList.stream().collect(Collectors.toMap(S3Object::key, s3Object -> s3Object));

        for (AlbionOnlineItem albionOnlineItem : albionOnlineItems) {
            List<Short> enchantmentLevels = new ArrayList<>();

            if (albionOnlineItem.getEnchantmentLevel0()) {
                enchantmentLevels.add((short) 0);
            }

            if (albionOnlineItem.getEnchantmentLevel1()) {
                enchantmentLevels.add((short) 1);
            }

            if (albionOnlineItem.getEnchantmentLevel2()) {
                enchantmentLevels.add((short) 2);
            }

            if (albionOnlineItem.getEnchantmentLevel3()) {
                enchantmentLevels.add((short) 3);
            }

            if (albionOnlineItem.getEnchantmentLevel4()) {
                enchantmentLevels.add((short) 4);
            }

            for (Short enchantmentLevel : enchantmentLevels) {
                for (short quality = 1; quality <= albionOnlineItem.getMaxQuality(); ++quality) {
                    if (s3ObjectSummaryMap.containsKey(String.format(
                            "albion-online/item/%s/%s/%s/asset.webp",
                            albionOnlineItem.getUniqueName(), enchantmentLevel, quality))) {
                        albionOnlineItem.setAsset(true);
                        this.albionOnlineItemRepository.save(albionOnlineItem);
                        continue;
                    }

                    Optional<Byte[]> rawItemAsset =
                            this.fetchItemAsset(albionOnlineItem.getUniqueName(), enchantmentLevel, quality);

                    if (rawItemAsset.isEmpty()) {
                        log.warning(String.format("No item asset found for: %s", albionOnlineItem.getUniqueName()));
                        albionOnlineItem.setAsset(false);
                        this.albionOnlineItemRepository.save(albionOnlineItem);
                        continue;
                    }

                    byte[] itemAssetWebpByteArray =
                            WebpConverter.imageByteToWebpByte(ArrayUtils.toPrimitive(rawItemAsset.get()));

                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(String.format(
                                    "albion-online/item/%s/%s/%s/asset.webp",
                                    albionOnlineItem.getUniqueName(), enchantmentLevel, quality))
                            .metadata(Map.of(
                                    "Content-Type", "image/webp",
                                    "Cache-Control", "public max-age=15552000",
                                    "Content-Length", String.valueOf(itemAssetWebpByteArray.length)))
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build();

                    RequestBody requestBody = RequestBody.fromBytes(itemAssetWebpByteArray);

                    this.s3Client.putObject(putObjectRequest, requestBody);

                    albionOnlineItem.setAsset(true);
                    this.albionOnlineItemRepository.save(albionOnlineItem);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private Optional<Byte[]> fetchItemAsset(String uniqueName, Short enchantment, Short quality) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("https://render.albiononline.com/v1/item/");
        stringBuilder.append(uniqueName);

        if (enchantment > 0) {
            stringBuilder.append("@").append(enchantment);
        }

        stringBuilder.append(".png").append("?");

        if (quality > 1) {
            stringBuilder.append("quality=").append(quality).append("&");
        }

        stringBuilder.append("locale=en");

        BufferedImage bufferedImage;

        try {
            bufferedImage = ImageIO.read(new URI(stringBuilder.toString()).toURL());
        } catch (IOException | URISyntaxException e) {
            return Optional.empty();
        }

        if (Objects.isNull(bufferedImage)) {
            return Optional.empty();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Byte[] bytes = ArrayUtils.toObject(byteArrayOutputStream.toByteArray());

        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(bytes);
    }
}

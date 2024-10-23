package studio.snowfox.albionsquare.service;

import com.albion_online_data.ao_bin_dumps.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import studio.snowfox.albionsquare.entity.*;
import studio.snowfox.albionsquare.json.GitHubCommitMetaJson;
import studio.snowfox.albionsquare.repository.AdpMetaLogRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineItemRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineLocalizationRepository;
import studio.snowfox.albionsquare.repository.AlbionOnlineShopCategoryRepository;

@Log
@Service
@RequiredArgsConstructor
public class AdpMetaSyncService {
    private final AdpMetaLogRepository adpMetaLogRepository;
    private final AlbionOnlineShopCategoryRepository albionOnlineShopCategoryRepository;
    private final AlbionOnlineItemRepository albionOnlineItemRepository;
    private final AlbionOnlineLocalizationRepository albionOnlineLocalizationRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void processAll() {
        this.handleProcessAll();
    }

    @Async
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void processAllAsync() {
        this.handleProcessAll();
    }

    private void handleProcessAll() {
        GitHubCommitMetaJson latestGitHubCommitMetaJson = null;

        try {
            latestGitHubCommitMetaJson = this.fetchLatestGitHubCommitMeta();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        String sha = latestGitHubCommitMetaJson.getSha();

        AdpMetaLog adpMetaLog = this.adpMetaLogRepository
                .findBySha(sha)
                .orElse(AdpMetaLog.builder()
                        .sha(sha)
                        .status(GenericStatus.PENDING)
                        .build());

        switch (adpMetaLog.getStatus()) {
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
                log.info("New ADP dumps available - will proceed with processing");
                this.adpMetaLogRepository.save(adpMetaLog);
            }
            default -> {
                throw new RuntimeException("Status not supported");
            }
        }

        adpMetaLog.setStatus(GenericStatus.IN_PROGRESS);
        this.adpMetaLogRepository.save(adpMetaLog);

        try {
            log.info("Processing items");

            Items items = this.fetchItemsByCommitHash(sha);

            StringWriter itemStringWriter = new StringWriter();
            JAXBContext.newInstance(Items.class).createMarshaller().marshal(items, itemStringWriter);
            adpMetaLog.setRawAdpItems(itemStringWriter.toString());

            this.processItems(items, sha);

            log.info("Processing tmx");

            Tmx tmx = this.fetchTmxByCommitHash(sha);

            StringWriter tmxStringWriter = new StringWriter();
            JAXBContext.newInstance(Tmx.class).createMarshaller().marshal(tmx, tmxStringWriter);
            adpMetaLog.setRawAdpTmx(tmxStringWriter.toString());

            this.processTmx(tmx, sha);
        } catch (MalformedURLException | URISyntaxException | JAXBException e) {
            adpMetaLog.setStatus(GenericStatus.FAILED);
            this.adpMetaLogRepository.save(adpMetaLog);
            throw new RuntimeException(e);
        }

        adpMetaLog.setStatus(GenericStatus.SUCCESS);
        this.adpMetaLogRepository.save(adpMetaLog);

        log.info("Done");
    }

    private GitHubCommitMetaJson fetchLatestGitHubCommitMeta() throws URISyntaxException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL adpLatestCommitUrl = new URI("https://api.github.com/repos/ao-data/ao-bin-dumps/commits/master").toURL();
        return objectMapper.readValue(adpLatestCommitUrl, GitHubCommitMetaJson.class);
    }

    private Items fetchItemsByCommitHash(String hash) throws JAXBException, URISyntaxException, MalformedURLException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Items.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Items) unmarshaller.unmarshal(
                new URI(String.format("https://raw.githubusercontent.com/ao-data/ao-bin-dumps/%s/items.xml", hash))
                        .toURL());
    }

    private void processItems(Items items, String sha) {
        for (Shopcategory shopcategory : items.getShopcategories().getShopcategory()) {
            for (Shopsubcategory shopsubcategory : shopcategory.getShopsubcategory()) {
                AlbionOnlineShopCategoryId albionOnlineShopCategoryId = new AlbionOnlineShopCategoryId();

                albionOnlineShopCategoryId.setShopCategory(shopcategory.getId());
                albionOnlineShopCategoryId.setShopSubCategory(shopsubcategory.getId());

                AlbionOnlineShopCategory albionOnlineShopCategory = new AlbionOnlineShopCategory();

                albionOnlineShopCategory.setId(albionOnlineShopCategoryId);
                albionOnlineShopCategory.setSha(sha);

                albionOnlineShopCategoryRepository.save(albionOnlineShopCategory);
            }
        }

        try {
            this.processItem(items.getHideoutitem(), sha);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.severe("Error while processing " + items.getHideoutitem().getUniquename());
        }

        try {
            this.processItem(items.getKilltrophy(), sha);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.severe("Error while processing " + items.getKilltrophy().getUniquename());
        }

        items.getTrackingitem().forEach(trackingitem -> {
            try {
                this.processItem(trackingitem, sha);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.severe("Error while processing " + trackingitem.getUniquename());
            }
        });

        items.getTrackingitem().forEach(trackingitem -> {
            try {
                this.processItem(trackingitem, sha);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.severe("Error while processing " + trackingitem.getUniquename());
            }
        });

        items.getFarmableitem().forEach(farmableitem -> {
            try {
                this.processItem(farmableitem, sha);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.severe("Error while processing " + farmableitem.getUniquename());
            }
        });

        items.getSiegebanner().forEach(siegebanner -> {
            try {
                this.processItem(siegebanner, sha);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.severe("Error while processing " + siegebanner.getUniquename());
            }
        });

        items.getConsumablefrominventoryitemOrConsumableitemOrCrystalleagueitem()
                .forEach(item -> {
                    try {
                        this.processItem(item, sha);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        try {
                            log.severe("Error while processing "
                                    + item.getClass().getMethod("getUniquename").invoke(item));
                        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                            log.severe("Error while processing and couldn't identify its unique name");
                        }
                    }
                });

        this.albionOnlineItemRepository.deleteAllByShaNot(sha);
    }

    private void processItem(Object object, String sha)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String uniqueName =
                (String) object.getClass().getMethod("getUniquename").invoke(object);
        BigInteger tier = (BigInteger) object.getClass().getMethod("getTier").invoke(object);
        String shopCategory =
                (String) object.getClass().getMethod("getShopcategory").invoke(object);
        String shopSubCategory =
                (String) object.getClass().getMethod("getShopsubcategory1").invoke(object);

        List<Short> enchantmentList = this.extractEnchantmentList(object);
        Short maxQuality = this.extractMaxQuality(object);

        AlbionOnlineItem albionOnlineItem = new AlbionOnlineItem();

        albionOnlineItem.setUniqueName(uniqueName);
        albionOnlineItem.setTier(tier.shortValue());

        for (Short enchantment : enchantmentList) {
            switch (enchantment) {
                case 0 -> albionOnlineItem.setEnchantmentLevel0(true);
                case 1 -> albionOnlineItem.setEnchantmentLevel1(true);
                case 2 -> albionOnlineItem.setEnchantmentLevel2(true);
                case 3 -> albionOnlineItem.setEnchantmentLevel3(true);
                case 4 -> albionOnlineItem.setEnchantmentLevel4(true);
                default -> log.severe("Unknown enchantment " + enchantment);
            }
        }

        albionOnlineItem.setMaxQuality(maxQuality);
        albionOnlineItem.setShopCategory(shopCategory);
        albionOnlineItem.setShopSubCategory(shopSubCategory);
        albionOnlineItem.setSha(sha);

        this.albionOnlineItemRepository.save(albionOnlineItem);
    }

    private List<Short> extractEnchantmentList(Object object) {
        List<Short> enchantmentList = new ArrayList<>();

        try {
            BigInteger enchantment = (BigInteger)
                    object.getClass().getMethod("getEnchantmentlevel").invoke(object);

            if (Objects.isNull(enchantment)) {
                throw new NoSuchMethodException();
            }

            enchantmentList.add(enchantment.shortValue());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            try {
                Enchantments rawEnchantments = (Enchantments)
                        object.getClass().getMethod("getEnchantments").invoke(object);

                if (Objects.isNull(rawEnchantments)) {
                    throw new NoSuchMethodException();
                }

                enchantmentList.add(Short.valueOf("0"));

                enchantmentList.addAll(rawEnchantments.getEnchantment().stream()
                        .map(enchantment -> enchantment.getEnchantmentlevel().shortValue())
                        .toList());
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                enchantmentList.add(Short.valueOf("0"));
            }
        }

        return enchantmentList;
    }

    private Short extractMaxQuality(Object object) {
        try {
            BigInteger maxQuality = (BigInteger)
                    object.getClass().getMethod("getMaxqualitylevel").invoke(object);

            if (Objects.isNull(maxQuality)) {
                throw new NoSuchMethodException();
            }

            return maxQuality.shortValue();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return Short.valueOf("1");
        }
    }

    private Tmx fetchTmxByCommitHash(String hash) throws JAXBException, URISyntaxException, MalformedURLException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Tmx.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Tmx) unmarshaller.unmarshal(new URI(String.format(
                        "https://raw.githubusercontent.com/ao-data/ao-bin-dumps/%s/localization.xml", hash))
                .toURL());
    }

    private void processTmx(Tmx tmx, String sha) {
        tmx.getBody().getTu().forEach(tu -> {
            AlbionOnlineLocalization albionOnlineLocalization = new AlbionOnlineLocalization();

            albionOnlineLocalization.setTuid(tu.getTuid());

            List<String> unsupportedLangs = new ArrayList<>();

            tu.getTuv().forEach(tuv -> {
                switch (tuv.getLang().toLowerCase().replace("-", "_")) {
                    case "en_us" -> albionOnlineLocalization.setEnUs(tuv.getSeg());
                    case "de_de" -> albionOnlineLocalization.setDeDe(tuv.getSeg());
                    case "fr_fr" -> albionOnlineLocalization.setFrFr(tuv.getSeg());
                    case "ru_ru" -> albionOnlineLocalization.setRuRu(tuv.getSeg());
                    case "pl_pl" -> albionOnlineLocalization.setPlPl(tuv.getSeg());
                    case "es_es" -> albionOnlineLocalization.setEsEs(tuv.getSeg());
                    case "pt_br" -> albionOnlineLocalization.setPtBr(tuv.getSeg());
                    case "it_it" -> albionOnlineLocalization.setItIt(tuv.getSeg());
                    case "zh_cn" -> albionOnlineLocalization.setZhCn(tuv.getSeg());
                    case "ko_kr" -> albionOnlineLocalization.setKoKr(tuv.getSeg());
                    case "ja_jp" -> albionOnlineLocalization.setJaJp(tuv.getSeg());
                    case "zh_tw" -> albionOnlineLocalization.setZhTw(tuv.getSeg());
                    case "id_id" -> albionOnlineLocalization.setIdId(tuv.getSeg());
                    case "tr_tr" -> albionOnlineLocalization.setTrTr(tuv.getSeg());
                    case "ar_sa" -> albionOnlineLocalization.setArSa(tuv.getSeg());
                    default -> unsupportedLangs.add(tuv.getLang());
                }
            });

            if (!unsupportedLangs.isEmpty()) {
                unsupportedLangs.forEach(
                        unsupportedLang -> log.severe(String.format("Unsupported language: %s", unsupportedLang)));
            }

            albionOnlineLocalization.setSha(sha);

            this.albionOnlineLocalizationRepository.save(albionOnlineLocalization);
        });

        this.albionOnlineLocalizationRepository.deleteAllByShaNot(sha);
    }
}

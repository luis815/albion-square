package studio.snowfox.albionsquare.batch.processor;

import com.albion_online_data.ao_bin_dumps.Enchantments;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.java.Log;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import studio.snowfox.albionsquare.entity.AlbionOnlineItem;

@Log
public class AdpMetaSyncItemProcessor implements ItemProcessor<Object, AlbionOnlineItem> {
    @Value("#{jobParameters['sha']}")
    private String sha;

    @Override
    public AlbionOnlineItem process(Object object) throws Exception {
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

        return albionOnlineItem;
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
}

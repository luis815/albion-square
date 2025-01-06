package studio.snowfox.albionsquare.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import studio.snowfox.albionsquare.entity.AlbionOnlineSpell;

public class AdpMetaSyncSpellProcessor implements ItemProcessor<Object, AlbionOnlineSpell> {
    @Value("#{jobParameters['sha']}")
    private String sha;

    @Override
    public AlbionOnlineSpell process(Object item) throws Exception {
        AlbionOnlineSpell spell = new AlbionOnlineSpell();

        spell.setUniqueName((String) item.getClass().getMethod("getUniquename").invoke(item));
        spell.setSha(sha);

        return spell;
    }
}

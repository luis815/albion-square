package studio.snowfox.albionsquare.batch.processor;

import com.albion_online_data.ao_bin_dumps.Tu;
import lombok.extern.java.Log;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import studio.snowfox.albionsquare.entity.AlbionOnlineLocalization;

@Log
public class AdpMetaSyncTmxProcessor implements ItemProcessor<Tu, AlbionOnlineLocalization> {
    @Value("#{jobParameters['sha']}")
    private String sha;

    @Override
    public AlbionOnlineLocalization process(Tu tu) throws Exception {
        AlbionOnlineLocalization albionOnlineLocalization = new AlbionOnlineLocalization();

        albionOnlineLocalization.setTuid(tu.getTuid());

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
            }
        });

        albionOnlineLocalization.setSha(sha);

        return albionOnlineLocalization;
    }
}

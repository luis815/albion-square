package studio.snowfox.albionsquare.component;

import com.albion_online_data.ao_bin_dumps.Items;
import com.albion_online_data.ao_bin_dumps.Spells;
import com.albion_online_data.ao_bin_dumps.Tmx;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import studio.snowfox.albionsquare.json.GitHubCommitMetaJson;

@Log
@Component
public class AdpMetaSyncGitHubComponent {
    private static final String ADP_BIN_DUMPS_LATEST_COMMIT_URI =
            "https://api.github.com/repos/ao-data/ao-bin-dumps/commits/master";

    private static final String ADP_BIN_DUMPS_COMMIT_ITEMS_URI_TEMPLATE =
            "https://raw.githubusercontent.com/ao-data/ao-bin-dumps/%s/items.xml";

    private static final String ADP_BIN_DUMPS_COMMIT_TMX_URI_TEMPLATE =
            "https://raw.githubusercontent.com/ao-data/ao-bin-dumps/%s/localization.xml";

    private static final String ADP_BIN_DUMPS_COMMIT_SPELLS_URI_TEMPLATE =
            "https://raw.githubusercontent.com/ao-data/ao-bin-dumps/%s/spells.xml";

    public GitHubCommitMetaJson fetchLatestGitHubCommitMeta() throws URISyntaxException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL adpLatestCommitUrl = new URI(ADP_BIN_DUMPS_LATEST_COMMIT_URI).toURL();
        return objectMapper.readValue(adpLatestCommitUrl, GitHubCommitMetaJson.class);
    }

    public Items fetchItemsByCommitHash(String hash) throws JAXBException, URISyntaxException, MalformedURLException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Items.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Items)
                unmarshaller.unmarshal(new URI(String.format(ADP_BIN_DUMPS_COMMIT_ITEMS_URI_TEMPLATE, hash)).toURL());
    }

    public Tmx fetchTmxByCommitHash(String hash) throws JAXBException, URISyntaxException, MalformedURLException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Tmx.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Tmx)
                unmarshaller.unmarshal(new URI(String.format(ADP_BIN_DUMPS_COMMIT_TMX_URI_TEMPLATE, hash)).toURL());
    }

    public Spells fetchSpellsByCommitHash(String hash) throws JAXBException, URISyntaxException, MalformedURLException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Spells.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Spells)
                unmarshaller.unmarshal(new URI(String.format(ADP_BIN_DUMPS_COMMIT_SPELLS_URI_TEMPLATE, hash)).toURL());
    }
}

package studio.snowfox.albionsquare.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubCommitMetaJson {
    private String sha;
}

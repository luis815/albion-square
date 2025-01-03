package studio.snowfox.albionsquare.repository;

import com.albion_online_data.ao_bin_dumps.Tu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AdpMetaSyncTmxTuRepository {
    private final List<Tu> tuList = Collections.synchronizedList(new ArrayList<>());

    public synchronized void saveAll(List<Tu> tuList) {
        this.tuList.addAll(tuList);
    }

    public synchronized void deleteAll() {
        this.tuList.clear();
    }

    public synchronized List<Tu> findAll() {
        return Collections.unmodifiableList(tuList);
    }
}

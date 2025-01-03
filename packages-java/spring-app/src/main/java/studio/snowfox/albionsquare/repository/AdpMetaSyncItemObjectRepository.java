package studio.snowfox.albionsquare.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AdpMetaSyncItemObjectRepository {
    private final List<Object> objects = Collections.synchronizedList(new ArrayList<>());

    public synchronized void saveAll(List<Object> objects) {
        this.objects.addAll(objects);
    }

    public synchronized void deleteAll() {
        this.objects.clear();
    }

    public synchronized List<Object> findAll() {
        return Collections.unmodifiableList(objects);
    }
}

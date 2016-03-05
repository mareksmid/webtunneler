package cz.mareksmid.webtunneler.server2;

import java.util.HashMap;
import java.util.Map;

public class CommBean {
    
    private Map<String,WSWorker> workers;
    private Map<String, WSWorker> workersById;

    public CommBean() {
        workers = new HashMap<>();
        workersById = new HashMap<>();
    }

    public WSWorker getWorker(String c) {
        return workers.get(c);
    }

    public void putWorker(String c, WSWorker w) {
        workers.put(c, w);
    }

    public void putWorkersById(String id, WSWorker w) {
        workersById.put(id, w);
    }

    public WSWorker getWorkersById(String id) {
        return workersById.get(id);
    }
    
}

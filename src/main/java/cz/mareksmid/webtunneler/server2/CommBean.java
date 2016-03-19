package cz.mareksmid.webtunneler.server2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommBean {
    
    private final Map<String,WSWorker> workers = new ConcurrentHashMap<>();
    private final Map<String, WSWorker> workersById = new ConcurrentHashMap<>();

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

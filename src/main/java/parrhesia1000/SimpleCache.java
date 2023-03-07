package parrhesia1000;

import lombok.experimental.Delegate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleCache<K,V> {

    private final Map<K,V> map;

    public SimpleCache(int maxSize) {

        map = Collections.synchronizedMap(new LinkedHashMap<K, V>() {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                return size() > maxSize;
            }
        });
    }

    public Map<K, V> getMap() {
        return map;
    }
}

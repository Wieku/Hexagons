package xyz.hexagons.server.util;

import javax.xml.ws.Holder;
import java.util.*;

public class TimeoutMap<K, V> {
    private final Map<K, V> backing = new HashMap<>();
    private final Map<K, Long> timeouts = new LinkedHashMap<>();
    private Long nextCheck = Long.MAX_VALUE;

    public void put(K key, V value, Long milisValid) {
        synchronized (backing) {
            long timeout = System.currentTimeMillis() + milisValid;
            backing.put(key, value);
            timeouts.put(key, timeout);
            if(timeout > nextCheck) nextCheck = timeout;
        }
        check();
    }

    public V get(K key) {
        V value;
        synchronized (backing) {
            value = backing.get(key);
        }
        check();
        return value;
    }

    public boolean containsKey(K key) {
        boolean value;
        synchronized (backing) {
            value = backing.containsKey(key);
        }
        check();
        return value;
    }

    public void remove(K key) {
        synchronized (backing) {
            backing.remove(key);
        }
        check();
    }

    private void check() {
        if(nextCheck < System.currentTimeMillis()) {
            synchronized (backing) {
                long current = System.currentTimeMillis();
                Holder<Long> next = new Holder<>(Long.MAX_VALUE);
                List<K> toRemove = new LinkedList<>();

                timeouts.forEach((key, timeout) -> {
                    if(timeout < current) {
                        toRemove.add(key);
                    } else if(next.value > timeout) {
                        next.value = timeout;
                    }
                });
                toRemove.forEach(key -> {backing.remove(key); timeouts.remove(key);});
                nextCheck = next.value;
            }
        }
    }
}

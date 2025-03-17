package com.reopenai.reactives.core.cache.local;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Allen Huang
 */
public interface LocalCacheInvoker {

    class F0<V> {

        private final LocalCache.LocalCacheInstance<Boolean, V> cache;

        public F0(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Supplier<V> func,
                  Consumer<V> evictionListener, Consumer<V> removalListener) {
            this.cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh, k -> func.get(),
                    CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        }

        public V get() {
            return cache.get(Boolean.TRUE);
        }

        public V put(V value) {
            return cache.put(Boolean.TRUE, value);
        }

        public void remove() {
            cache.remove(Boolean.TRUE);
        }

    }

    class F1<K, V> {

        private final LocalCache.LocalCacheInstance<K, V> cache;

        public F1(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Function<K, V> func,
                  BiConsumer<K, V> evictionListener, BiConsumer<K, V> removalListener) {
            this.cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, evictionListener, removalListener);
        }

        public V get(K key) {
            return cache.get(key);
        }

        public V put(K key, V value) {
            return cache.put(key, value);
        }

        public void remove(K key) {
            cache.remove(key);
        }

    }

}

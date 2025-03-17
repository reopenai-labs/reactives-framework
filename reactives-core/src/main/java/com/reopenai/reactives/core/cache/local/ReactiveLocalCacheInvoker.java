package com.reopenai.reactives.core.cache.local;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Allen Huang
 */
public interface ReactiveLocalCacheInvoker {


    //--------------------
    //   缓存执行器
    //--------------------

    class F0<V> {

        private final ReactiveLocalCache.ReactiveLocalCacheInstance<Boolean, V> cache;

        public F0(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Supplier<Mono<V>> func,
                  Consumer<V> evictionListener, Consumer<V> removalListener) {
            BiConsumer<Boolean, V> eviction = null, removal = null;
            if (evictionListener != null) {
                eviction = (k, v) -> evictionListener.accept(v);
            }
            if (removalListener != null) {
                removal = (k, v) -> removalListener.accept(v);
            }
            this.cache = ReactiveLocalCache.ReactiveLocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh, k -> func.get(), eviction, removal);
        }

        public Mono<V> get() {
            return cache.get(Boolean.TRUE);
        }

        public V put(V value) {
            return cache.put(Boolean.TRUE, value);
        }

    }

    class F1<K, V> {

        private final ReactiveLocalCache.ReactiveLocalCacheInstance<K, V> cache;

        public F1(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Function<K, Mono<V>> func,
                  BiConsumer<K, V> evictionListener, BiConsumer<K, V> removalListener) {
            this.cache = ReactiveLocalCache.ReactiveLocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, evictionListener, removalListener);
        }

        public Mono<V> get(K key) {
            return cache.get(key);
        }

        public V put(K key, V value) {
            return cache.put(key, value);
        }

    }
}

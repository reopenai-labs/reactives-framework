package com.reopenai.reactives.core.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.reopenai.reactives.core.builtin.Tuple;
import com.reopenai.reactives.core.lambda.XConsumer;
import com.reopenai.reactives.core.lambda.XFunction;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Allen Huang
 */
@Slf4j
public class LocalCache {

    //--------------------
    //   无参数
    //--------------------

    public static <V> XFunction.R0<V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Supplier<V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <V> XFunction.R0<V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Supplier<V> func,
                                             Consumer<V> evictionListener, Consumer<V> removalListener) {
        LocalCache.LocalCacheInstance<Boolean, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh,
                k -> func.get(), CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        return () -> cache.get(Boolean.TRUE);
    }

    //--------------------
    //   单参数
    //--------------------

    public static <K, V> XFunction.R1<K, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Function<K, V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K, V> XFunction.R1<K, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Function<K, V> func,
                                                   BiConsumer<K, V> evictionListener, BiConsumer<K, V> removalListener) {
        LocalCache.LocalCacheInstance<K, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, evictionListener, removalListener);
        return cache::get;
    }

    //--------------------
    //   两个参数
    //--------------------

    public static <K1, K2, V> XFunction.R2<K1, K2, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R2<K1, K2, V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K1, K2, V> XFunction.R2<K1, K2, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R2<K1, K2, V> func,
                                                             XConsumer.R3<K1, K2, V> evictionListener, XConsumer.R3<K1, K2, V> removalListener) {
        LocalCache.LocalCacheInstance<Tuple.V2<K1, K2>, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh,
                tuple -> func.call(tuple.v1(), tuple.v2()), CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        return (v1, v2) -> cache.get(new Tuple.V2<>(v1, v2));
    }

    //--------------------
    //   三个参数
    //--------------------

    public static <K1, K2, K3, V> XFunction.R3<K1, K2, K3, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R3<K1, K2, K3, V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K1, K2, K3, V> XFunction.R3<K1, K2, K3, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R3<K1, K2, K3, V> func,
                                                                     XConsumer.R4<K1, K2, K3, V> evictionListener, XConsumer.R4<K1, K2, K3, V> removalListener) {
        LocalCache.LocalCacheInstance<Tuple.V3<K1, K2, K3>, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh,
                tuple -> func.call(tuple.v1(), tuple.v2(), tuple.v3()), CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        return (v1, v2, v3) -> cache.get(new Tuple.V3<>(v1, v2, v3));
    }

    //--------------------
    //   四个参数
    //--------------------

    public static <K1, K2, K3, K4, V> XFunction.R4<K1, K2, K3, K4, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R4<K1, K2, K3, K4, V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K1, K2, K3, K4, V> XFunction.R4<K1, K2, K3, K4, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R4<K1, K2, K3, K4, V> func,
                                                                             XConsumer.R5<K1, K2, K3, K4, V> evictionListener, XConsumer.R5<K1, K2, K3, K4, V> removalListener) {
        LocalCache.LocalCacheInstance<Tuple.V4<K1, K2, K3, K4>, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh,
                tuple -> func.call(tuple.v1(), tuple.v2(), tuple.v3(), tuple.v4()), CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        return (v1, v2, v3, v4) -> cache.get(new Tuple.V4<>(v1, v2, v3, v4));
    }

    //--------------------
    //   五个参数
    //--------------------

    public static <K1, K2, K3, K4, K5, V> XFunction.R5<K1, K2, K3, K4, K5, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R5<K1, K2, K3, K4, K5, V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K1, K2, K3, K4, K5, V> XFunction.R5<K1, K2, K3, K4, K5, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R5<K1, K2, K3, K4, K5, V> func,
                                                                                     XConsumer.R6<K1, K2, K3, K4, K5, V> evictionListener, XConsumer.R6<K1, K2, K3, K4, K5, V> removalListener) {
        LocalCache.LocalCacheInstance<Tuple.V5<K1, K2, K3, K4, K5>, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh,
                tuple -> func.call(tuple.v1(), tuple.v2(), tuple.v3(), tuple.v4(), tuple.v5()), CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        return (v1, v2, v3, v4, v5) -> cache.get(new Tuple.V5<>(v1, v2, v3, v4, v5));
    }


    //--------------------
    //   六个参数
    //--------------------

    public static <K1, K2, K3, K4, K5, K6, V> XFunction.R6<K1, K2, K3, K4, K5, K6, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R6<K1, K2, K3, K4, K5, K6, V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K1, K2, K3, K4, K5, K6, V> XFunction.R6<K1, K2, K3, K4, K5, K6, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R6<K1, K2, K3, K4, K5, K6, V> func,
                                                                                             XConsumer.R7<K1, K2, K3, K4, K5, K6, V> evictionListener, XConsumer.R7<K1, K2, K3, K4, K5, K6, V> removalListener) {
        LocalCache.LocalCacheInstance<Tuple.V6<K1, K2, K3, K4, K5, K6>, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh,
                tuple -> func.call(tuple.v1(), tuple.v2(), tuple.v3(), tuple.v4(), tuple.v5(), tuple.v6()), CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        return (v1, v2, v3, v4, v5, v6) -> cache.get(new Tuple.V6<>(v1, v2, v3, v4, v5, v6));
    }

    //--------------------
    //   七个参数
    //--------------------

    public static <K1, K2, K3, K4, K5, K6, K7, V> XFunction.R7<K1, K2, K3, K4, K5, K6, K7, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R7<K1, K2, K3, K4, K5, K6, K7, V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K1, K2, K3, K4, K5, K6, K7, V> XFunction.R7<K1, K2, K3, K4, K5, K6, K7, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R7<K1, K2, K3, K4, K5, K6, K7, V> func,
                                                                                                     XConsumer.R8<K1, K2, K3, K4, K5, K6, K7, V> evictionListener, XConsumer.R8<K1, K2, K3, K4, K5, K6, K7, V> removalListener) {
        LocalCache.LocalCacheInstance<Tuple.V7<K1, K2, K3, K4, K5, K6, K7>, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh,
                tuple -> func.call(tuple.v1(), tuple.v2(), tuple.v3(), tuple.v4(), tuple.v5(), tuple.v6(), tuple.v7()), CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        return (v1, v2, v3, v4, v5, v6, v7) -> cache.get(new Tuple.V7<>(v1, v2, v3, v4, v5, v6, v7));
    }

    //--------------------
    //   八个参数
    //--------------------

    public static <K1, K2, K3, K4, K5, K6, K7, K8, V> XFunction.R8<K1, K2, K3, K4, K5, K6, K7, K8, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R8<K1, K2, K3, K4, K5, K6, K7, K8, V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K1, K2, K3, K4, K5, K6, K7, K8, V> XFunction.R8<K1, K2, K3, K4, K5, K6, K7, K8, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R8<K1, K2, K3, K4, K5, K6, K7, K8, V> func,
                                                                                                             XConsumer.R9<K1, K2, K3, K4, K5, K6, K7, K8, V> evictionListener, XConsumer.R9<K1, K2, K3, K4, K5, K6, K7, K8, V> removalListener) {
        LocalCache.LocalCacheInstance<Tuple.V8<K1, K2, K3, K4, K5, K6, K7, K8>, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh,
                tuple -> func.call(tuple.v1(), tuple.v2(), tuple.v3(), tuple.v4(), tuple.v5(), tuple.v6(), tuple.v7(), tuple.v8()), CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        return (v1, v2, v3, v4, v5, v6, v7, v8) -> cache.get(new Tuple.V8<>(v1, v2, v3, v4, v5, v6, v7, v8));
    }

    //--------------------
    //   九个参数
    //--------------------

    public static <K1, K2, K3, K4, K5, K6, K7, K8, K9, V> XFunction.R9<K1, K2, K3, K4, K5, K6, K7, K8, K9, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R9<K1, K2, K3, K4, K5, K6, K7, K8, K9, V> func) {
        return create(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K1, K2, K3, K4, K5, K6, K7, K8, K9, V> XFunction.R9<K1, K2, K3, K4, K5, K6, K7, K8, K9, V> create(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R9<K1, K2, K3, K4, K5, K6, K7, K8, K9, V> func,
                                                                                                                     XConsumer.R10<K1, K2, K3, K4, K5, K6, K7, K8, K9, V> evictionListener, XConsumer.R10<K1, K2, K3, K4, K5, K6, K7, K8, K9, V> removalListener) {
        LocalCache.LocalCacheInstance<Tuple.V9<K1, K2, K3, K4, K5, K6, K7, K8, K9>, V> cache = LocalCache.LocalCacheInstance.getOrCreate(cacheName, initialCapacity, maximumSize, expire, autoRefresh,
                tuple -> func.call(tuple.v1(), tuple.v2(), tuple.v3(), tuple.v4(), tuple.v5(), tuple.v6(), tuple.v7(), tuple.v8(), tuple.v9()),
                CacheAdapter.listenerAdapter(evictionListener), CacheAdapter.listenerAdapter(removalListener));
        return (v1, v2, v3, v4, v5, v6, v7, v8, v9) -> cache.get(new Tuple.V9<>(v1, v2, v3, v4, v5, v6, v7, v8, v9));
    }

    //--------------------
    //   单参数
    //--------------------
    public static <K, V> LocalCacheInvoker.F1<K, V> createInvoker(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Function<K, V> func) {
        return createInvoker(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K, V> LocalCacheInvoker.F1<K, V> createInvoker(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Function<K, V> func,
                                                                  BiConsumer<K, V> evictionListener, BiConsumer<K, V> removalListener) {
        return new LocalCacheInvoker.F1<>(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, evictionListener, removalListener);
    }

    //--------------------
    //   多个参数
    //--------------------

    public static <K extends Tuple, V> LocalCacheInvoker.F1<K, V> createInvokerWithTuple(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R1<K, V> func) {
        return createInvokerWithTuple(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func, null, null);
    }

    public static <K extends Tuple, V> LocalCacheInvoker.F1<K, V> createInvokerWithTuple(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, XFunction.R1<K, V> func,
                                                                                         BiConsumer<K, V> evictionListener, BiConsumer<K, V> removalListener) {
        return new LocalCacheInvoker.F1<>(cacheName, initialCapacity, maximumSize, expire, autoRefresh, func::call, evictionListener, removalListener);
    }

    //--------------------
    //   缓存实现类
    //--------------------

    static class LocalCacheInstance<K, V> {

        @SuppressWarnings("rawtypes")
        private static final Map<String, LocalCacheInstance> instances = new HashMap<>();

        private static final int concurrency = Runtime.getRuntime().availableProcessors();

        private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(concurrency * 2);

        private final Cache<K, V> cache;

        private final Function<? super K, V> mapper;

        private LocalCacheInstance(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh, Function<? super K, V> mapper,
                                   BiConsumer<K, V> evictionListener, BiConsumer<K, V> removalListener) {
            Caffeine<Object, Object> builder = Caffeine.newBuilder();
            builder.expireAfterWrite(expire)
                    .recordStats()
                    .maximumSize(maximumSize)
                    .initialCapacity(initialCapacity)
                    .scheduler(Scheduler.systemScheduler());
            if (evictionListener != null) {
                builder.<K, V>evictionListener((k, v, status) -> evictionListener.accept(k, v));
            }
            if (removalListener != null) {
                builder.<K, V>removalListener((k, v, status) -> removalListener.accept(k, v));
            }
            this.mapper = mapper;
            this.cache = builder.build();
            new CaffeineCacheMetrics<>(this.cache, cacheName, Tags.of("type", "LocalCache"))
                    .bindTo(Metrics.globalRegistry);

            if (autoRefresh != null) {
                SCHEDULER.scheduleWithFixedDelay(() -> {
                    Flux.fromIterable(this.cache.asMap().keySet())
                            .map(this::refreshLoad)
                            .doOnNext(tuple -> cache.put(tuple.v1(), tuple.v2()))
                            .subscribe();
                }, Duration.ofMinutes(1).toMillis(), autoRefresh.toMillis(), TimeUnit.MILLISECONDS);
            }
        }

        private Tuple.V2<K, V> refreshLoad(K key) {
            return new Tuple.V2<>(key, mapper.apply(key));
        }

        public V get(K key) {
            return cache.get(key, k -> mapper.apply(key));
        }

        public V put(K key, V value) {
            if (value != null) {
                cache.put(key, value);
            }
            return value;
        }

        public void remove(K key) {
            cache.invalidate(key);
        }

        @SuppressWarnings("unchecked")
        public static <K, V> LocalCacheInstance<K, V> getOrCreate(String cacheName, int initialCapacity, long maximumSize, Duration expire, Duration autoRefresh,
                                                                  Function<? super K, V> mapper, BiConsumer<K, V> evictionListener, BiConsumer<K, V> removalListener) {
            return instances.computeIfAbsent(cacheName, n -> new LocalCacheInstance<>(cacheName, initialCapacity, maximumSize, expire, autoRefresh, mapper, evictionListener, removalListener));
        }

    }

}

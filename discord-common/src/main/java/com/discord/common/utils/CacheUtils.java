package com.discord.common.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CacheUtils<K, V> {

    private final Cache<K, CacheObject<Object>> cache;

    private final Integer DEFAULT_DURATION = 120;

    public CacheUtils() {
        cache = CacheBuilder.newBuilder().expireAfterWrite(DEFAULT_DURATION, TimeUnit.MINUTES).build();
    }

    public CacheUtils(long duration, TimeUnit timeUnit) {
        cache = CacheBuilder.newBuilder().expireAfterWrite(duration, timeUnit).build();
    }

    public void put(K key, Object value, long duration, TimeUnit timeUnit) {
        CacheObject<Object> cacheObject = new CacheObject<>(value, duration, timeUnit);
        cache.put(key, cacheObject);
    }

    public void put(K key, Object value) {
        CacheObject<Object> cacheObject = new CacheObject<>(value, DEFAULT_DURATION, TimeUnit.MINUTES);
        cache.put(key, cacheObject);
    }

    public Object get(K key) {
        CacheObject<Object> cacheObject = cache.getIfPresent(key);
        if (cacheObject == null) {
            return null;
        }

        if (System.currentTimeMillis() > cacheObject.getExpireTime()) {
            cache.invalidate(key);
            return null;
        }

        return cacheObject.getValue();
    }

    public void remove(K key) {
        cache.invalidate(key);
    }

    public Object hGet(K key, K hashKey) {
        Object map = get(key);
        if (map instanceof Map) {
            return ((Map) map).get(hashKey);
        }
        return null;
    }

    public void hSet(K key, K hashKey, V value, long duration, TimeUnit timeUnit) {
        Object map = get(key);
        if (map == null) {
            map = new HashMap<K, V>();
            ((Map) map).put(hashKey, value);
        }

        if (map instanceof Map) {
            ((Map) map).put(hashKey, value);
        }
        put(key, map, duration, timeUnit);
    }

    public void hSet(K key, K hashKey, V value) {
        Object map = get(key);
        if (map == null) {
            map = new HashMap<K, V>();
            ((Map) map).put(hashKey, value);
        }

        if (map instanceof Map) {
            ((Map) map).put(hashKey, value);
        }
        put(key, map);
    }

    public void hDel(K key, K hashKey) {
        Object map = get(key);
        if (map instanceof Map) {
            ((Map) map).remove(hashKey);
        }
        put(key, map);
    }

    public Map<K, V> hGetAll(K key) {
        Object o = get(key);

        if (o instanceof Map) {
            return (Map<K, V>) o;
        }

        return null;
    }

    private static class CacheObject<V> {

        private final V value;

        private final long expireTime;

        CacheObject(V value, long duration, TimeUnit timeUnit) {
            this.value = value;
            expireTime = System.currentTimeMillis() + timeUnit.toMillis(duration);
        }

        public V getValue() {
            return value;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }
}

package com.healthcare.bbc.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private static class CacheEntry {
        Object value;
        long expireTime;

        CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        boolean isExpired() {
            return expireTime > 0 && System.currentTimeMillis() > expireTime;
        }
    }

    public void set(String key, Object value) {
        cache.put(key, new CacheEntry(value, 0));
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        long expireTime = System.currentTimeMillis() + unit.toMillis(timeout);
        cache.put(key, new CacheEntry(value, expireTime));
    }

    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    public Boolean delete(String key) {
        return cache.remove(key) != null;
    }

    public Boolean hasKey(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return false;
        }
        return true;
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return false;
        }
        long expireTime = System.currentTimeMillis() + unit.toMillis(timeout);
        cache.put(key, new CacheEntry(entry.value, expireTime));
        return true;
    }

    public Long getExpire(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return -2L;
        }
        if (entry.expireTime == 0) {
            return -1L;
        }
        long remaining = entry.expireTime - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000 : -2L;
    }
}

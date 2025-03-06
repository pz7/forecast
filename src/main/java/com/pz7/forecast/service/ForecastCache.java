package com.pz7.forecast.service;

import java.util.HashMap;
import java.util.Map;

// TODO possibly think of a more sophisticated cache invalidation/management, or replace with 3rd party (e.g. Caffeine)
public class ForecastCache {

    // TODO manage capacity of the cache
    private final Map<String, CacheEntry> cache = new HashMap<>();
    private final long expirationTimeMs;

    public ForecastCache(long expirationTimeMs) {
        this.expirationTimeMs = expirationTimeMs;
    }

    public void put(String address, String value) {
        String key = getKey(address);
        cache.put(key, CacheEntry.of(value));
    }

    public String get(String address) {
        String key = getKey(address);
        CacheEntry cacheEntry = cache.get(key);
        if (cacheEntry != null) {
            if (cacheEntry.isExpired(expirationTimeMs)) {
                cache.remove(key);
                return null;
            } else {
                return cacheEntry.value;
            }
        } else {
            return null;
        }
    }

    private static String getKey(String address) {
        // TODO Normalize the address to avoid duplicates in the cache
        return address.toLowerCase().trim();
    }

    private static class CacheEntry {
        public final String value;
        public final long timestamp;

        private CacheEntry(String value) {
            this.value = value;
            // TODO Use time provider to determine the time instead of System.currentTimeMillis()
            this.timestamp = System.currentTimeMillis();
        }

        public static CacheEntry of(String value) {
            return new CacheEntry(value);
        }

        public boolean isExpired(long expirationTimeMs) {
            // TODO Use time provider to determine the time instead of System.currentTimeMillis()
            return System.currentTimeMillis() - timestamp > expirationTimeMs;
        }
    }
}

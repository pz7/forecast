package com.pz7.forecast.service;

import com.pz7.forecast.time.TimeStampProvider;

import java.util.HashMap;
import java.util.Map;

public class ForecastCache {

    // TODO manage capacity of the cache
    private final Map<String, CacheEntry> cache = new HashMap<>();
    private final long expirationTimeMs;
    private final TimeStampProvider timeStampProvider;

    public ForecastCache(long expirationTimeMs, TimeStampProvider timeStampProvider) {
        this.expirationTimeMs = expirationTimeMs;
        this.timeStampProvider = timeStampProvider;
    }

    public void put(String address, String value) {
        String key = getKey(address);
        cache.put(key, CacheEntry.of(value, timeStampProvider.getTimeStamp()));
    }

    public String get(String address) {
        String key = getKey(address);
        CacheEntry cacheEntry = cache.get(key);
        if (cacheEntry != null) {
            if (cacheEntry.isExpired(expirationTimeMs, timeStampProvider.getTimeStamp())) {
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
        return address.toLowerCase().replaceAll("[^a-zA-Z0-9]", ""); // removes all non-alphanumeric characters
    }

    private static class CacheEntry {
        public final String value;
        public final long timestamp;

        private CacheEntry(String value, long timeStamp) {
            this.value = value;
            this.timestamp = timeStamp;
        }

        public static CacheEntry of(String value, long timeStamp) {
            return new CacheEntry(value, timeStamp);
        }

        public boolean isExpired(long expirationTimeMs, long currentTime) {
            return currentTime - timestamp > expirationTimeMs;
        }
    }
}

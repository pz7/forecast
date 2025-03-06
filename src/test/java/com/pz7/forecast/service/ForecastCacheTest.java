package com.pz7.forecast.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ForecastCacheTest {

    private static final long EXPIRATION_TIME_MS = 1000; // 1 second
    private ForecastCache forecastCache;

    @BeforeEach
    public void setUp() {
        forecastCache = new ForecastCache(EXPIRATION_TIME_MS);
    }

    @Test
    public void testPutAndGet() {
        String address = "123 Main St";
        String forecast = "Sunny";

        forecastCache.put(address, forecast);
        String result = forecastCache.get(address);

        assertThat(result).isEqualTo(forecast);
    }

    @Test
    public void testGetExpiredEntry() throws InterruptedException {
        String address = "123 Main St";
        String forecast = "Sunny";

        forecastCache.put(address, forecast);
        // TODO Do not use wait for time specific tests, manipulate the time in the test instead
        Thread.sleep(EXPIRATION_TIME_MS + 100); // Wait for the entry to expire
        String result = forecastCache.get(address);

        assertThat(result).isNull();
    }

    @Test
    public void testGetNonExistentEntry() {
        String address = "123 Main St";

        String result = forecastCache.get(address);

        assertThat(result).isNull();
    }

    @Test
    public void testPutUpdatesEntry() {
        String address = "123 Main St";
        String forecast1 = "Sunny";
        String forecast2 = "Rainy";

        forecastCache.put(address, forecast1);
        forecastCache.put(address, forecast2);
        String result = forecastCache.get(address);

        assertThat(result).isEqualTo(forecast2);
    }
}

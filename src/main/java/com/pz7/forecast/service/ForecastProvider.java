package com.pz7.forecast.service;

import java.io.IOException;
import java.net.http.HttpClient;

public class ForecastProvider {

    private static final long expirationTimeMs = 30 * 60 * 1000; // 30 minutes

    private final GeoService geoService;
    private final WeatherService weatherService;
    private final ForecastCache forecastCache;

    public ForecastProvider() {
        this(HttpClient.newHttpClient());
    }

    public ForecastProvider(HttpClient httpClient) {
        this(new GeoService(httpClient), new WeatherService(httpClient), new ForecastCache(expirationTimeMs));
    }

    public ForecastProvider(GeoService geoService,
                            WeatherService weatherService,
                            ForecastCache forecastCache) {
        this.geoService = geoService;
        this.weatherService = weatherService;
        this.forecastCache = forecastCache;
    }

    public String getForecast(String address) throws IOException, InterruptedException {
        String cachedForecast = forecastCache.get(address);
        if (cachedForecast != null) {
            return cachedForecast;
        }

        GeoService.Coordinates coordinates = geoService.getCoordinates(address);
        String forecast = weatherService.getForecast(coordinates);
        forecastCache.put(address, forecast);
        return forecast;
    }
}

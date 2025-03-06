package com.pz7.forecast.service;

import com.pz7.forecast.time.DefaultTimeStampProvider;

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
        this(new GeoService(httpClient),
                new WeatherService(httpClient),
                new ForecastCache(expirationTimeMs, new DefaultTimeStampProvider()));
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
            return cachedForecast + " (**cached indicator**)";
        }

        try {
            GeoService.Response response = geoService.getCoordinates(address);
            String forecast = weatherService.getForecast(response.coordinates);

            String output = formatOutput(response, forecast);
            forecastCache.put(address, output);

            return output;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private static String formatOutput(GeoService.Response response, String forecast) {
        StringBuilder sb = new StringBuilder();
        if (response.city != null) {
            sb.append(response.city);
        }
        if (response.stateCode != null) {
            sb.append(", ").append(response.stateCode.toUpperCase());
        }
        if (response.countryCode != null) {
            sb.append(", ").append(response.countryCode.toUpperCase());
        }
        sb.append(": ").append(forecast);
        return sb.toString();
    }
}

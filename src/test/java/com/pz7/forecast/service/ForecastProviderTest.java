package com.pz7.forecast.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ForecastProviderTest {

    private HttpClient httpClient;
    private ForecastProvider forecastProvider;

    @BeforeEach
    public void setUp() {
        httpClient = mock(HttpClient.class);
        forecastProvider = new ForecastProvider(httpClient);
    }

    @Test
    public void getForecast_HappyPath() throws IOException, InterruptedException {
        String address = "123 Main St";

        // GeoService
        HttpResponse<String> geoResponse = mock(HttpResponse.class);
        when(geoResponse.statusCode()).thenReturn(200);
        when(geoResponse.body()).thenReturn("""
                { "features": [{ "geometry": { "coordinates": [-74.0060, 40.7128] } }] }
                """);
        HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(
                String.format("https://api.geoapify.com/v1/geocode/search?text=%s&apiKey=4bfc17fa7df34e28979b546b13705d94",
                        URLEncoder.encode(address, StandardCharsets.UTF_8)))).build();
        when(httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(geoResponse);

        // WeatherService
        HttpResponse<String> weatherResponse = mock(HttpResponse.class);
        when(weatherResponse.statusCode()).thenReturn(200);
        when(weatherResponse.body()).thenReturn("""
                { "main": { "temp": 75.0, "temp_min": 72.3, "temp_max": 76.8 } }
                """);
        HttpRequest weatherRequest = HttpRequest.newBuilder().uri(URI.create(
                "https://api.openweathermap.org/data/2.5/weather?lat=40.7128&lon=-74.006&appid=d763a233e0d81d5bae49f1b3f3487db7&units=imperial")).build();
        when(httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(weatherResponse);

        String result = forecastProvider.getForecast(address);

        assertThat(result).isEqualTo("Temperature: 75.0 °F, low: 72.3 °F, high: 76.8 °F");

        String cachedResult = forecastProvider.getForecast(address);

        assertThat(cachedResult).isEqualTo("Temperature: 75.0 °F, low: 72.3 °F, high: 76.8 °F (**cached indicator**)");
    }
}

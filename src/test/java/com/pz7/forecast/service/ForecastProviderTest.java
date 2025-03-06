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
        String address = "123 Main St, San Francisco, CA";

        givenGeoService(address, "[40.7128, -74.0060]");
        givenWeatherService("-74.006", "40.7128", "75.0", "72.3", "76.8");

        String result = forecastProvider.getForecast(address);
        assertThat(result).isEqualTo("Temperature: 75.0 °F, low: 72.3 °F, high: 76.8 °F");

        String cachedResult = forecastProvider.getForecast(address);
        assertThat(cachedResult).isEqualTo("Temperature: 75.0 °F, low: 72.3 °F, high: 76.8 °F (**cached indicator**)");

        String addressDiffersWithSpaces = "123 Main St,San Francisco,CA";
        String cachedResult2 = forecastProvider.getForecast(addressDiffersWithSpaces);
        assertThat(cachedResult2).isEqualTo("Temperature: 75.0 °F, low: 72.3 °F, high: 76.8 °F (**cached indicator**)");
    }

    @Test
    public void getForecast_EmptyAddress() throws IOException, InterruptedException {
        givenGeoServiceEmptyAddressResponse();

        String result = forecastProvider.getForecast("");
        assertThat(result).isEqualTo("\"text\" is not allowed to be empty");
    }

    @Test
    public void getForecast_InvalidAddress() throws IOException, InterruptedException {
        givenGeoServiceInvalidAddressResponse();

        String result = forecastProvider.getForecast("aajasfasdf");
        assertThat(result).isEqualTo("No coordinates found for address: aajasfasdf");
    }

    private void givenGeoService(String address, String coordinates) throws IOException, InterruptedException {
        HttpResponse<String> geoResponse = mock(HttpResponse.class);
        when(geoResponse.statusCode()).thenReturn(200);
        when(geoResponse.body()).thenReturn(String.format("""
                { "features": [{ "geometry": { "coordinates": %s } }] }
                """, coordinates));
        HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(
                String.format("https://api.geoapify.com/v1/geocode/search?text=%s&apiKey=4bfc17fa7df34e28979b546b13705d94",
                        URLEncoder.encode(address, StandardCharsets.UTF_8)))).build();
        when(httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(geoResponse);
    }

    private void givenGeoServiceEmptyAddressResponse() throws IOException, InterruptedException {
        HttpResponse<String> geoResponse = mock(HttpResponse.class);
        when(geoResponse.statusCode()).thenReturn(400);
        when(geoResponse.body()).thenReturn("""
                {"statusCode":400,"error":"Bad Request","message":"\\"text\\" is not allowed to be empty"}
                """);
        HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(
                String.format("https://api.geoapify.com/v1/geocode/search?text=%s&apiKey=4bfc17fa7df34e28979b546b13705d94",
                        URLEncoder.encode("", StandardCharsets.UTF_8)))).build();
        when(httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(geoResponse);
    }

    private void givenGeoServiceInvalidAddressResponse() throws IOException, InterruptedException {
        HttpResponse<String> geoResponse = mock(HttpResponse.class);
        when(geoResponse.statusCode()).thenReturn(200);
        when(geoResponse.body()).thenReturn("""
                {"type":"FeatureCollection","features":[],"query":{"text":"aajasfasdf","parsed":{"street":"aajasfasdf","expected_type":"unknown"}}}
                """);
        HttpRequest geoRequest = HttpRequest.newBuilder().uri(URI.create(
                String.format("https://api.geoapify.com/v1/geocode/search?text=%s&apiKey=4bfc17fa7df34e28979b546b13705d94",
                        URLEncoder.encode("aajasfasdf", StandardCharsets.UTF_8)))).build();
        when(httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(geoResponse);
    }

    private void givenWeatherService(String lat, String lon, String temp, String tempMin, String tempMax) throws IOException, InterruptedException {
        // WeatherService
        HttpResponse<String> weatherResponse = mock(HttpResponse.class);
        when(weatherResponse.statusCode()).thenReturn(200);
        when(weatherResponse.body()).thenReturn(String.format("""
                { "main": { "temp": %s, "temp_min": %s, "temp_max": %s } }
                """, temp, tempMin, tempMax));
        HttpRequest weatherRequest = HttpRequest.newBuilder().uri(URI.create(
                String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=d763a233e0d81d5bae49f1b3f3487db7&units=imperial",
                        lat, lon))).build();
        when(httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(weatherResponse);
    }
}

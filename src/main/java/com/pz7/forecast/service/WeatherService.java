package com.pz7.forecast.service;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {
    private static final String WEATHER_API_KEY = "d763a233e0d81d5bae49f1b3f3487db7";

    private final HttpClient client;

    public WeatherService(HttpClient client) {
        this.client = client;
    }

    public String getForecast(GeoService.Coordinates coordinates) throws IOException, InterruptedException {
        String apiUrl = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=imperial",
                coordinates.lat,
                coordinates.lon,
                WEATHER_API_KEY
        );

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONObject main = jsonResponse.getJSONObject("main");
            double temp = main.getDouble("temp");
            return "Temperature: " + temp + " °F";
        }

        throw new IllegalStateException("Weather request failed: " + response.statusCode() + " " + response.body());
    }
}

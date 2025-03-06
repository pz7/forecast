package com.pz7.forecast.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeoService {

    private final HttpClient client;

    public GeoService(HttpClient client) {
        this.client = client;
    }

    public Response getCoordinates(String address) throws IOException, InterruptedException {
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String apiUrl = String.format(
                "https://api.geoapify.com/v1/geocode/search?text=%s&apiKey=%s",
                encodedAddress,
                Secrets.GEOAPIFY_API_KEY
        );

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray features = jsonResponse.getJSONArray("features");
            if (features.length() > 0) {
                JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                double lat = coordinates.getDouble(1);
                double lon = coordinates.getDouble(0);

                JSONObject properties = features.getJSONObject(0).getJSONObject("properties");
                String city = properties.getString("city");
                String stateCode = properties.getString("state_code");
                String countryCode = properties.getString("country_code");

                return Response.of(city, stateCode, countryCode, Coordinates.of(lat, lon));
            } else {
                throw new IllegalArgumentException("No coordinates found for address: " + address);
            }
        } else if(response.statusCode() == 400) {
            JSONObject jsonResponse = new JSONObject(response.body());
            String message = jsonResponse.getString("message");
            throw new IllegalArgumentException(message);
        }

        throw new IllegalStateException("Geo request failed: " + response.statusCode() + " " + response.body());
    }

    public static class Response {
        public final String city;
        public final String stateCode;
        public final String countryCode;
        public final Coordinates coordinates;

        private Response(String city, String stateCode, String countryCode, Coordinates coordinates) {
            this.city = city;
            this.stateCode = stateCode;
            this.countryCode = countryCode;
            this.coordinates = coordinates;
        }

        public static Response of(String city, String stateCode, String countryCode, Coordinates coordinates) {
            return new Response(city, stateCode, countryCode, coordinates);
        }

        public String toString() {
            return "Response[" + "city=" + city + ", stateCode=" + stateCode + ", countryCode=" + countryCode + ']';
        }
    }

    public static class Coordinates {
        public final double lat;
        public final double lon;

        private Coordinates(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public static Coordinates of(double lat, double lon) {
            return new Coordinates(lat, lon);
        }

        public String toString() {
            return "Coordinates[" + "lat=" + lat + ", lon=" + lon + ']';
        }
    }
}

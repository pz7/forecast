package com.pz7.forecast.service;

public class Secrets {
    public final static String GEOAPIFY_API_KEY;
    public final static String WEATHER_API_KEY;

    static {
        String geoApiKey = System.getenv("GEOAPIFY_API_KEY");
        String weatherApiKey = System.getenv("WEATHER_API_KEY");

        // Normally this shouldn't be here.
        if (geoApiKey == null) {
            geoApiKey = "4bfc17fa7df34e28979b546b13705d94";
        }
        if (weatherApiKey == null) {
            weatherApiKey = "d763a233e0d81d5bae49f1b3f3487db7";
        }

        GEOAPIFY_API_KEY = geoApiKey;
        WEATHER_API_KEY = weatherApiKey;
    }
}

package com.pz7.forecast;

import com.pz7.forecast.cli.ForecastCli;

public class ForecastApp {

    public static void main(String[] args) {
        final ForecastCli forecastCli = new ForecastCli();
        forecastCli.run();
    }
}

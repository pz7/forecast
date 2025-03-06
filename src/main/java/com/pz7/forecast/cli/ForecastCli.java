package com.pz7.forecast.cli;

import com.pz7.forecast.service.ForecastProvider;

import java.io.IOException;
import java.util.Scanner;

public class ForecastCli {

    private final ForecastProvider forecastProvider = new ForecastProvider();

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter an address:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Good bye!");
                break;
            } else if (input.equalsIgnoreCase("help")) {
                System.out.println("Type 'exit' or 'quit' to quit the application.");
                continue;
            }

            try {
                String forecast = forecastProvider.getForecast(input);
                System.out.println(forecast);
                System.out.println("-----------------------------");
            } catch (IOException | InterruptedException e) {
                System.err.println("Error retrieving forecast: " + e.getMessage());
            }
        }
    }
}

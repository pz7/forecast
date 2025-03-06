# Forecast App

This is a simple project to forecast the weather using the Geoapify and OpenWeatherMap API.
As of now provides current temperature, low, and high for a given address.
The output will contain ```(**cached indicator**)``` in case the result was retrieved from cache.

## Tests

```
./gradlew test
```

## Execution

Can be run as a CLI using:

```
./gradlew runApp --console=plain
```

These command options are available:
- enter an address to retrieve the current temperature for
- enter ```exit``` or ```quit``` to exit the program
- enter ```help``` for help
# Forecast App

This is a simple project to forecast the weather using the Geoapify and OpenWeatherMap API.

## Test

```
./gradlew test
```

## Execution

The project can be run as a CLI

## Run with Bash script
First build the project and put into distribution folder

```./gradlew installDist```

then run it (you might need to give execution permission to the script using ```chmod```)

```./run-forecast-app.sh```


## Run with gradle task

For testing purposes
```
./gradlew runApp --console=plain
```

# Input
The CLI is interactive and these command options are available:
- enter an address to retrieve the current temperature for
- enter ```exit``` or ```quit``` to exit the program
- enter ```help``` for help

# Output
The app provides city, state code, country code, current temperature, low, and high for a given address.
The output will contain ```(**cached indicator**)``` in case the result was retrieved from cache.

# TODOs
The project is in a certain shape, there are possible follow up improvements:
- Error handling - add error handling of system failure, specifically the connection failures, downstream services unavailability etc.
- Validation - add validation to the input and downstream services requests and responses
- Logging - add logging to the project
- DI - add dependency injection to the project
- Refactor - refactor the code to make it more readable and maintainable and in line with DI
- Cache - use 3rd party cache library to make more robust
- Tests - add more tests
- FE - add a simple frontend to the project
- L10N - add localization to the project
- Performance - evaluate and improve performance of the project
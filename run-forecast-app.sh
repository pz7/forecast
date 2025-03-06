#!/bin/bash

# Set the application name (replace with your actual application name)
APP_NAME="forecast-app"

# Construct the path to the installation directory
INSTALL_DIR="./build/install/$APP_NAME"

# Check if the installation directory exists
if [ ! -d "$INSTALL_DIR" ]; then
  echo "Error: Installation directory '$INSTALL_DIR' not found."
  echo "Please run './gradlew installDist' first."
  exit 1
fi

# Construct the path to the startup script (platform independent)
if [[ "$OSTYPE" == "darwin"* || "$OSTYPE" == "linux-gnu"* ]]; then
  # Unix-like systems (macOS, Linux)
  START_SCRIPT="$INSTALL_DIR/bin/$APP_NAME"
elif [[ "$OSTYPE" == "cygwin" || "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    #Windows
    START_SCRIPT="$INSTALL_DIR/bin/$APP_NAME.bat"
else
  echo "Error: Unsupported operating system."
  exit 1
fi

# Check if the startup script exists
if [ ! -f "$START_SCRIPT" ]; then
  echo "Error: Startup script '$START_SCRIPT' not found."
  exit 1
fi

# Run the startup script
"$START_SCRIPT" "$@"

# Exit with the exit code of the startup script
exit $?
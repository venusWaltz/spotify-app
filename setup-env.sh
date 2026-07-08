#!/usr/bin/env bash

set -euo pipefail

ENV_FILE=".env"
TEMPLATE_FILE=".env.example"
VSCODE_DIR=".vscode"
LAUNCH_JSON="$VSCODE_DIR/launch.json"

echo "Starting local environment setup..."

# check that template file exists
if [ ! -f "$TEMPLATE_FILE" ]; then
    echo "Error: $TEMPLATE_FILE not found. Verify that it exists in the root directory."
    exit 1
fi

# create .env file
if [ ! -f "$ENV_FILE" ]; then
    echo "Creating local $ENV_FILE from template..."
    cp "$TEMPLATE_FILE" "$ENV_FILE"
    echo "Update keys in '.env' with your Spotify Developer project client ID and secret."
else
    echo "$ENV_FILE already exists. Verify that the keys from $TEMPLATE_FILE are included."
fi

# VS Code environment setup
if [ "$TERM_PROGRAM" = "vscode" ] || [ -d "$VSCODE_DIR" ]; then
    echo "VS Code environment detected. Updating configuration..."
    mkdir -p "$VSCODE_DIR"

    # create launch.json with envFile configuration
    if [ ! -f "$LAUNCH_JSON" ]; then
        echo "Creating VS Code launch.json configuration..."
        cat << 'EOF' > "$LAUNCH_JSON"
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Run Application",
            "request": "launch",
            "mainClass": "com.bebopt.app.Application",
            "projectName": "bebopt",
            "envFile": "${workspaceFolder}/.env"
        }
    ]
}
EOF
    else
        echo "$LAUNCH_JSON already exists. Verify '\"envFile\": \"\${workspaceFolder}/.env\"' is included in your configurations."
    fi
else
    echo "Not a VS Code environment. Ensure your project is configured to load variables from $ENV_FILE."
fi

echo "Setup complete."

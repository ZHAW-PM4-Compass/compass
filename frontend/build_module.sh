#!/bin/bash

# Define the URL for your Swagger/OpenAPI documentation
DOCS_URL="http://localhost:8080/v3/api-docs"

# Directory to output the generated TypeScript client
OUTPUT_DIR="./src/api/compassClient"

# Save the current directory
CURRENT_DIR=$(pwd)

# Check if the API documentation is accessible
if curl --output /dev/null --silent --head --fail "$DOCS_URL"; then
  echo "API docs are accessible. Proceeding with client generation."

  # Use openapi-generator-cli to generate the TypeScript client
  npx @openapitools/openapi-generator-cli generate -i $DOCS_URL -g typescript-axios -o $OUTPUT_DIR
  # openapi-generator-cli generate -i https://linktomybackendswagger/swagger.json -g typescript-axios -o src/components/api --additional-properties=supportsES6=true

  # Check if the TypeScript client was generated successfully
  if [ -d "$OUTPUT_DIR" ] && [ "$(ls -A $OUTPUT_DIR)" ]; then
    echo "TypeScript client generated successfully in $OUTPUT_DIR."

    # If there's an existing package.json, update the package name to compass-client
    if [ -f "$OUTPUT_DIR/package.json" ]; then
      npm --prefix $OUTPUT_DIR pkg set name=compass-client
      echo "Package name updated to compass-client."
    else
      echo "No package.json found in $OUTPUT_DIR. Skipping name update."
      # cd $OUTPUT_DIR
      # npm init -y
      # npm pkg set name=compass-client
      # echo "npm package initialized and name set to compass-client."
      # cd $CURRENT_DIR
    fi
  else
    echo "Failed to generate TypeScript client, directory $OUTPUT_DIR is empty."
  fi
else
  echo "API docs are not accessible. Please ensure the API server is running."
  exit 1
fi
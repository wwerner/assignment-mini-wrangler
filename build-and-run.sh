#!/usr/bin/env bash

./gradlew clean build
cd mini-wrangler-cli/build/distributions/
unzip mini-wrangler-cli.zip
cd mini-wrangler-cli
./bin/mini-wrangler-cli
cd ../../../../

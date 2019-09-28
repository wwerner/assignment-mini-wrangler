#!/usr/bin/env bash

./gradlew clean build
cd mini-wrangler-cli/build/distributions/
unzip mini-wrangler-cli.zip
cd mini-wrangler-cli
cd ../../../../
./mini-wrangler-cli/build/distributions/mini-wrangler-cli/bin/mini-wrangler-cli --input demo/orders.csv --config demo/orders-config.kts

#!/usr/bin/env bash

./gradlew distJar

mkdir -p ./storage

java -Xmx512m  -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:MaxDirectMemorySize=512m -Dlog.home=/var/log/buck-cache-client/logs -jar cache/build/libs/cache-1.0.0-standalone.jar server cache/src/dist/config/$1.yml

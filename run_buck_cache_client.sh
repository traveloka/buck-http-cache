#!/usr/bin/env bash

./gradlew distJar

# Based by https://ignite.apache.org/docs/latest/perf-and-troubleshooting/memory-tuning 
# MaxDirectMemorySize should be 4 * walSegmentSize, if we use default walSegmentSize, it will be 256MB
java -Xmx2g  -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:MaxDirectMemorySize=256m -Dlog.home=/var/log/buck-cache-client/logs -jar cache/build/libs/cache-1.0.0-standalone.jar server cache/src/dist/config/$1.yml

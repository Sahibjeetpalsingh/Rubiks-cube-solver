#!/usr/bin/env bash
set -e
mkdir -p bin
javac -encoding UTF-8 -d bin src/*.java
java -cp bin RubikWebServer

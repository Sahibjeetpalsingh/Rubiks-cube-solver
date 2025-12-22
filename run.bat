@echo off
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\*.java
java -cp bin RubikWebServer

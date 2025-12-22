FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy source files
COPY src/ ./src/
COPY public/ ./public/

# Create bin directory and compile
RUN mkdir -p bin && javac -d bin src/*.java

# Expose port
EXPOSE 8080

# Run the server
CMD ["java", "-cp", "bin", "RubikWebServer"]

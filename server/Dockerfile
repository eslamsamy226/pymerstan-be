# Stage 1: Build the application
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app

# Copy the maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the wrapper executable and download dependencies
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

# Copy the source code and build
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
# Note: if a jre tag isn't available for 25 yet, you can safely use the jdk tag here too
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Copy only the compiled jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
# Gunakan image Maven untuk membangun proyek
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory di dalam container
WORKDIR /app

# Salin file konfigurasi Maven (opsional, untuk cache dependency)
COPY pom.xml .
RUN mvn dependency:go-offline

# Salin seluruh kode sumber ke dalam container
COPY . .

# Jalankan Maven build untuk menghasilkan JAR
RUN mvn clean package -DskipTests

# Gunakan image OpenJDK 17 yang lebih ringan untuk menjalankan aplikasi
FROM openjdk:17-jdk-slim

# Set working directory di dalam container
WORKDIR /app

# Salin file JAR hasil build dari tahap sebelumnya
COPY target/credit-simulator.jar /app/credit-simulator.jar

# Set environment variable untuk file JAR
ENV JAR_FILE=/app/credit-simulator.jar

# Perintah untuk menjalankan aplikasi
ENTRYPOINT ["java", "-jar", "/app/credit-simulator.jar"]

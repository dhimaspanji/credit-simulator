#!/bin/bash

# Pindah ke direktori root proyek agar bisa dijalankan dari mana saja
cd "$(dirname "$0")/.."

JAR_FILE="./target/credit-simulator-1.0-SNAPSHOT.jar"

# Periksa apakah JAR sudah ada, jika tidak, build dengan Maven
if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found! Building with Maven..."
    mvn clean package -DskipTests
fi

# Jalankan aplikasi dengan atau tanpa argumen
if [ "$#" -gt 0 ]; then
    java -jar "$JAR_FILE" "$@"
else
    java -jar "$JAR_FILE"
fi

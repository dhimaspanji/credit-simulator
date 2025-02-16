#!/bin/bash

# Menentukan lokasi file JAR
JAR_FILE="./target/credit-simulator-1.0-SNAPSHOT.jar"

# Cek apakah file JAR ada
if [[ ! -f "$JAR_FILE" ]]; then
  echo "Error: File JAR tidak ditemukan di $JAR_FILE"
  exit 1
fi

# Menjalankan aplikasi dengan file input jika diberikan
if [[ $# -eq 0 ]]; then
  java -jar "$JAR_FILE"
else
  java -jar "$JAR_FILE" "$@"
fi

@echo off
set JAR_FILE=.\target\credit-simulator-1.0-SNAPSHOT.jar

:: Cek apakah file JAR ada
if not exist %JAR_FILE% (
    echo Error: File JAR tidak ditemukan di %JAR_FILE%
    exit /b 1
)

:: Menjalankan aplikasi dengan atau tanpa file input
if "%1"=="" (
    java -jar %JAR_FILE%
) else (
    java -jar %JAR_FILE% %*
)

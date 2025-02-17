@echo off
cd /d "%~dp0\.."

set JAR_FILE=.\target\credit-simulator-1.0-SNAPSHOT.jar

:: Periksa apakah JAR sudah ada, jika tidak, build dengan Maven
if not exist %JAR_FILE% (
    echo JAR file not found! Building with Maven...
    mvn clean package -DskipTests
)

:: Jalankan aplikasi dengan atau tanpa argumen
if "%1"=="" (
    java -jar %JAR_FILE%
) else (
    java -jar %JAR_FILE% %*
)

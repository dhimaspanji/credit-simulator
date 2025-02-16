FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/credit_simulator-1.0-SNAPSHOT.jar /app/credit_simulator.jar

COPY file_input.txt /app/file_input.txt

RUN mkdir -p /app/bin
COPY /bin/credit_simulator /app/bin/credit_simulator
COPY /bin/credit_simulator.bat /app/bin/credit_simulator.bat
RUN chmod +x /app/bin/credit_simulator

ENV PATH="/app/bin:${PATH}"

ENTRYPOINT ["java", "-jar", "credit_simulator.jar"]

FROM eclipse-temurin:21-jdk-jammy

RUN apt-get update
RUN apt install -y curl webp

RUN curl -sSf https://atlasgo.sh | sh

RUN adduser --disabled-password --gecos "" temurin
USER temurin

WORKDIR /home/temurin
COPY --chown=temurin:temurin . .

RUN ./gradlew assemble

CMD ["./gradlew", "spring-app:bootRun"]

FROM ubuntu:24.04
RUN apt-get update
RUN apt install -y wget apt-transport-https gpg webp curl

RUN wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor | tee /etc/apt/trusted.gpg.d/adoptium.gpg > /dev/null
RUN echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list
RUN apt-get update
RUN apt-get install temurin-21-jdk -y

RUN curl -fsSL https://deb.nodesource.com/setup_22.x -o nodesource_setup.sh
RUN bash nodesource_setup.sh
RUN apt-get install -y nodejs

RUN curl -sSf https://atlasgo.sh | sh

RUN adduser --disabled-password --gecos "" snowfoxstudio
USER snowfoxstudio

RUN mkdir /home/snowfoxstudio/albion-square
WORKDIR /home/snowfoxstudio/albion-square
COPY --chown=snowfoxstudio:snowfoxstudio . .

RUN ./gradlew assemble

ENV NODE_ENV=production

CMD ["sleep", "infinity"]

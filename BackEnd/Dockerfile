FROM gradle:8.10-jdk17
WORKDIR /opt/app
COPY ./build/libs/s3-cb1-3-io-room-usage-detection-backend-0.0.1-SNAPSHOT.jar ./

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar s3-cb1-3-io-room-usage-detection-backend-0.0.1-SNAPSHOT.jar"]
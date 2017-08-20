FROM java:8
VOLUME /tmp
ADD twitterfy-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
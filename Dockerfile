#The Docker image, based on the JDK Version
FROM openjdk:17.0.2-jdk-slim
# Add the jar, being built by the app, based on the final name in the POM (Path Name of the jar)
ADD target/air-nz-mailer-service.jar air-nz-mailer-service.jar
#Expose the port which must be the same as in the application.yml file
EXPOSE 1300
#Entry point (as to run a jar - add more commands if required)
ENTRYPOINT ["java", "-jar", "air-nz-mailer-service.jar"]
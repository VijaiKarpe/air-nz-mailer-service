**Build**

mvn clean install -U

**Docker Container**

_Create a Docker Image **air-nz--mailer-service** in the local folder for Docker_

Build Command (Intel): docker build -f Dockerfile -t air-nz-mailer-service:0.0.1 .

Build Command (Apple Silicon): docker buildx build -t air-nz-mailer-service:0.0.1 . --platform linux/amd64

Run the Image using the port (The port has to be the same as specified in the application's properties)

Run Command: docker run -p 1300:1300 air-nz-mailer-service:0.0.1

**Push to Docker Hub**
docker tag <image-name> <username>/<repository>:<tag>
docker tag air-nz-mailer-service:0.0.1 karpetec/primezone:air-nz-mailer-service-0.0.1

docker push <username>/<repository>:<tag>
docker push karpetec/primezone:air-nz-mailer-service-0.0.1

**Log files Info**

Windows: The destination folder for the logs will be in the following location in which ever drive the springboot application is run.

{Drive}/ServiceLogs/air-nz-mailer-service-0.0.1


Mac / Linux: Change the path to the User's Home Directory _prior to building project_


**Open API Swagger UI**

http://localhost:1300/air-nz-mailer-service/swagger-ui/index.html

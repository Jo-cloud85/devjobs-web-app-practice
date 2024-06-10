# Build Angular
FROM node:22 AS ngbuild

WORKDIR /frontend

# Install Angular
RUN npm i -g @angular/cli@17.3.8

COPY frontend/angular.json .
COPY frontend/package*.json .
COPY frontend/tsconfig*.json .
COPY frontend/src src

# Install modules (ci is faster than using i but don't use for development)
# We include && because we only want to run ng build only if npm ci is successful
RUN npm ci && ng build

# Build Spring Boot
FROM openjdk:21 AS javabuild

WORKDIR /backend

COPY backend/mvnw .
COPY backend/pom.xml .
COPY backend/.mvn .mvn
COPY backend/src src

# Copy Angular files to Spring Boot
COPY --from=ngbuild /frontend/dist/frontend/browser/ src/main/resources/static

# produce target/day36.giphy-0.0.1-SNAPSHOT.jar
RUN ./mvnw package -Dmaven.test.skip=true

# Run container
FROM openjdk:21

WORKDIR /app

COPY --from=javabuild /backend/target/backend-0.0.1-SNAPSHOT.jar app.jar

ENV S3_SECRET_KEY=abc123
ENV S3_ACCESS_KEY=abc123
ENV PORT=5050

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar
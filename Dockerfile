# Build
FROM gradle:8.6.0-jdk17-alpine AS builder
WORKDIR /build
COPY . .
RUN gradle clean build --no-daemon -x test --parallel

# Run
FROM openjdk:17-jdk-alpine
WORKDIR /app

RUN apk add --no-cache tzdata
ENV TZ=Asia/Shanghai

COPY --from=builder /build/build/libs/jsbsp-server-0.0.1.jar jsbsp-server.jar
EXPOSE 57277
CMD ["java", "-jar", "jsbsp-server.jar"]
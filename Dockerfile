FROM gradle:jdk-21-and-22-graal AS builder

WORKDIR /app

COPY . .

RUN ./gradlew nativeCompile

FROM alpine:3.19

WORKDIR /app

RUN apk add --no-cache libpq libssl3

COPY --from=builder /app/build/native/nativeCompile/* /app/

ENV SPRING_PROFILES_ACTIVE=production
ENV POSTGRES_URL=""
ENV POSTGRES_USERNAME=""
ENV POSTGRES_PASSWORD=""

EXPOSE 8080

ENTRYPOINT ["/app/liquidonline-server"]
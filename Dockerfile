FROM ghcr.io/graalvm/native-image-community:21 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew nativeCompile

FROM alpine:3.19

WORKDIR /app

RUN apk add --no-cache libpq libssl1.1

COPY --from=builder /app/build/native/nativeCompile/* /app/

ENV SPRING_PROFILES_ACTIVE=production
ENV POSTGRES_URL=""
ENV POSTGRES_USERNAME=""
ENV POSTGRES_PASSWORD=""

EXPOSE 8080

ENTRYPOINT ["/app/liquidonline-server"]
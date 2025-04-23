FROM ghcr.io/graalvm/native-image-community:21-muslib AS builder

RUN microdnf install -y findutils

WORKDIR /app

COPY . .

RUN ./gradlew nativeCompile

FROM alpine:3.19

WORKDIR /app

RUN apk add --no-cache libpq libssl3

COPY --from=builder /app/build/native/nativeCompile/* /app/bin/

ENV SPRING_PROFILES_ACTIVE=production
ENV POSTGRES_URL=""
ENV POSTGRES_USERNAME=""
ENV POSTGRES_PASSWORD=""

EXPOSE 8080

ENTRYPOINT ["/app/bin/liquidonline-server"]
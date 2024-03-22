# Frontend Build Stage
FROM node:20 AS frontend-builder
WORKDIR /app
COPY ./frontend/package*.json ./
RUN npm ci
COPY ./frontend/ .
RUN apt-get update && apt-get install -y wget
RUN wget -q https://github.com/getsentry/sentry-cli/releases/download/2.3.0/sentry-cli-Linux-x86_64 -O /usr/local/bin/sentry-cli
RUN chmod +x /usr/local/bin/sentry-cli
RUN npm run build

# Backend Build Stage
FROM gradle:8.6.0-jdk21 AS backend-builder
WORKDIR /app
COPY ./backend/build.gradle .
COPY ./backend/settings.gradle .
COPY ./backend/src src
RUN gradle build --no-daemon

# Final Image Preparation
FROM ubuntu:latest
# Install Node.js and JDK
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk nodejs npm && \
    apt-get clean

# Set the working directory in the container
WORKDIR /app

# Copy frontend and backend artifacts
COPY --from=frontend-builder /app/.next ./.next
COPY --from=frontend-builder /app/node_modules ./node_modules
COPY --from=backend-builder /app/build/libs/*.jar ./backend/app.jar

# Copy the startup script
COPY start.sh .
RUN chmod +x start.sh

EXPOSE 3000 8080

CMD ["./start.sh"]

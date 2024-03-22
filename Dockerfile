# Use Node Version 16 as builder
FROM node:20 AS builder

# Set the working directory to where your app is located
WORKDIR /app
COPY ./frontend/package*.json ./
RUN npm ci

COPY ./frontend/ .

# Sentry Docker patch remains the same
RUN apt-get update && apt-get install -y wget
RUN wget -q https://github.com/getsentry/sentry-cli/releases/download/2.3.0/sentry-cli-Linux-x86_64 -O /usr/local/bin/sentry-cli
RUN chmod +x /usr/local/bin/sentry-cli

# Use npm to run the build script
RUN npm run build

# Use the latest Node Version as the final image
FROM node:20
WORKDIR /app

# Copy necessary files from the builder stage
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules

EXPOSE 3000

# Use npm to start the application
CMD ["npm", "start"]

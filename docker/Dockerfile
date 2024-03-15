# Lets use Node Version 16 as builder
FROM node:20 AS builder

# Set the working directory
WORKDIR /app
COPY package*.json ./

# RUN will be triggered if the package.json or yarn.lock file changes
COPY yarn.lock ./
RUN npm install

# Copy the rest of the files
COPY . .

# Sentry Docker patch
RUN apt-get update && apt-get install -y wget
RUN wget -q https://github.com/getsentry/sentry-cli/releases/download/2.3.0/sentry-cli-Linux-x86_64 -O /usr/local/bin/sentry-cli
RUN chmod +x /usr/local/bin/sentry-cli

# Use yarn to run the build script
RUN npm run build

# Use the latest Node Version as the final image
FROM node:20
WORKDIR /app
COPY --from=builder /app/next.config.js ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
EXPOSE 3000

# Use yarn to start the application
CMD ["npm", "start"]

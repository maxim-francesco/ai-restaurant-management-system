# Stage 1: Build Angular App
FROM node:20-alpine AS build

WORKDIR /app

# Copiem package.json și package-lock.json
COPY package*.json ./

# Instalăm dependințele
RUN npm ci --legacy-peer-deps

# Copiem sursele aplicației
COPY public/ /client-app/public
COPY src/ /client-app/src
COPY package.json /client-app
COPY tsconfig.json /client-app

# Build de producție
RUN npm run build --prod

# Stage 2: Server static minimalist cu http-server
FROM node:20-alpine AS runtime

WORKDIR /app

# Instalăm http-server global
RUN npm install -g http-server

# Copiem build-ul din stage-ul anterior
COPY --from=build /app/dist/frontend /app

# Expunem portul pe care va servi aplicația
EXPOSE 8080

# Pornim aplicația
CMD ["http-server", "-p", "8080", "."]

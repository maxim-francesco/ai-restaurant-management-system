# Stage 1: Build Angular App
FROM node:20-alpine AS build

WORKDIR /admin-app

# Copiem package.json și package-lock.json
COPY package*.json ./

# Instalăm dependințele
RUN npm ci --legacy-peer-deps

# Copiem sursele aplicației
COPY public/ /admin-app/public
COPY src/ /admin-app/src
COPY package.json /admin-app
COPY tsconfig.json /admin-app

# Build de producție
RUN npm run build --prod

# Stage 2: Server static minimalist cu http-server
FROM node:20-alpine AS runtime

WORKDIR /admin-app

# Instalăm http-server global
RUN npm install -g http-server

# Copiem build-ul din stage-ul anterior
COPY --from=build /dist/frontend /admin-app

# Expunem portul pe care va servi aplicația
EXPOSE 80

# Pornim aplicația
CMD ["http-server", "-p", "80", "."]

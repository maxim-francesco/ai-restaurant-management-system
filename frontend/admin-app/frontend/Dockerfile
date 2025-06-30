FROM node:20-alpine
WORKDIR /admin-app
COPY public/ /admin-app/public
COPY src/ /admin-app/src
COPY package.json /admin-app
COPY tsconfig.json /admin-app
COPY node_modules/ /admin-app/node_modules
#RUN npm install
EXPOSE 4200
ENTRYPOINT ["npm", "start", "--legacy-peer-deps"]
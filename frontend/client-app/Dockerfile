FROM node:20-alpine
WORKDIR /client-app
COPY public/ /client-app/public
COPY src/ /client-app/src
COPY package.json /client-app
COPY tsconfig.json /client-app
COPY node_modules/ /client-app/node_modules
#RUN npm install
EXPOSE 3000
ENTRYPOINT ["npm", "start"]
FROM node:24 AS build
WORKDIR /app
COPY package*.json angular.json tsconfig*.json ./
RUN npm install
COPY src src
RUN npm run build

FROM nginx:alpine
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

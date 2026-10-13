FROM node:24 AS build
WORKDIR /app
COPY frontend/package.json frontend/package-lock.json frontend/angular.json frontend/tsconfig.json frontend/tsconfig.app.json /app/
RUN npm install
COPY frontend/src /app/src/
RUN npm run build

FROM nginx:alpine
COPY frontend/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

FROM node:22 AS build
WORKDIR /app
COPY package.json package-lock.json index.html vite.config.js ./
COPY src src
RUN npm install
RUN npm run build

FROM nginx:alpine
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

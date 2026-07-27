# ---- Build stage ----
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build

# ---- Runtime stage ----
# nginx-unprivileged: same nginx build, but pre-configured to run as a
# non-root user and listen on unprivileged ports -- lets us set
# runAsNonRoot: true in Kubernetes without nginx needing root just to bind
# port 80 (which would otherwise fail with "Permission denied").
FROM nginxinc/nginx-unprivileged:1.27-alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY docker-entrypoint.sh /docker-entrypoint.d/40-inject-env.sh
USER root
RUN chmod +x /docker-entrypoint.d/40-inject-env.sh \
    # Pick up patched OS packages at build time rather than trusting
    # whatever was current when the base image was last published -- this is
    # what actually closes the CVEs Trivy flags (c-ares, openssl, etc.),
    # not just bumping the base image tag once.
    && apk update && apk upgrade --no-cache
USER 101
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s CMD wget -qO- http://127.0.0.1:8080/health || exit 1

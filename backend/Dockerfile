# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Explicit numeric UID/GID, not just a named user: Kubernetes' runAsNonRoot
# check happens at pod admission, before the entrypoint runs, and it can only
# verify "non-root" by reading the image's declared numeric user straight out
# of the image config. A named user like "spring" can't be resolved without
# executing code, so runAsNonRoot: true fails closed with "cannot verify user
# is non-root" even though the user genuinely isn't root.
RUN addgroup --system --gid 1000 spring && adduser --system --uid 1000 --ingroup spring spring
COPY --from=build /app/target/ai-bankapp-backend.jar app.jar
USER 1000
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]

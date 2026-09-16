FROM node:24-bookworm-slim AS web
WORKDIR /app/frontend
RUN npm install --global pnpm@11.19.0
COPY frontend/package.json frontend/pnpm-lock.yaml frontend/pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile
COPY frontend/ ./
RUN pnpm build

FROM maven:3.9.11-eclipse-temurin-21 AS java
WORKDIR /app
COPY pom.xml ./
COPY src ./src
COPY --from=web /app/frontend/dist ./frontend/dist
RUN mvn -B verify

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 app && mkdir /app/data && chown app /app/data
COPY --from=java /app/target/maos-em-acao-1.0.0.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

FROM maven:3-eclipse-temurin-11 as builder
COPY . .
RUN mvn --batch-mode verify

FROM eclipse-temurin:11
WORKDIR /core
RUN mkdir /core/plugins
RUN mkdir /core/files

COPY --from=builder /core/target/core-1.0.jar /core/
COPY --from=builder /core/target/plugins/*.jar /core/plugins/

CMD ["java", "--module-path", "/core/core-1.0.jar", "--module", "core", "-console", "-cs", "UTF-8", "-f", "files", "-captionml"]

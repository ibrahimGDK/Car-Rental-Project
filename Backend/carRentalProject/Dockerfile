# 1. Java 17 içeren küçük bir imaj kullan
FROM openjdk:17-jdk-alpine

# 2. Çalışma dizini oluştur
WORKDIR /app

# 3. Jar dosyasını konteynıra kopyala
COPY target/carRentalProject-0.0.1-SNAPSHOT.jar app.jar

# 4. Spring Boot uygulamasının portunu dışa aç
EXPOSE 8001

# 5. Uygulamayı çalıştır
ENTRYPOINT ["java", "-jar", "app.jar"]

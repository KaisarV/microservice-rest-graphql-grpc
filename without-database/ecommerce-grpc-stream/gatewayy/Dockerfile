# Load Versi Java
FROM openjdk:11

# Menentukan Lokasi File JAR
ARG JAR_FILE=gateway-server/target/*.jar

# Copy Konfigurasi Aplikasi dari JAR ke Image pada Kontainer
COPY ${JAR_FILE} app.jar

# Mengatur variabel lingkungan untuk profil Spring Boot
ENV SPRING_PROFILES_ACTIVE=docker

# Eksekusi File ketika kontainer dijalankan
ENTRYPOINT ["java","-jar","/app.jar"]
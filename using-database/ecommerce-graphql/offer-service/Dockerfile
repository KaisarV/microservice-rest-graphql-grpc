
#Load Versi Java
FROM openjdk:11

#Menentukan Lokasi File JAR
ARG JAR_FILE=target/*.jar

#Copy Konfigurasi Aplikasi dari JAR ke Image pada Kontainer
COPY ${JAR_FILE} app.jar

# Mengatur variabel lingkungan untuk profil Spring Boot
ENV SPRING_PROFILES_ACTIVE=docker

#Executable File ketika kontainer dijalankan
ENTRYPOINT ["java","-jar","/app.jar"]



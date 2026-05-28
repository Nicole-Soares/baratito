# Usamos la imagen de Java 17
FROM eclipse-temurin:17-jdk-alpine

# Definimos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos todos los archivos del backend al contenedor
COPY . .

# Damos permiso de ejecución al script de Gradle (Clave para evitar errores)
RUN chmod +x ./gradlew

# Compilamos el proyecto saltando los tests para que sea más rápido
RUN ./gradlew bootJar -x test

# Usamos un comando más flexible para ejecutar el JAR sin importar el nombre exacto
ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]

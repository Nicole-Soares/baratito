# 🛒 Baratito

**Baratito** es una plataforma web diseñada para centralizar y comparar los precios de productos en diferentes supermercados. El objetivo principal es ahorrarle tiempo y dinero a los usuarios, evitando que tengan que navegar por múltiples páginas web o recorrer tiendas físicas para encontrar las mejores ofertas.

---

## 🚀 Características Principales

- **Autenticación de Usuarios:** Registro e inicio de sesión seguro para una experiencia personalizada.
- **Búsqueda Avanzada:** Buscador eficiente de productos con información de precios actualizada de distintos supermercados.
- **Carrito Comparador:** El usuario puede agregar productos a un carrito inteligente que calcula el costo total estimado en cada cadena de supermercados, permitiendo ver dónde conviene hacer la compra completa.

---

## 🛠️ Stack Tecnológico

El proyecto está dividido en una arquitectura desacoplada (Frontend y Backend) utilizando las siguientes tecnologías:

### Frontend
- **Framework:** React + Vite
- **Estilos:** HTML5 y CSS3 nativo.

### Backend
- **Lenguaje:** Java
- **Framework:** Spring Boot
- **Gestor de Dependencias:** Gradle
- **Base de Datos:** PostgreSQL

### Infraestructura
- **Contenerización:** Docker y Docker Compose (para un despliegue rápido y consistente en cualquier entorno).

---

## 📦 Requisitos Previos

Antes de empezar, asegurate de tener instalado localmente:
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Git](https://git-scm.com/)

---

## ⚙️ Instalación y Ejecución Local

Con docker compose up --build, Docker lee los archivos de configuración (llamados Dockerfile) que estan dentro de la carpeta del frontend y del backend.

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Nicole-Soares/baratito.git
   cd baratito
   
2. **Ejecutar el siguiente comando en la raíz del proyecto:**
    ```bash
    docker compose up --build
   
 Nota: Agregá  -d al final si preferís correr los contenedores en segundo plano (detached mode).
 
3. **Acceder a la aplicación:**

- **web:** https://baratito-sage.vercel.app/

- **Con el usuario:** usuario@gmail.com  / password: 123456


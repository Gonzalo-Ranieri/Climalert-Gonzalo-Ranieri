# Climalert

Climalert es un servicio autónomo de monitoreo climático y envío automático de alertas por correo electrónico ante condiciones climáticas inusuales o críticas (temperatura superior a 35°C y humedad mayor a 60%).

## Requisitos Previos

- Java 21
- Docker y Docker Compose

## Configuración del Entorno de Desarrollo (SMTP Local con MailHog)

Para probar el envío de correos localmente sin usar credenciales SMTP reales, utilizamos **MailHog** como servidor SMTP de prueba.

1. **Levantar MailHog con Docker**:
   Ejecuta el siguiente comando en la raíz del proyecto para iniciar el contenedor en segundo plano:
   ```bash
   docker compose up -d
   ```

2. **Acceder a la Interfaz de Correo**:
   Una vez que el contenedor esté corriendo, puedes ver todos los correos electrónicos interceptados y enviados por la aplicación ingresando a:
   - **Bandeja de Entrada (Web UI)**: [http://localhost:8025](http://localhost:8025)
   - **Servidor SMTP (interno)**: `localhost:1025` (ya configurado en el perfil `dev`).

3. **Detener el Contenedor**:
   Si deseas detener MailHog, ejecuta:
   ```bash
   docker compose down
   ```

## Ejecución del Proyecto con Perfil de Desarrollo

Para iniciar la aplicación Spring Boot activando el perfil `dev` (que utiliza MailHog), puedes ejecutar:

```bash
# Con Maven Wrapper (si estuviera disponible en tu IDE o entorno local):
./mvnw spring-boot:run -Dspring.profiles.active=dev

# O desde tu IDE de preferencia, estableciendo la propiedad de máquina virtual de Java:
-Dspring.profiles.active=dev
```

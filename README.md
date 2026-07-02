# CanchApp - Gestion de Reservas

Modulo de Gestion de Reservas del sistema CanchApp, desarrollado con arquitectura en capas para el Segundo Parcial de Diseno de Sistemas.

## Tecnologias

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven

## Arquitectura

El proyecto respeta la arquitectura en capas definida para CanchApp:

- `controller`: recibe las solicitudes HTTP y devuelve respuestas.
- `service`: contiene la logica de negocio.
- `repository`: acceso a datos con Spring Data JPA.
- `entity`: entidades persistentes del modelo.
- `dto`: objetos de entrada, salida y error.
- `exception`: manejo centralizado de errores.
- `config`: configuracion general de la aplicacion.

## Modelo de datos

Entidades implementadas:

- `Usuario`: `idUsuario`, `nombre`, `email`, `telefono`.
- `Cancha`: `idCancha`, `nombre`, `tipo`, `precioHora`, `activa`.
- `Reserva`: `idReserva`, `usuario`, `cancha`, `fecha`, `horaInicio`, `horaFin`, `estado`.

Estados de reserva:

- `ACTIVA`
- `CANCELADA`

Los scripts de base de datos estan en:

- `src/main/resources/schema.sql`
- `src/main/resources/data.sql`

## Regla de negocio principal

No se permite crear ni modificar una reserva si existe otra reserva `ACTIVA` para la misma cancha, fecha y horario superpuesto.

La superposicion se valida con:

```text
nuevaHoraInicio < horaFinExistente
AND
nuevaHoraFin > horaInicioExistente
```

## Configuracion de PostgreSQL

Crear una base de datos llamada `canchapp`:

```sql
CREATE DATABASE canchapp;
```

Editar `src/main/resources/application.properties` si tus credenciales son distintas:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/canchapp
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Ejecucion

Desde la carpeta del proyecto:

```bash
mvn spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

## Endpoints

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| POST | `/reservas` | Crear reserva |
| GET | `/reservas` | Consultar todas las reservas |
| GET | `/reservas/{id}` | Consultar una reserva por ID |
| PUT | `/reservas/{id}` | Modificar una reserva |
| DELETE | `/reservas/{id}` | Cancelar una reserva |

## Ejemplos JSON

Crear reserva:

```json
{
  "idUsuario": 1,
  "idCancha": 1,
  "fecha": "2026-07-10",
  "horaInicio": "17:00",
  "horaFin": "18:00"
}
```

Modificar reserva:

```json
{
  "idUsuario": 2,
  "idCancha": 2,
  "fecha": "2026-07-12",
  "horaInicio": "18:00",
  "horaFin": "19:30"
}
```

Respuesta de error:

```json
{
  "timestamp": "2026-07-01T20:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "El horario ya esta ocupado para la cancha seleccionada",
  "path": "/reservas"
}
```

## Casos de prueba manuales

| Caso | Request | Resultado esperado |
| --- | --- | --- |
| Crear reserva valida | `POST /reservas` con cancha activa y horario libre | `201 Created` y reserva `ACTIVA` |
| Usuario inexistente | `POST /reservas` con `idUsuario` inexistente | `404 Not Found` |
| Cancha inexistente | `POST /reservas` con `idCancha` inexistente | `404 Not Found` |
| Cancha inactiva | `POST /reservas` con `idCancha: 3` | `400 Bad Request` |
| Horario invalido | `POST /reservas` con `horaFin <= horaInicio` | `400 Bad Request` |
| Horario ocupado | `POST /reservas` para cancha 1, `2026-07-10`, `18:30` a `19:30` | `409 Conflict` |
| Consultar reservas | `GET /reservas` | `200 OK` con listado |
| Cancelar reserva | `DELETE /reservas/1` | `200 OK` con estado `CANCELADA` |

## Trazabilidad diseno a codigo

| Diseno del Primer Parcial | Implementacion |
| --- | --- |
| Flujo Crear reserva de cancha | `ReservaController.crearReserva`, `ReservaService.crearReserva` |
| Validacion de disponibilidad | `ReservaService.validarDisponibilidad`, `ReservaRepository.existsReservaActivaSuperpuesta` |
| Flujo Cancelar reserva | `ReservaController.cancelarReserva`, `ReservaService.cancelarReserva` |
| Arquitectura en capas | Paquetes `controller`, `service`, `repository`, `entity`, `dto`, `exception`, `config` |
| Persistencia del modulo | Entidades JPA y scripts `schema.sql` / `data.sql` |

## Decisiones e inconsistencias detectadas

En los PDF revisados no aparece un DDL especifico distinto al modelo solicitado para este modulo. Por eso se implemento el esquema con las entidades y atributos indicados: `Usuario`, `Cancha` y `Reserva`.

DROP TABLE IF EXISTS reservas;
DROP TABLE IF EXISTS canchas;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id_usuario BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    telefono VARCHAR(30)
);

CREATE TABLE canchas (
    id_cancha BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    precio_hora NUMERIC(10, 2) NOT NULL,
    activa BOOLEAN NOT NULL
);

CREATE TABLE reservas (
    id_reserva BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_cancha BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT fk_reservas_usuarios FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_reservas_canchas FOREIGN KEY (id_cancha) REFERENCES canchas(id_cancha),
    CONSTRAINT chk_reservas_estado CHECK (estado IN ('ACTIVA', 'CANCELADA')),
    CONSTRAINT chk_reservas_horario CHECK (hora_fin > hora_inicio)
);

INSERT INTO usuarios (nombre, email, telefono) VALUES
('Martin Bianco', 'martin.bianco@example.com', '1122334455'),
('Lucia Gomez', 'lucia.gomez@example.com', '1166677788'),
('Juan Perez', 'juan.perez@example.com', '1199988877');

INSERT INTO canchas (nombre, tipo, precio_hora, activa) VALUES
('Cancha 1', 'Futbol 5', 12000.00, true),
('Cancha 2', 'Futbol 7', 18000.00, true),
('Cancha 3', 'Padel', 10000.00, false);

INSERT INTO reservas (id_usuario, id_cancha, fecha, hora_inicio, hora_fin, estado) VALUES
(1, 1, '2026-07-10', '18:00', '19:00', 'ACTIVA'),
(2, 2, '2026-07-10', '19:00', '20:00', 'ACTIVA'),
(3, 1, '2026-07-11', '20:00', '21:00', 'CANCELADA');

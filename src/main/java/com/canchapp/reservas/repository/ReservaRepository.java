package com.canchapp.reservas.repository;

import com.canchapp.reservas.entity.EstadoReserva;
import com.canchapp.reservas.entity.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
            select count(r) > 0
            from Reserva r
            where r.cancha.idCancha = :idCancha
              and r.fecha = :fecha
              and r.estado = :estado
              and (:idReservaIgnorada is null or r.idReserva <> :idReservaIgnorada)
              and :horaInicio < r.horaFin
              and :horaFin > r.horaInicio
            """)
    boolean existsReservaActivaSuperpuesta(
            @Param("idCancha") Long idCancha,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("estado") EstadoReserva estado,
            @Param("idReservaIgnorada") Long idReservaIgnorada);
}

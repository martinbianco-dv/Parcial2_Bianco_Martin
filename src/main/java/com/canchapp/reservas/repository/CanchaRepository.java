package com.canchapp.reservas.repository;

import com.canchapp.reservas.entity.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanchaRepository extends JpaRepository<Cancha, Long> {
}

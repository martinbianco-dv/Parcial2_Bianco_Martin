package com.canchapp.reservas.controller;

import com.canchapp.reservas.dto.ReservaRequest;
import com.canchapp.reservas.dto.ReservaResponse;
import com.canchapp.reservas.service.ReservaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crearReserva(@Valid @RequestBody ReservaRequest request) {
        return reservaService.crearReserva(request);
    }

    @GetMapping
    public List<ReservaResponse> consultarReservas() {
        return reservaService.consultarReservas();
    }

    @GetMapping("/{id}")
    public ReservaResponse consultarReservaPorId(@PathVariable Long id) {
        return reservaService.consultarReservaPorId(id);
    }

    @PutMapping("/{id}")
    public ReservaResponse modificarReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequest request) {
        return reservaService.modificarReserva(id, request);
    }

    @DeleteMapping("/{id}")
    public ReservaResponse cancelarReserva(@PathVariable Long id) {
        return reservaService.cancelarReserva(id);
    }
}

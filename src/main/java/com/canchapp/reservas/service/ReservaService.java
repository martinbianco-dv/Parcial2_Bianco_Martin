package com.canchapp.reservas.service;

import com.canchapp.reservas.dto.ReservaRequest;
import com.canchapp.reservas.dto.ReservaResponse;
import com.canchapp.reservas.entity.Cancha;
import com.canchapp.reservas.entity.EstadoReserva;
import com.canchapp.reservas.entity.Reserva;
import com.canchapp.reservas.entity.Usuario;
import com.canchapp.reservas.exception.BusinessException;
import com.canchapp.reservas.exception.ResourceNotFoundException;
import com.canchapp.reservas.repository.CanchaRepository;
import com.canchapp.reservas.repository.ReservaRepository;
import com.canchapp.reservas.repository.UsuarioRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CanchaRepository canchaRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository,
                          CanchaRepository canchaRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.canchaRepository = canchaRepository;
    }

    @Transactional
    public ReservaResponse crearReserva(ReservaRequest request) {
        Usuario usuario = buscarUsuario(request.getIdUsuario());
        Cancha cancha = buscarCanchaActiva(request.getIdCancha());
        validarHorario(request);
        validarDisponibilidad(cancha.getIdCancha(), request, null);

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(request.getFecha());
        reserva.setHoraInicio(request.getHoraInicio());
        reserva.setHoraFin(request.getHoraFin());
        reserva.setEstado(EstadoReserva.ACTIVA);

        return toResponse(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> consultarReservas() {
        return reservaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse consultarReservaPorId(Long id) {
        return toResponse(buscarReserva(id));
    }

    @Transactional
    public ReservaResponse modificarReserva(Long id, ReservaRequest request) {
        Reserva reserva = buscarReserva(id);
        Usuario usuario = buscarUsuario(request.getIdUsuario());
        Cancha cancha = buscarCanchaActiva(request.getIdCancha());
        validarHorario(request);
        validarDisponibilidad(cancha.getIdCancha(), request, reserva.getIdReserva());

        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(request.getFecha());
        reserva.setHoraInicio(request.getHoraInicio());
        reserva.setHoraFin(request.getHoraFin());
        reserva.setEstado(EstadoReserva.ACTIVA);

        return toResponse(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponse cancelarReserva(Long id) {
        Reserva reserva = buscarReserva(id);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new BusinessException("La reserva ya se encuentra cancelada", HttpStatus.CONFLICT);
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return toResponse(reservaRepository.save(reserva));
    }

    private Usuario buscarUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));
    }

    private Cancha buscarCanchaActiva(Long idCancha) {
        Cancha cancha = canchaRepository.findById(idCancha)
                .orElseThrow(() -> new ResourceNotFoundException("La cancha no existe"));

        if (!Boolean.TRUE.equals(cancha.getActiva())) {
            throw new BusinessException("La cancha esta inactiva", HttpStatus.BAD_REQUEST);
        }

        return cancha;
    }

    private Reserva buscarReserva(Long idReserva) {
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("La reserva no existe"));
    }

    private void validarHorario(ReservaRequest request) {
        if (!request.getHoraFin().isAfter(request.getHoraInicio())) {
            throw new BusinessException("La hora de fin debe ser posterior a la hora de inicio", HttpStatus.BAD_REQUEST);
        }
    }

    private void validarDisponibilidad(Long idCancha, ReservaRequest request, Long idReservaIgnorada) {
        // Regla de negocio: una reserva se superpone si nuevaInicio < finExistente y nuevaFin > inicioExistente.
        boolean horarioOcupado = reservaRepository.existsReservaActivaSuperpuesta(
                idCancha,
                request.getFecha(),
                request.getHoraInicio(),
                request.getHoraFin(),
                EstadoReserva.ACTIVA,
                idReservaIgnorada);

        if (horarioOcupado) {
            throw new BusinessException("El horario ya esta ocupado para la cancha seleccionada", HttpStatus.CONFLICT);
        }
    }

    private ReservaResponse toResponse(Reserva reserva) {
        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(reserva.getIdReserva());
        response.setIdUsuario(reserva.getUsuario().getIdUsuario());
        response.setNombreUsuario(reserva.getUsuario().getNombre());
        response.setIdCancha(reserva.getCancha().getIdCancha());
        response.setNombreCancha(reserva.getCancha().getNombre());
        response.setTipoCancha(reserva.getCancha().getTipo());
        response.setFecha(reserva.getFecha());
        response.setHoraInicio(reserva.getHoraInicio());
        response.setHoraFin(reserva.getHoraFin());
        response.setEstado(reserva.getEstado());
        return response;
    }
}

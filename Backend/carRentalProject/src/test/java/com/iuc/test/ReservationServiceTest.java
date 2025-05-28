package com.iuc.test;


import com.iuc.dto.ReservationDTO;
import com.iuc.dto.request.ReservationRequest;
import com.iuc.dto.request.ReservationUpdateRequest;
import com.iuc.entities.Car;
import com.iuc.entities.Reservation;
import com.iuc.entities.User;
import com.iuc.entities.enums.ReservationStatus;
import com.iuc.exception.BadRequestException;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.exception.message.ErrorMessage;
import com.iuc.mapper.ReservationMapper;
import com.iuc.repository.ReservationRepository;
import com.iuc.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReservation_Success() {
        User user = new User();
        Car car = new Car();
        car.setPricePerHour(10.0);

        ReservationRequest request = new ReservationRequest();
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        request.setPickUpTime(now);
        request.setDropOfTime(now.plusHours(2));

        Reservation reservation = new Reservation();
        when(reservationMapper.reservationRequestToReservation(request)).thenReturn(reservation);
        when(reservationRepository.checkCarStatus(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());

        reservationService.createReservation(request, user, car);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testCreateReservation_CarNotAvailable() {
        User user = new User();
        Car car = new Car();
        car.setId(1L);  // ID ataması önemli!
        car.setPricePerHour(10.0);

        ReservationRequest request = new ReservationRequest();
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        request.setPickUpTime(now);
        request.setDropOfTime(now.plusHours(2));

        Reservation reservation = new Reservation();

        when(reservationMapper.reservationRequestToReservation(request)).thenReturn(reservation);

        // Mock: checkCarStatus, carId = 1L, diğer parametreler any()
        when(reservationRepository.checkCarStatus(
                eq(car.getId()),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(ReservationStatus[].class))
        ).thenReturn(List.of(new Reservation())); // Araç müsait değil

        // Artık createReservation çağrıldığında BadRequestException beklenir
        assertThrows(BadRequestException.class, () -> {
            reservationService.createReservation(request, user, car);
        });
    }


    @Test
    void testCheckReservationTimeIsCorrect_InvalidTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusHours(1);

        assertThrows(BadRequestException.class, () -> {
            reservationService.checkReservationTimeIsCorrect(before, now);
        });

        assertThrows(BadRequestException.class, () -> {
            reservationService.checkReservationTimeIsCorrect(now, now);
        });

        assertThrows(BadRequestException.class, () -> {
            reservationService.checkReservationTimeIsCorrect(now.plusHours(1), now);
        });
    }

    @Test
    void testGetById_NotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reservationService.getById(1L));
    }

    @Test
    void testRemoveById_NotFound() {
        when(reservationRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> reservationService.removeById(1L));
    }

    @Test
    void testGetAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(new Reservation()));
        when(reservationMapper.map(any())).thenReturn(List.of(new ReservationDTO()));

        List<ReservationDTO> result = reservationService.getAllReservations();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllWithPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservationPage = new PageImpl<>(List.of(new Reservation()));
        when(reservationRepository.findAll(pageable)).thenReturn(reservationPage);
        when(reservationMapper.reservationToReservationDTO(any())).thenReturn(new ReservationDTO());

        Page<ReservationDTO> result = reservationService.getAllWithPage(pageable);
        assertEquals(1, result.getTotalElements());
    }
}
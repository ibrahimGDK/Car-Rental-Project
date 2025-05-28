package com.iuc.test.controller;

import com.iuc.controller.ReservationController;
import com.iuc.dto.ReservationDTO;
import com.iuc.dto.request.ReservationRequest;
import com.iuc.dto.request.ReservationUpdateRequest;
import com.iuc.dto.response.CarAvailabilityResponse;
import com.iuc.dto.response.SfResponse;
import com.iuc.entities.Car;
import com.iuc.entities.User;
import com.iuc.service.CarService;
import com.iuc.service.ReservationService;
import com.iuc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationService reservationService;

    @Mock
    private CarService carService;

    @Mock
    private UserService userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testMakeReservation() {
        Long carId = 1L;
        ReservationRequest request = new ReservationRequest();
        Car car = new Car();
        User user = new User();

        when(carService.getCarById(carId)).thenReturn(car);
        when(userService.getCurrentUser()).thenReturn(user);

        ResponseEntity<SfResponse> response = reservationController.makeReservation(carId, request);

        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        verify(reservationService).createReservation(request, user, car);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAddReservationAsAdmin() {
        Long carId = 2L;
        Long userId = 1L;
        ReservationRequest request = new ReservationRequest();
        Car car = new Car();
        User user = new User();

        when(carService.getCarById(carId)).thenReturn(car);
        when(userService.getById(userId)).thenReturn(user);

        ResponseEntity<SfResponse> response = reservationController.addReservation(userId, carId, request);

        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        verify(reservationService).createReservation(request, user, car);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllReservations() {
        List<ReservationDTO> reservations = List.of(new ReservationDTO(), new ReservationDTO());

        when(reservationService.getAllReservations()).thenReturn(reservations);

        ResponseEntity<List<ReservationDTO>> response = reservationController.getAllReservations();

        assertEquals(2, response.getBody().size());
        verify(reservationService).getAllReservations();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllReservationsWithPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<ReservationDTO> page = new PageImpl<>(List.of(new ReservationDTO()));

        when(reservationService.getAllWithPage(pageable)).thenReturn(page);

        ResponseEntity<Page<ReservationDTO>> response = reservationController.getAllReservationsWithPage(0, 10, "id", Sort.Direction.DESC);

        assertEquals(1, response.getBody().getContent().size());
        verify(reservationService).getAllWithPage(pageable);
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testCheckCarIsAvailable() {
        Long carId = 1L;
        LocalDateTime pickUp = LocalDateTime.now().plusDays(1);
        LocalDateTime dropOff = pickUp.plusDays(2);
        Car car = new Car();

        when(carService.getCarById(carId)).thenReturn(car);
        when(reservationService.checkCarAvailability(car, pickUp, dropOff)).thenReturn(true);
        when(reservationService.getTotalPrice(car, pickUp, dropOff)).thenReturn(500.0);

        ResponseEntity<SfResponse> response = reservationController.checkCarIsAvailable(carId, pickUp, dropOff);

        CarAvailabilityResponse body = (CarAvailabilityResponse) response.getBody();
        assertTrue(body.isAvailable());
        assertEquals(500.0, body.getTotalPrice());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateReservation() {
        Long carId = 1L;
        Long reservationId = 2L;
        Car car = new Car();
        ReservationUpdateRequest request = new ReservationUpdateRequest();

        when(carService.getCarById(carId)).thenReturn(car);

        ResponseEntity<SfResponse> response = reservationController.updateReservation(carId, reservationId, request);

        assertEquals(200, response.getStatusCodeValue());
        verify(reservationService).updateReservation(reservationId, car, request);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetReservationById() {
        Long id = 3L;
        ReservationDTO dto = new ReservationDTO();
        when(reservationService.getReservationDTO(id)).thenReturn(dto);

        ResponseEntity<ReservationDTO> response = reservationController.getReservationById(id);

        assertEquals(dto, response.getBody());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllUserReservationsForAdmin() {
        User user = new User();
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        Page<ReservationDTO> page = new PageImpl<>(List.of(new ReservationDTO()));

        when(userService.getById(1L)).thenReturn(user);
        when(reservationService.findReservationPageByUser(user, pageable)).thenReturn(page);

        ResponseEntity<Page<ReservationDTO>> response =
                reservationController.getAllUserReservations(1L, 0, 5, "id", Sort.Direction.ASC);

        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testGetUserReservationById() {
        Long id = 1L;
        User user = new User();
        ReservationDTO dto = new ReservationDTO();

        when(userService.getCurrentUser()).thenReturn(user);
        when(reservationService.findByIdAndUser(id, user)).thenReturn(dto);

        ResponseEntity<ReservationDTO> response = reservationController.getUserReservationById(id);

        assertEquals(dto, response.getBody());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testGetAllUserReservations() {
        User user = new User();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<ReservationDTO> page = new PageImpl<>(List.of(new ReservationDTO()));

        when(userService.getCurrentUser()).thenReturn(user);
        when(reservationService.findReservationPageByUser(user, pageable)).thenReturn(page);

        ResponseEntity<Page<ReservationDTO>> response = reservationController.getAllUserReservations(0, 10, "id", Sort.Direction.DESC);

        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteReservation() {
        ResponseEntity<SfResponse> response = reservationController.deleteReservation(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(reservationService).removeById(1L);
    }
}


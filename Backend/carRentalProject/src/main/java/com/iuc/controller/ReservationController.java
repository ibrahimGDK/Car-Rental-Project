package com.iuc.controller;

import com.iuc.dto.ReservationDTO;
import com.iuc.dto.request.ReservationRequest;
import com.iuc.dto.request.ReservationUpdateRequest;
import com.iuc.dto.response.CarAvailabilityResponse;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.dto.response.SfResponse;
import com.iuc.entities.Car;
import com.iuc.entities.User;
import com.iuc.service.CarService;
import com.iuc.service.ReservationService;
import com.iuc.service.UserService;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final CarService carService;
    private final UserService userService;

    public ReservationController(ReservationService reservationService, CarService carService, UserService userService) {
        this.reservationService = reservationService;
        this.carService = carService;
        this.userService = userService;
    }

    // !!! make Reservation
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<SfResponse> makeReservation(@RequestParam("carId") Long carId,
                                                      @Valid @RequestBody ReservationRequest reservationRequest) {

        Car car = carService.getCarById(carId);
        User user = userService.getCurrentUser();

        reservationService.createReservation(reservationRequest, user, car);

        SfResponse response =
                new SfResponse(ResponseMessage.RESERVATION_CREATED_RESPONSE_MESSAGE, true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // !!! AdminMakeReservation
    @PostMapping("/add/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> addReservation(
            @RequestParam("userId") Long userId,
            @RequestParam("carId") Long carId,
            @Valid @RequestBody ReservationRequest reservationRequest) {

        Car car = carService.getCarById(carId);
        User user = userService.getById(userId);

        reservationService.createReservation(reservationRequest, user, car);

        SfResponse response = new SfResponse(
                ResponseMessage.RESERVATION_CREATED_RESPONSE_MESSAGE, true);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // !!! getAllReservations
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> allReservations = reservationService.getAllReservations();
        return ResponseEntity.ok(allReservations);
    }

    // !!! getAllReservationsWithPage
    @GetMapping("/admin/all/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDTO>> getAllReservationsWithPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String prop,//neye göre sıralanacağı belirtiliyor
            @RequestParam(value = "direction",
                    required = false, // direction required olmasın
                    defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page<ReservationDTO> allReservations = reservationService.getAllWithPage(pageable);

        return ResponseEntity.ok(allReservations);
    }
    //@DateTimeFormat -- pickUp time ı istediğimiz formatta gelmesi için kullandık

    // !!! CheckCarIsAvailable
    @GetMapping("/auth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<SfResponse> checkCarIsAvailable(
            @RequestParam("carId") Long carId,
            @RequestParam("pickUpDateTime")
            @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") LocalDateTime pickUpTime,
            @RequestParam("dropOffDateTime")
            @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") LocalDateTime dropOffTime
    ) {

        Car car = carService.getCarById(carId);

        boolean isAvailable = reservationService.checkCarAvailability(car, pickUpTime, dropOffTime);

        Double totalPrice = reservationService.getTotalPrice(car, pickUpTime, dropOffTime);

        SfResponse response = new CarAvailabilityResponse(ResponseMessage.CAR_AVAILABLE_MESSAGE,
                true,
                isAvailable,
                totalPrice);

        return ResponseEntity.ok(response);
    }

    // !!! Update
    @PutMapping("/admin/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> updateReservation(
            @RequestParam("carId") Long carId,
            @RequestParam("reservationId") Long reservationId,
            @Valid @RequestBody ReservationUpdateRequest reservationUpdateRequest) {

        Car car = carService.getCarById(carId);
        reservationService.updateReservation(reservationId, car, reservationUpdateRequest);

        SfResponse response =
                new SfResponse(ResponseMessage.RESERVATION_UPDATED_RESPONSE_MESSAGE, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // !!! getReservationById-ADMIN
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        ReservationDTO reservationDTO = reservationService.getReservationDTO(id);
        return ResponseEntity.ok(reservationDTO);
    }

    // !!! getReservationForSpecificUser-ADMIN
    @GetMapping("/admin/auth/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDTO>> getAllUserReservations(
            @RequestParam("userId") Long userId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String prop,//neye göre sıralanacağı belirtiliyor
            @RequestParam(value = "direction",
                    required = false, // direction required olmasın
                    defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));

        User user = userService.getById(userId);

        Page<ReservationDTO> reservationDTOS = reservationService.findReservationPageByUser(user, pageable);

        return ResponseEntity.ok(reservationDTOS);

    }

    //***********  Customer veya Admin kendine ait olan reservasyon bilgilerini getirsin  ******
    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<ReservationDTO> getUserReservationById(@PathVariable Long id) {
        User user = userService.getCurrentUser();
        ReservationDTO reservationDTO = reservationService.findByIdAndUser(id, user);
        return ResponseEntity.ok(reservationDTO);
    }

    //getAllReservations (sadece kendisine ait olan)
    @GetMapping("/auth/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Page<ReservationDTO>> getAllUserReservations(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String prop,//neye göre sıralanacağı belirtiliyor
            @RequestParam(value = "direction",
                    required = false, // direction required olmasın
                    defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        User user = userService.getCurrentUser();
        Page<ReservationDTO> reservationDTOPage =
                reservationService.findReservationPageByUser(user, pageable);

        return ResponseEntity.ok(reservationDTOPage);
    }
    // !!! DELETE
    @DeleteMapping("/admin/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> deleteReservation(@PathVariable Long id) {
        reservationService.removeById(id);

        SfResponse response =
                new SfResponse(ResponseMessage.RESERVATION_DELETED_RESPONSE_MESSAGE, true);
        return ResponseEntity.ok(response);
    }
}

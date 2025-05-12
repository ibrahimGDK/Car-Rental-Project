package com.iuc.controller;


import com.iuc.dto.CarDTO;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.dto.response.SfResponse;
import com.iuc.service.CarService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }


    // !!! SaveCar
    @PostMapping("/admin/{imageId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> saveCar(
            @PathVariable String imageId, @Valid @RequestBody CarDTO carDTO) {
        carService.saveCar(imageId, carDTO);

        SfResponse response = new SfResponse(
                ResponseMessage.CAR_SAVED_RESPONSE_MESSAGE, true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // !!! getAllCar
    @GetMapping("/visitors/all")
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> allCars = carService.getAllCars();
        return ResponseEntity.ok(allCars);
    }

}

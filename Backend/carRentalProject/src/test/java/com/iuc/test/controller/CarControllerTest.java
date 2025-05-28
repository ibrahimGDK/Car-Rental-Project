package com.iuc.test.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.iuc.controller.CarController;
import com.iuc.dto.CarDTO;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Autowired
    private ObjectMapper objectMapper;

    private CarDTO carDTO;

    @BeforeEach
    void setUp() {
        carDTO = new CarDTO();
        carDTO.setId(1L);
        carDTO.setModel("Toyota");
        carDTO.setDoors(4);
        carDTO.setSeats(5);
        carDTO.setLuggage(2);
        carDTO.setTransmission("Automatic");
        carDTO.setAirConditioning(true);
        carDTO.setFuelType("Gasoline");
        carDTO.setPricePerHour(50.0);
        carDTO.setAge(2022);
    }

    @Test
    void testSaveCar() throws Exception {
        doNothing().when(carService).saveCar(anyString(), any(CarDTO.class));

        mockMvc.perform(post("/car/admin/abc123/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(ResponseMessage.CAR_SAVED_RESPONSE_MESSAGE))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetAllCars() throws Exception {
        List<CarDTO> cars = Arrays.asList(carDTO);
        when(carService.getAllCars()).thenReturn(cars);

        mockMvc.perform(get("/car/visitors/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetAllCarsWithPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "model"));
        Page<CarDTO> carPage = new PageImpl<>(List.of(carDTO));

        when(carService.findAllWithPage(any(Pageable.class))).thenReturn(carPage);

        mockMvc.perform(get("/car/visitors/pages")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "model")
                        .param("direction", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void testGetCarById() throws Exception {
        when(carService.findById(1L)).thenReturn(carDTO);

        mockMvc.perform(get("/car/visitors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Toyota"));
    }

    @Test
    void testUpdateCar() throws Exception {
        doNothing().when(carService).updateCar(eq(1L), anyString(), any(CarDTO.class));

        mockMvc.perform(put("/car/admin/auth")
                        .param("id", "1")
                        .param("imageId", "img123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseMessage.CAR_UPDATE_RESPONSE_MESSAGE))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeleteCar() throws Exception {
        doNothing().when(carService).removeById(1L);

        mockMvc.perform(delete("/car/admin/1/auth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseMessage.CAR_DELETE_RESPONSE_MESSAGE))
                .andExpect(jsonPath("$.success").value(true));
    }
}


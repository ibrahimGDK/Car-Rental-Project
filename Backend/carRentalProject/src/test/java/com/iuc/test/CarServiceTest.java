package com.iuc.test;

import com.iuc.dto.CarDTO;
import com.iuc.entities.Car;
import com.iuc.entities.ImageFile;
import com.iuc.exception.BadRequestException;
import com.iuc.exception.ConflictException;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.exception.message.ErrorMessage;
import com.iuc.mapper.CarMapper;
import com.iuc.repository.CarRepository;
import com.iuc.service.CarService;
import com.iuc.service.ImageFileService;
import com.iuc.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ImageFileService imageFileService;

    @Mock
    private CarMapper carMapper;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private CarService carService;

    private CarDTO carDTO;
    private Car car;
    private ImageFile imageFile;

    @BeforeEach
    void setUp() {
        carDTO = new CarDTO();
        carDTO.setModel("BMW");

        car = new Car();
        car.setId(1L);
        car.setModel("BMW");

        imageFile = new ImageFile();
        imageFile.setId("img123");
    }

    @Test
    void testSaveCar_whenImageAlreadyUsed_thenThrowConflictException() {
        // Gerçek bir imageFile nesnesi yarat ve ID ata
        ImageFile imageFile = new ImageFile();
        imageFile.setId("img123");

        // DTO oluştur, boş olsa da mapper'a ulaşmadığımız için önemli değil
        CarDTO carDTO = new CarDTO();

        // Mock'ları ayarla
        when(imageFileService.findImageById("img123")).thenReturn(imageFile);
        when(carRepository.findCarCountByImageId("img123")).thenReturn(1);

        // Gerçek test
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            carService.saveCar("img123", carDTO);
        });

        // Sabit mesajı kullan
        assertEquals(ErrorMessage.IMAGE_USED_MESSAGE, exception.getMessage());
    }



    @Test
    void testSaveCar_whenValid_thenSaveCar() {
        when(imageFileService.findImageById("img123")).thenReturn(imageFile);
        when(carRepository.findCarCountByImageId("img123")).thenReturn(0);
        when(carMapper.carDTOToCar(carDTO)).thenReturn(car);

        carService.saveCar("img123", carDTO);

        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testGetAllCars_returnsCarDTOList() {
        List<Car> cars = Arrays.asList(car);
        List<CarDTO> carDTOs = Arrays.asList(carDTO);

        when(carRepository.findAll()).thenReturn(cars);
        when(carMapper.map(cars)).thenReturn(carDTOs);

        List<CarDTO> result = carService.getAllCars();

        assertEquals(1, result.size());
        assertEquals("BMW", result.get(0).getModel());
    }

    @Test
    void testUpdateCar_whenCarIsBuiltIn_thenThrowBadRequestException() {
        Long carId = 1L;
        String imageId = "img123";

        Car car = new Car();
        car.setId(carId);
        car.setBuiltIn(true); // builtIn true

        when(carRepository.findCarById(carId)).thenReturn(java.util.Optional.of(car));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            carService.updateCar(carId, imageId, carDTO);
        });

        assertEquals(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE, exception.getMessage());
    }

    @Test
    void testUpdateCar_whenImageUsedByAnotherCar_thenThrowConflictException() {
        Long carId = 1L;
        String imageId = "img123";

        Car car = new Car();
        car.setId(carId);
        car.setBuiltIn(false);

        Car anotherCar = new Car();
        anotherCar.setId(2L); // farklı araç

        when(carRepository.findCarById(carId)).thenReturn(java.util.Optional.of(car));
        when(imageFileService.findImageById(imageId)).thenReturn(imageFile);
        when(carRepository.findCarsByImageId(imageFile.getId())).thenReturn(List.of(anotherCar));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            carService.updateCar(carId, imageId, carDTO);
        });

        assertEquals(ErrorMessage.IMAGE_USED_MESSAGE, exception.getMessage());
    }

    @Test
    void testUpdateCar_successfulUpdate() {
        Long carId = 1L;
        String imageId = "img123";

        Car car = new Car();
        car.setId(carId);
        car.setBuiltIn(false);
        car.setImage(new HashSet<>());

        when(carRepository.findCarById(carId)).thenReturn(java.util.Optional.of(car));
        when(imageFileService.findImageById(imageId)).thenReturn(imageFile);
        when(carRepository.findCarsByImageId(imageFile.getId())).thenReturn(List.of(car));

        carService.updateCar(carId, imageId, carDTO);

        verify(carRepository).save(car);

        assertTrue(car.getImage().contains(imageFile));
        // İstersen car içindeki diğer alanların güncellenip güncellenmediğini de kontrol edebilirsin:
        assertEquals(carDTO.getAge(), car.getAge());
        assertEquals(carDTO.getAirConditioning(), car.getAirConditioning());
    }

    @Test
    void testFindById_whenCarExists_thenReturnCarDTO() {
        when(carRepository.findCarById(1L)).thenReturn(Optional.of(car));
        when(carMapper.carToCarDTO(car)).thenReturn(carDTO);

        CarDTO result = carService.findById(1L);

        assertNotNull(result);
        assertEquals("BMW", result.getModel());
    }

    @Test
    void testFindById_whenCarNotExists_thenThrowException() {
        when(carRepository.findCarById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            carService.findById(1L);
        });
    }

    @Test
    void testRemoveById_whenBuiltIn_thenThrowException() {
        car.setBuiltIn(true);
        when(carRepository.findCarById(1L)).thenReturn(Optional.of(car));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            carService.removeById(1L);
        });

        assertEquals(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE, exception.getMessage());
    }

    @Test
    void testRemoveById_whenCarUsedInReservation_thenThrowException() {
        car.setBuiltIn(false);
        when(carRepository.findCarById(1L)).thenReturn(Optional.of(car));
        when(reservationService.existByCar(car)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            carService.removeById(1L);
        });

        assertEquals(ErrorMessage.CAR_USED_BY_RESERVATION_MESSAGE, exception.getMessage());
    }

    @Test
    void testRemoveById_success() {
        car.setBuiltIn(false);
        when(carRepository.findCarById(1L)).thenReturn(Optional.of(car));
        when(reservationService.existByCar(car)).thenReturn(false);

        carService.removeById(1L);

        verify(carRepository).delete(car);
    }

    @Test
    void testGetAllCar_returnsCarList() {
        List<Car> carList = List.of(car);
        when(carRepository.getAllBy()).thenReturn(carList);

        List<Car> result = carService.getAllCar();

        assertEquals(1, result.size());
        assertEquals("BMW", result.get(0).getModel());
    }



}

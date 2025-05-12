package com.iuc.service;

import com.iuc.dto.CarDTO;
import com.iuc.entities.Car;
import com.iuc.entities.ImageFile;
import com.iuc.exception.ConflictException;
import com.iuc.exception.message.ErrorMessage;
import com.iuc.mapper.CarMapper;
import com.iuc.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final ImageFileService imageFileService;
    private final CarMapper carMapper;
    private final ReservationService reservationService;

    public CarService(CarRepository carRepository, ImageFileService imageFileService, CarMapper carMapper, ReservationService reservationService) {
        this.carRepository = carRepository;
        this.imageFileService = imageFileService;
        this.carMapper = carMapper;
        this.reservationService = reservationService;
    }

    public void saveCar(String imageId, CarDTO carDTO) {
        //!!! image Id , Repo da var mi ??
        ImageFile imageFile = imageFileService.findImageById(imageId);
        //!!! imageId daha once baska bir arac icin kullanildi mi ???
        Integer usedCarCount = carRepository.findCarCountByImageId(imageFile.getId());
        if(usedCarCount>0) {
            throw new ConflictException(ErrorMessage.IMAGE_USED_MESSAGE);
        }
        //!!! mapper islemi
        Car car = carMapper.carDTOToCar(carDTO);
        //!!! image bilgisini Car a ekliyoruz
        Set<ImageFile> imFiles = new HashSet<>();
        imFiles.add(imageFile);
        car.setImage(imFiles);
        carRepository.save(car);

    }

    public List<CarDTO> getAllCars() {
        List<Car> carList = carRepository.findAll();
        //!!! CarMapper
        return carMapper.map(carList);
    }

}

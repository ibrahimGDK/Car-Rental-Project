package com.iuc.service;

import com.iuc.dto.CarDTO;
import com.iuc.entities.Car;
import com.iuc.entities.ImageFile;
import com.iuc.exception.BadRequestException;
import com.iuc.exception.ConflictException;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.exception.message.ErrorMessage;
import com.iuc.mapper.CarMapper;
import com.iuc.repository.CarRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<CarDTO> findAllWithPage(Pageable pageable) {

        Page<Car> carPage =carRepository.findAll(pageable);
        return carPage.map(car-> carMapper.carToCarDTO(car));
    }

    public CarDTO findById(Long id) {

        Car car = getCar(id);

        return carMapper.carToCarDTO(car);
    }

    //!!! yardımcı metod
    private Car getCar(Long id){
        Car car = carRepository.findCarById(id).orElseThrow(()->
                new ResourceNotFoundException(
                        String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION, id)));
        return car;
    }

    public void updateCar(Long id, String imageId, CarDTO carDTO) {//imageId: yeni yükleencek fotoğrafın id'si
        Car car = getCar(id);

        // !!! builtIn ???
        if(car.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        // !!! verilen image daha önce başka araç için kullanılmış mı ???
        //Bu imageId upload edilmiş ancak başka bir arabada kullanılmamış image  olmalı
        ImageFile imageFile =  imageFileService.findImageById(imageId);

        List<Car> carList = carRepository.findCarsByImageId(imageFile.getId()); // BU PARAMETREYE ARGÜMAN OLARAK DOĞRUDAN imageId YAZAMAZ MIYDIK
        for (Car c : carList) {
            // Long --> long ||  carların id leri Long tipinde non-primitive'dir. Bunların eşitliğini karşılaştırmak zordur. Fieldları aynı olsa bile farklı objeler. Bu nedenle longValue() metodu ile primitive (long) yapılır
            if(car.getId().longValue()!= c.getId().longValue()){
                throw  new ConflictException(ErrorMessage.IMAGE_USED_MESSAGE);
            }
        }
        car.setAge(carDTO.getAge());
        car.setAirConditioning(carDTO.getAirConditioning());
        car.setBuiltIn(carDTO.getBuiltIn());
        car.setDoors(carDTO.getDoors());
        car.setFuelType(carDTO.getFuelType());
        car.setLuggage(carDTO.getLuggage());
        car.setModel(carDTO.getModel());
        car.setPricePerHour(carDTO.getPricePerHour());
        car.setSeats(carDTO.getSeats());
        car.setTransmission(carDTO.getTransmission());

        car.getImage().add(imageFile);

        carRepository.save(car);
    }

    public void removeById(Long id) {
        Car car = getCar(id);

        // !!! builtIn ???
        if(car.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        //rezervasyon kontrolü
        boolean exist =  reservationService.existByCar(car);
        if (exist){
            throw new BadRequestException(ErrorMessage.CAR_USED_BY_RESERVATION_MESSAGE);
        }


        carRepository.delete(car);
    }
    public Car getCarById(Long carId) {

        Car car =  carRepository.findById(carId).orElseThrow(()->
                new ResourceNotFoundException(
                        String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION, carId)));
        return car ;
    }

    public List<Car> getAllCar() {
        return carRepository.getAllBy();
    }

}

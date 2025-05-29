package com.iuc.mapper;

import com.iuc.dto.CarDTO;
import com.iuc.entities.Car;
import com.iuc.entities.ImageFile;
import org.mapstruct.*;
//import org.mapstruct.Mapping;
//import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")//spring iiçinde mapStruck kullanılabilsim
public interface CarMapper {

    //CarDTO--> Car
    @Mapping(target = "image", ignore = true)//Bu metotda neden image fieldını ignore etttik. Buradaki dönüşümde image gieldı işe yaramıyorda mı ignore ettik
    Car carDTOToCar(CarDTO carDTO);


    // List.CarDTO--> List.Car
    //@Mapping burada yazmayua gerek yok. Aşağıda yazdığımız için burada otomatik yaptı. Aşağıdaki metodu yazmasaydık burada @Mapping yapmamız gerekecekti
    //Çoğulu nasıl mapleyeceğini tekile bakarak yapacak
    List<CarDTO> map(List<Car> cars);


    //!!! Car -> CarDTO
    @Mapping(source = "image", target ="image", qualifiedByName = "getImageAsString")
    CarDTO carToCarDTO(Car car);
    //qualifiedByName ile  @Named("getImageAsString") aynı olmalı
    @Named("getImageAsString")
    public static Set<String> getImageIds(Set<ImageFile> imageFiles) {
        Set<String> imgs = new HashSet<>();
        imgs = imageFiles.stream().
                map(imFile -> imFile.getId().toString()).
                collect(Collectors.toSet());
        return imgs;

    }

}
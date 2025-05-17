package com.iuc.mapper;

import com.iuc.dto.ReservationDTO;
import com.iuc.dto.request.ReservationRequest;
import com.iuc.entities.ImageFile;
import com.iuc.entities.Reservation;
import com.iuc.entities.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.*;
import java.util.stream.*;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    Reservation reservationRequestToReservation(ReservationRequest reservationRequest);

    @Mapping(source="car.image", target="car.image", qualifiedByName = "getImageAsString") // car.image anlamadım
    @Mapping(source="user", target="userId", qualifiedByName = "getUserId")
    ReservationDTO reservationToReservationDTO(Reservation reservation);

    List<ReservationDTO> map(List<Reservation> reservationList);

    @Named("getImageAsString")
    public static Set<String> getImageIds(Set<ImageFile> imageFiles) {
        Set<String> imgs = new HashSet<>();

        imgs = imageFiles.stream().map(imFile->imFile.getId().toString()).
                collect(Collectors.toSet());
        return imgs;
    }

    @Named("getUserId")
    public static Long getUserId(User user) {
        return user.getId();
    }
}

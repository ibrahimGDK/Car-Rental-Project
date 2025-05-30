package com.iuc.dto.request;



import com.fasterxml.jackson.annotation.*;
import com.iuc.entities.enums.ReservationStatus;
import lombok.*;
import jakarta.validation.constraints.*;
import java.time.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationUpdateRequest {

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    @NotNull(message="Please provide the pick up time of the reservation")
    private LocalDateTime pickUpTime;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    @NotNull(message="Please provide the drop off time of the reservation")
    private LocalDateTime dropOfTime;

    @Size(max=150, message ="Pick up location must be max 150 chars")
    @NotBlank(message="Please provide the pick up location of the reservation")
    private String pickUpLocation;

    @Size(max=150, message ="Drop of location must be max 150 chars")
    @NotBlank(message="Please provide the drop of location of the reservation")
    private String dropOfLocation;

    private ReservationStatus status;
}

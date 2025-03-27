package com.iuc.entities;

import com.iuc.entities.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
// !!! Reservation tablosundaki car_id ile Car Tablosundaki id header eşleştirdim
    @JoinColumn(name="car_id", referencedColumnName = "id")
    private Car car;

    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime pickUpTime; // kiralama başlangıç zamanını

    @Column(nullable = false)
    private LocalDateTime dropOfTime; // kiralama bitiş zamanını

    @Column(length = 150, nullable = false)
    private String pickUpLocation; // Aracın nereden alınacağını belirtir.

    @Column(length = 150, nullable = false)
    private String dropOfLocation; // Aracın nereden alınacağını belirtir.

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private Double totalPrice;
}

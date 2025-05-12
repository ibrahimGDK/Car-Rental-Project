package com.iuc.repository;

import com.iuc.entities.Car;
import com.iuc.entities.Reservation;
import com.iuc.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    boolean existsByUser(User user);
    boolean existsByCar(Car car) ;
}

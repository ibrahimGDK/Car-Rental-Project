package com.iuc.repository;

import com.iuc.entities.Car;
import com.iuc.entities.Reservation;
import com.iuc.entities.User;
import com.iuc.entities.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    /*@Query("SELECT r FROM Reservation r " +
            "JOIN FETCH Car c on r.car=c.id WHERE " + //r.car=c.id --> rezervasyondaki car objesini, car objesinin id'siyle maple
            "c.id=:carId and (r.status not in :status) and :pickUpTime BETWEEN r.pickUpTime and r.dropOfTime " +
            "or " +
            "c.id=:carId and (r.status not in :status) and :dropOfTime BETWEEN r.pickUpTime and r.dropOfTime " +
            "or " +
            "c.id=:carId and (r.status not in :status) and (r.pickUpTime BETWEEN :pickUpTime and :dropOfTime)")
    List<Reservation> checkCarStatus(@Param("carId") Long carId,
                                     @Param("pickUpTime") LocalDateTime pickUpTime,
                                     @Param("dropOfTime") LocalDateTime dropOfTime,
                                     @Param("status") ReservationStatus[] status);*/

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.car c " +
            "WHERE c.id = :carId AND r.status NOT IN :status AND (" +
            "(:pickUpTime BETWEEN r.pickUpTime AND r.dropOfTime) OR " +
            "(:dropOfTime BETWEEN r.pickUpTime AND r.dropOfTime) OR " +
            "(r.pickUpTime BETWEEN :pickUpTime AND :dropOfTime))")
    List<Reservation> checkCarStatus(@Param("carId") Long carId,
                                     @Param("pickUpTime") LocalDateTime pickUpTime,
                                     @Param("dropOfTime") LocalDateTime dropOfTime,
                                     @Param("status") ReservationStatus[] status);

    @EntityGraph(attributePaths = {"car","car.image"})//nereye kadar eager çalışacağını söylüyorum
    List<Reservation> findAll();

    @EntityGraph(attributePaths = {"car","car.image"})
    Page<Reservation> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.image","user"})
    Optional<Reservation> findById(Long id);


    @EntityGraph(attributePaths = {"car", "car.image","user"})
    Page<Reservation> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.image","user"})
    Optional<Reservation> findByIdAndUser(Long id, User user);//2 parametreli metodu JAVA kendisi türetti


    boolean existsByUser(User user);
    boolean existsByCar(Car car) ;
    @EntityGraph(attributePaths = {"car","user"})
    List<Reservation> findAllBy();
}

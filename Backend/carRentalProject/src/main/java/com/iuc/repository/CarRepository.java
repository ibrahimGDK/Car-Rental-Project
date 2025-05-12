package com.iuc.repository;


import com.iuc.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    // !!! JPQL
    @Query("SELECT count(*) from Car c join c.image img where img.id=:id")
    Integer findCarCountByImageId(@Param("id") String id);

    @EntityGraph(attributePaths = {"image"})//!!! EAGER yaptik
    List<Car> findAll();

    @EntityGraph(attributePaths = {"image"})//!!EAGER yaptik
    Page<Car> findAll(Pageable pageable);
}

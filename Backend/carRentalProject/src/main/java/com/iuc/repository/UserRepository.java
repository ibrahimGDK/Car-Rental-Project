package com.iuc.repository;

import com.iuc.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "roles") //Defaultta Lazy olan Role bilgilerini EAGER yaptık. sadece bu metot çalıştığında eager olur
    Optional<User> findByEmail(String email); // servis katında türetilmek için optinal yaptık

    @EntityGraph(attributePaths = "roles")// findAll() sorgusu çağırlıdğında user entity sinin içindeki roles verisinide getirir yani EAGLE olur
    List<User> findAll();

    @EntityGraph(attributePaths = "roles")
    Page<User> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findById(Long id);

    @Modifying// JpaRepository içinde custom bir query ile DML operasyonları yapılıyor ise @Modifying yazılır
    @Query("UPDATE User u SET u.firstName=:firstName, u.lastName=:lastName,u.phoneNumber=:phoneNumber,u.email=:email,u.address=:address,u.zipCode=:zipCode WHERE u.id=:id")
    void update(@Param("id") Long id,
                @Param("firstName") String firstName,
                @Param("lastName") String lastName,
                @Param("phoneNumber") String phoneNumber,
                @Param("email") String email,
                @Param("address") String address,
                @Param("zipCode") String zipCode);
    //@Param: Bu annotation, method parametrelerini JPQL sorgusunda kullanılan parametrelerle eşleştirir.


    @EntityGraph(attributePaths = "id")//bana Rolleri getirme
    Optional<User> findUserById(Long id);
}

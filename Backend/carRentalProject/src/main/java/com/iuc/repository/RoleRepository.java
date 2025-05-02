package com.iuc.repository;

import com.iuc.entities.Role;
import com.iuc.entities.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {

    Optional<Role> findByType(RoleType type);
}
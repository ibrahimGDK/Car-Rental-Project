package com.iuc.service;

import com.iuc.entities.Role;
import com.iuc.entities.enums.RoleType;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.exception.message.ErrorMessage;
import com.iuc.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findByType(RoleType roleType){
        Role role =  roleRepository.findByType(roleType).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.ROLE_NOT_FOUND_EXCEPTION, roleType.name())));
        return role;
    }

}

package com.iuc.test.service;


import com.iuc.entities.Role;
import com.iuc.entities.enums.RoleType;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.repository.RoleRepository;
import com.iuc.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void testFindByType_whenRoleExists_shouldReturnRole() {
        // arrange
        RoleType roleType = RoleType.ROLE_ADMIN;
        Role expectedRole = new Role();
        expectedRole.setId(1);
        expectedRole.setType(roleType);

        when(roleRepository.findByType(roleType)).thenReturn(Optional.of(expectedRole));

        // act
        Role result = roleService.findByType(roleType);

        // assert
        assertNotNull(result);
        assertEquals(roleType, result.getType());
        assertEquals(1, result.getId());
        verify(roleRepository, times(1)).findByType(roleType);
    }

    @Test
    void testFindByType_whenRoleNotFound_shouldThrowException() {
        // arrange
        RoleType roleType = RoleType.ROLE_CUSTOMER;

        when(roleRepository.findByType(roleType)).thenReturn(Optional.empty());

        // act & assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.findByType(roleType);
        });

        // exception mesajı enum'un adını içeriyor, .name() ile eşleşir (yani ROLE_CUSTOMER)
        assertTrue(exception.getMessage().contains(roleType.name()));
        verify(roleRepository, times(1)).findByType(roleType);
    }
}
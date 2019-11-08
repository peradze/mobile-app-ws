package com.appdevelope.mobileappws.service.impl;

import com.appdevelope.mobileappws.io.entity.UserEntity;
import com.appdevelope.mobileappws.io.repositories.UserRepository;
import com.appdevelope.mobileappws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Jon");
        userEntity.setUserId("asdfasd23412");
        userEntity.setEncryptedPassword("342safasdsf");

        when(userRepository.findByEmail( anyString() )).thenReturn(userEntity);

        UserDto userDto = userService.getUser("test@test.com");

        assertNotNull(userDto);
        assertEquals("Jon", userDto.getFirstName());
    }

    @Test
    final void testGetUser_UsernameNotFoundException() {
        when(userRepository.findByEmail( anyString() )).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser("test@test.com"));
    }
}
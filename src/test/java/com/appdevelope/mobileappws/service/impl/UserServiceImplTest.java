package com.appdevelope.mobileappws.service.impl;

import com.appdevelope.mobileappws.io.entity.UserEntity;
import com.appdevelope.mobileappws.io.repositories.UserRepository;
import com.appdevelope.mobileappws.shared.Utils;
import com.appdevelope.mobileappws.shared.dto.AddressDto;
import com.appdevelope.mobileappws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private String userId = "asdfasd23412";
    private String encryptedPassword = "342safasdsf";

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Jon");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationToken("asdf324234sf");
    }

    @Test
    void getUser() {
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

    @Test
    final void testCreateUser() {
        when(userRepository.findByEmail( anyString() )).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("asdfasdfas12312");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(addressDto);

        UserDto userDto = new UserDto();
        userDto.setAddresses(addresses);

        UserDto storedUserDetails = userService.createUser(userDto);

        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
    }
}
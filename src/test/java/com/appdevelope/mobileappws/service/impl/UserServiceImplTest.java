package com.appdevelope.mobileappws.service.impl;

import com.appdevelope.mobileappws.io.entity.AddressEntity;
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
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        userEntity.setLastName("Doe");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationToken("asdf324234sf");
        userEntity.setAddresses(getAddressesEntity());
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

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressDto());
        userDto.setFirstName("Jon");
        userDto.setLastName("Doe");
        userDto.setPassword("12345567");
        userDto.setEmail("test@test.com");

        UserDto storedUserDetails = userService.createUser(userDto);
        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils, times(2)).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode("12345567");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    private List<AddressDto> getAddressDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");
        addressDto.setCity("Vancouver");
        addressDto.setCountry("Canada");
        addressDto.setPostalCode("ABC123");
        addressDto.setStreetName("123 Street name");

        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Vancouver");
        billingAddressDto.setCountry("Canada");
        billingAddressDto.setPostalCode("ABC123");
        billingAddressDto.setStreetName("123 Street name");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(addressDto);
        addresses.add(billingAddressDto);
        return addresses;
    }

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDto> addresses = getAddressDto();

        Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
        return new ModelMapper().map(addresses, listType);
    }

    @Test
    final void testCreateUser_RuntimeException() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        assertThrows(RuntimeException.class, () -> userService.createUser(new UserDto()));
    }
}
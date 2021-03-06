package com.appdevelope.mobileappws.service.impl;

import com.appdevelope.mobileappws.io.entity.AddressEntity;
import com.appdevelope.mobileappws.io.entity.UserEntity;
import com.appdevelope.mobileappws.io.repositories.AddressRepository;
import com.appdevelope.mobileappws.io.repositories.UserRepository;
import com.appdevelope.mobileappws.service.AddressService;
import com.appdevelope.mobileappws.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String useId) {
        List<AddressDto> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = userRepository.findByUserId(useId);
        if (userEntity == null) return returnValue;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity addressEntity: addresses) {
            returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
        }

        return returnValue;
    }

    @Override
    public AddressDto getAddress(String userId, String addressId) {
        AddressDto returnValue = new AddressDto();
        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) return returnValue;

        AddressEntity addressEntity = addressRepository.findByUserDetailsAndAddressId(userEntity, addressId);
        returnValue = modelMapper.map(addressEntity, AddressDto.class);

        return returnValue;
    }
}

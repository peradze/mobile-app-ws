package com.appdevelope.mobileappws.service;

import com.appdevelope.mobileappws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String userId, String addressId);
}

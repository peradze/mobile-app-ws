package com.appdevelope.mobileappws.io.repositories;

import com.appdevelope.mobileappws.io.entity.AddressEntity;
import com.appdevelope.mobileappws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
    AddressEntity findByUserDetailsAndAddressId(UserEntity userEntity, String addressId);
}

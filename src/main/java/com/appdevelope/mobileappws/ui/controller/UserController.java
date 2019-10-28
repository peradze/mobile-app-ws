package com.appdevelope.mobileappws.ui.controller;

import com.appdevelope.mobileappws.service.AddressService;
import com.appdevelope.mobileappws.service.UserService;
import com.appdevelope.mobileappws.shared.dto.AddressDto;
import com.appdevelope.mobileappws.shared.dto.UserDto;
import com.appdevelope.mobileappws.ui.model.request.UserDetailRequestModel;
import com.appdevelope.mobileappws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;

    @GetMapping(path = "/{id}",
        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public UserRest getUser(@PathVariable String id)
    {
        UserDto userDto = userService.getUserByUserId(id);
//        BeanUtils.copyProperties(userDto, returnValue);
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(userDto, UserRest.class);
    }

    @PostMapping(
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
            )
    public UserRest createUser(@RequestBody UserDetailRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");

//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(userDetails, userDto);

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
//        BeanUtils.copyProperties(createdUser, returnValue);
        returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @PutMapping(
            path = "/{id}",
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public UserRest updateUser(@RequestBody UserDetailRequestModel userDetails, @PathVariable String id) {
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(userDto, id);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(
            path = "/{id}",
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        userService.deleteUser(id);

        return returnValue;
    }

    @GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users =  userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }

    @GetMapping(path = "/{id}/addresses",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
    public Resources<AddressesRest> getUserAddresses(@PathVariable String id) {
        List<AddressesRest> addressesListRestModel = new ArrayList<>();

        List<AddressDto> addressDto = addressService.getAddresses(id);
        if (addressDto != null && !addressDto.isEmpty()) {
            ModelMapper modelMapper = new ModelMapper();

            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            addressesListRestModel = modelMapper.map(addressDto, listType);

            for (AddressesRest addressesRest: addressesListRestModel) {
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressesRest.getAddressId())).withSelfRel();
                addressesRest.add(addressLink);

                Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
                addressesRest.add(userLink);
            }
        }

        return new Resources<>(addressesListRestModel) ;
    }
    @GetMapping(path = "/{useId}/addresses/{addressId}",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"  })
    public Resource<AddressesRest>  getUserAddress(@PathVariable String useId, @PathVariable String addressId) {
        AddressesRest returnValue = new AddressesRest();

        AddressDto addressDto = addressService.getAddress(useId, addressId);

        if (addressDto != null) {
            ModelMapper modelMapper = new ModelMapper();
            returnValue = modelMapper.map(addressDto, AddressesRest.class);
            Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(useId, addressId)).withSelfRel();
            Link userLink = linkTo(methodOn(UserController.class).getUser(useId)).withRel("user");
            Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(useId)).withRel("addresses");

            returnValue.add(addressLink);
            returnValue.add(userLink);
            returnValue.add(addressesLink);
        }

        return new Resource<>(returnValue);
    }

    @GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE })
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            returnValue.setOperationResult(RequestOperationName.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationName.ERROR.name());
        }

        return returnValue;
    }
}

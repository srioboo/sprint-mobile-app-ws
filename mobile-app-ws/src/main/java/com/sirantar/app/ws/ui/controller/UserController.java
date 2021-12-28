package com.sirantar.app.ws.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sirantar.app.ws.exceptions.UserServiceException;
import com.sirantar.app.ws.service.AddressService;
import com.sirantar.app.ws.service.UserService;
import com.sirantar.app.ws.shared.dto.AddressDto;
import com.sirantar.app.ws.shared.dto.UserDto;
import com.sirantar.app.ws.ui.model.request.UserDetailsRequestModel;
import com.sirantar.app.ws.ui.model.response.AddressesRest;
import com.sirantar.app.ws.ui.model.response.ErrorMessages;
import com.sirantar.app.ws.ui.model.response.OperationStatusModel;
import com.sirantar.app.ws.ui.model.response.RequestOperationStatus;
import com.sirantar.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users") // http://localhost:8080/users -> http://localhost:8080/mobile-app-ws/users
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	AddressService addressService;

	@Autowired
	AddressService addressesServices;

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }) // /users/{id}
	public UserRest getUser(@PathVariable String id) {

		// UserRest returnValue = new UserRest();
		UserDto userDto = userService.getUserByUserId(id);

		ModelMapper modelMapper = new ModelMapper();
		UserRest returnValue = modelMapper.map(userDto, UserRest.class);
		// BeanUtils.copyProperties(userDto, returnValue);

		return returnValue;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		// usando http://modelmapper.org/getting-started/

		// UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		// UserDto userDto = new UserDto();
		// BeanUtils.copyProperties(userDetails, userDto);

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		// BeanUtils.copyProperties(createdUser, returnValue);
		UserRest returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto updateUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updateUser, returnValue);

		return returnValue;
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {

		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperatioName.DELETE.name());

		userService.deleteUser(id);

		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnValue = new ArrayList<>();

		List<UserDto> users = userService.getUsers(page, limit);

		for (UserDto userDto : users) {
			// UserRest userModel = new UserRest();
			// BeanUtils.copyProperties(userDto, userModel);
			ModelMapper modelMapper = new ModelMapper();
			UserRest userModel = modelMapper.map(userDto, UserRest.class);

			returnValue.add(userModel);
		}

		return returnValue;
	}

	// http://localhost:8080/mobile-app-ws/users/{id}/addresses
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }) // /users/{id}
	public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {

		List<AddressesRest> returnValue = new ArrayList<>();

		List<AddressDto> addressesDto = addressesServices.getAddresses(id);

		if (addressesDto != null && !addressesDto.isEmpty()) {
			java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {
			}.getType();
			returnValue = new ModelMapper().map(addressesDto, listType);

			for (AddressesRest addressesRest : returnValue) {
				Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
						.getUserAddress(id, addressesRest.getAddressId())).withSelfRel();
				addressesRest.add(selfLink);
			}
		}

		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
				.withSelfRel();

		return CollectionModel.of(returnValue, userLink, selfLink);
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }) // /users/{id}
	public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDto addressesDto = addressService.getAddress(addressId);

		ModelMapper modelMapper = new ModelMapper();
		AddressesRest returnValue = modelMapper.map(addressesDto, AddressesRest.class);

		// http://localhost:8080/users/<userId>/addresses
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
		Link userAddressesLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
				// .slash(userId)
				// .slash("addresses")
				.withRel("addresses");
		Link selfLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
				// .slash(userId)
				// .slash("addresses")
				// .slash(addressId)
				.withSelfRel();

		// esto ya no sería necesario
		// returnValue.add(userLink);
		// returnValue.add(userAddressesLink);
		// returnValue.add(selfLink);

		return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
	}

	/**
	 * http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsdf
	 *
	 * @param token
	 * @return
	 */
	@GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperatioName.VERIFY_EMAIL.name());

		boolean isVerified = userService.verifyEmailToken(token);

		if (isVerified) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}

		return returnValue;

	}
}

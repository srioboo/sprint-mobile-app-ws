package com.sirantar.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sirantar.app.ws.service.impl.UserServiceImpl;
import com.sirantar.app.ws.shared.dto.AddressDto;
import com.sirantar.app.ws.shared.dto.UserDto;
import com.sirantar.app.ws.ui.model.response.UserRest;

class UserControllerTest {

	@InjectMocks
	UserController userController;

	@Mock
	UserServiceImpl userService;

	UserDto userDto;

	final String USER_ID = "basfasgasdfa3424";

	@BeforeEach
	void setUp() throws Exception {
		// @deprecated MockitoAnnotations.initMocks(this);
		MockitoAnnotations.openMocks(this);

		userDto = new UserDto();
		userDto.setFirstName("Salva");
		userDto.setLastName("Rioboo");
		userDto.setEmail("test@test.com");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressDto());
		userDto.setEncryptedPassword("xcf58thgh47");

	}

	@Test
	void testGetUser() {
		// fail("Not yet implemented");
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);

		UserRest userRest = userController.getUser(USER_ID);

		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
	}

	private List<AddressDto> getAddressDto() {
		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");
		addressDto.setCity("Malaga");
		addressDto.setCountry("Spain");
		addressDto.setPostalCode("29003");
		addressDto.setStreetName("123 Street name");

		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("Malaga");
		billingAddressDto.setCountry("Spain");
		billingAddressDto.setPostalCode("29003");
		billingAddressDto.setStreetName("123 Street name");

		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);

		return addresses;
	}

}

package com.sirantar.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sirantar.app.ws.io.entity.UserEntity;
import com.sirantar.app.ws.io.repositories.UserRepository;
import com.sirantar.app.ws.shared.Utils;
import com.sirantar.app.ws.shared.dto.AddressDto;
import com.sirantar.app.ws.shared.dto.UserDto;

class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userService;

	@Mock
	UserRepository userRepository;

	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;

	String userId = "hhty57ehfy";
	String encryptedPassword = "74hghd8474jf";

	UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		// MockitoAnnotations.initMocks(this); // initMocks is deprecated
		MockitoAnnotations.openMocks(this);

		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("salva");
		userEntity.setUserId(userId);
		userEntity.setEncrypedPassword(encryptedPassword);
		userEntity.setEmail("test@test.com");
		userEntity.setEmailVerificationToken("sdsf56");
	}

	@Test
	void testGetUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		UserDto userDto = userService.getUser("test@test.com");

		assertNotNull(userDto);
		assertEquals("salva", userDto.getFirstName());
	}

	@Test
	final void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getUser("test@test.com");
		});
	}

	@Test
	final void testCreateUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("asdfasf");
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

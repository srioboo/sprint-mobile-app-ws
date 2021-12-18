package com.sirantar.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sirantar.app.ws.io.entity.UserEntity;
import com.sirantar.app.ws.io.repositories.UserRepository;
import com.sirantar.app.ws.shared.Utils;
import com.sirantar.app.ws.shared.dto.UserDto;

class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	Utils Utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// TODO create PasswordResetTokenRepository
	//@Autowired
	//PasswordResetTokenRepository passwordResetTokeRepository;
	
	@BeforeEach
	void setUp() throws Exception {
		// MockitoAnnotations.initMocks(this); // initMocks is deprecated
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetUser() {
		
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("salva");
		userEntity.setUserId("htty57ehfy");
		userEntity.setEncrypedPassword("74hghd8474jf");
		
		when(userRepository.findByEmail( anyString() ) ).thenReturn(userEntity);
		
		UserDto userDto = userService.getUser("test@test.com");
		
		assertNotNull(userDto);
		assertEquals("salva", userDto.getFirstName());
	}

	@Test
	final void testGetUser_UsernameNotFoundException(){
		when(userRepository.findByEmail( anyString())).thenReturn(null);
		
		assertThrows(UsernameNotFoundException.class, 
				()-> {
					userService.getUser("test@test.com");
				}
		);
	}
	
}

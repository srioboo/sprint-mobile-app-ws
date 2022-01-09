package com.sirantar.app.ws.shared;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {
		String userId = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);

		assertNotNull(userId);
		assertNotNull(userId2);

		assertTrue(userId.length() == 30);
		assertTrue(!userId.equalsIgnoreCase(userId2));
	}

	@Test
	// @Disabled if you want avoid this test
	void testHasTokenNotExpired() {
		// fail("Not yet implemented");

		// String token = ""; // need a token valid
		String token = utils.generateEmailVerificationToken("4yr65hhyid84");
		assertNotNull(token);

		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		assertFalse(hasTokenExpired);
	}

	@Test
	@Disabled
	void testHasTokenExpired() {

		String expiredToken = ""; // hardcoded token expired

		boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);

		assertTrue(hasTokenExpired);
	}

}

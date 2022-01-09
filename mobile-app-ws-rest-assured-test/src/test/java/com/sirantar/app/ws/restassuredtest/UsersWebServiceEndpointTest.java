package com.sirantar.app.ws.restassuredtest;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UsersWebServiceEndpointTest {

  private final String CONTEXT_PAHT  = "/mobile-app-ws";
  private final String EMAIL_ADDRESS = "test@test.com";
  private final String JSON          = "application/json";

  private static String authorizationHeader;
  private static String userId;

  private static List<Map<String, String>> addresses;

  @BeforeEach
  void setUp() throws Exception {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port    = 8080;
  }

  /*
   * testUserLogin()
   *
   */
  @Test
  final void a() {
    Map<String, String> loginDetails = new HashMap<>();
    loginDetails.put("email", EMAIL_ADDRESS);
    loginDetails.put("password", "123");

    Response response = given().contentType(JSON).accept(JSON).body(loginDetails).when().post(CONTEXT_PAHT
      + "/users/login").then().statusCode(200).extract().response();

    authorizationHeader = response.header("Authorization");
    userId              = response.header("UserID");

    assertNotNull(authorizationHeader);
    assertNotNull("userId");

  }

  /*
   * testGetUserDetails()
   *
   */
  @Test
  final void b() {
    Response response = given()
      .pathParam("id", userId)
      .header("Authorization", authorizationHeader)
      .accept(JSON)
      .when()
      .get(CONTEXT_PAHT + "/users/{id}")
      .then()
      .statusCode(200)
      .contentType(JSON)
      .extract()
      .response();

    String userPublicId = response.jsonPath().getString("userId");
    String userEmail    = response.jsonPath().getString("email");
    String firsName     = response.jsonPath().getString("firsName");
    String lastName     = response.jsonPath().getString("lastName");

    addresses = response.jsonPath().getList("addresses");

    String addressId = addresses.get(0).get("addressId");

    assertNotNull(userPublicId);
    assertNotNull(userEmail);
    assertNotNull(firsName);
    assertNotNull(lastName);
    assertEquals(EMAIL_ADDRESS, userEmail);

    assertTrue(addresses.size() == 2);
    assertTrue(addressId.length() == 30);

  }

  /*
   * Test Update User Details
   */
  @Test
  final void c() {

    Map<String, Object> userDetails = new HashMap<>();
    userDetails.put("firstName", "Salva");
    userDetails.put("lastName", "Rio");

    Response response = given()
      .contentType(JSON)
      .accept(JSON)
      .header("Authorization", authorizationHeader)
      .pathParam("id", userId)
      .body(userDetails)
      .when()
      .put(CONTEXT_PAHT + "/users/{id}")
      .then()
      .statusCode(200)
      .contentType(JSON)
      .extract()
      .response();

    String firstName = response.jsonPath().getString("firstName");
    String lastName  = response.jsonPath().getString("lastName");

    List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

    assertEquals("Salva", firstName);
    assertEquals("Rio", lastName);
    assertNotNull(storedAddresses);
    assertTrue(addresses.size() == storedAddresses.size());
    assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
  }

  /*
   * Test delete User Details
   */
  @Test
  @Ignore
  final void d() {
    Response response = given()
      .header("Authorization", authorizationHeader)
      .accept(JSON)
      .pathParam("id", userId)
      .when()
      .delete(CONTEXT_PAHT + "/users/{id}")
      .then()
      .statusCode(200)
      .contentType(JSON)
      .extract()
      .response();

    String operationResult = response.jsonPath().getString("operationResult");
    assertEquals("SUCESS", operationResult);

  }

}

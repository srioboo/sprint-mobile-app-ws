package com.sirantar.app.ws.restassuredtest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

class TestCreateUser {

  private final String CONTEXT_PAHT = "/mobile-app-ws";
  private final String JSON = "application/json";

  @BeforeEach
  void setUp() throws Exception {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8080;
  }

  @Test
  void testCreateUser() {

    List<Map<String, Object>> userAddresses = new ArrayList<>();

    Map<String, Object> shippingAddress = new HashMap<>();
    shippingAddress.put("city", "Malaga");
    shippingAddress.put("country", "Spain");
    shippingAddress.put("streetName", "123 Street Name");
    shippingAddress.put("postalCode", "ABCCBA");
    shippingAddress.put("type", "shipping");

    Map<String, Object> billingAddress = new HashMap<>();
    billingAddress.put("city", "Malaga");
    billingAddress.put("country", "Spain");
    billingAddress.put("streetName", "123 Street Name");
    billingAddress.put("postalCode", "ABCCBA");
    billingAddress.put("type", "billing");

    userAddresses.add(shippingAddress);
    userAddresses.add(billingAddress);

    Map<String, Object> userDetails = new HashMap<>();
    userDetails.put("name", "Van");
    userDetails.put("lastName", "Cand");
    userDetails.put("email", "salrio1@test.com");
    userDetails.put("password", "123");
    userDetails.put("addresses", userAddresses);

    Response response = given().contentType(JSON).accept(JSON).body(userDetails).when().post(CONTEXT_PAHT
      + "/users").then().statusCode(200).contentType(JSON).extract().response();

    String userId = response.jsonPath().getString("userId");
    assertNotNull(userId);

    String bodyString = response.body().asString();

    try {
      JSONObject responseBodyJson = new JSONObject(bodyString);
      JSONArray addresses = responseBodyJson.getJSONArray("addresses");

      assertNotNull(addresses);
      assertTrue(addresses.length() == 2);

      String addressId = addresses.getJSONObject(0).getString("addressId");
      assertNotNull(addressId);
      assertTrue(addressId.length() == 30);

    } catch (JSONException e) {
      fail(e.getMessage());
    }

  }

}

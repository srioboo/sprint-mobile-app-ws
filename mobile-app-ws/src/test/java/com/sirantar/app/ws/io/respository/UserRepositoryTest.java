package com.sirantar.app.ws.io.respository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sirantar.app.ws.io.entity.AddressEntity;
import com.sirantar.app.ws.io.entity.UserEntity;
import com.sirantar.app.ws.io.repositories.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;

  static boolean recordsCreated = false;

  @BeforeEach
  void setUp() throws Exception {
    if (!recordsCreated) {
      createRecords();
    }
  }

  @Test
  void testGestVerifiedUsers() {
    Pageable pageableRequest = PageRequest.of(0, 2);

    Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);

    assertNotNull(pages);
    List<UserEntity> userEntities = pages.getContent();
    assertNotNull(userEntities);
    assertTrue(userEntities.size() == 2);
  }

  @Test
  final void testFindUserByFirstName() {
    String firstName = "Salva";

    List<UserEntity> users = userRepository.findUserByFirstName(firstName);

    assertNotNull(users);
    assertTrue(users.size() == 2);

    UserEntity user = users.get(0);
    assertTrue(user.getFirstName().equals(firstName));
  }

  @Test
  final void testFindUserByLastName() {
    String lastName = "Rio";

    List<UserEntity> users = userRepository.findUserByLastName(lastName);

    assertNotNull(users);
    assertTrue(users.size() == 2);

    UserEntity user = users.get(0);
    assertTrue(user.getLastName().equals(lastName));
  }

  @Test
  final void testFindUserByKeyword() {
    String keyword = "Sal";

    List<UserEntity> users = userRepository.findUserByKeyword(keyword);

    assertNotNull(users);
    assertTrue(users.size() == 2);

    UserEntity user = users.get(0);
    assertTrue(user.getLastName().contains(keyword) || user.getFirstName().contains(keyword));
  }

  @Test
  final void testFindUserFirtNameAndLastNameByKeyword() {
    String keyword = "Sal";

    List<Object[]> users = userRepository.findUserFirtNameAndLastNameByKeyword(keyword);

    assertNotNull(users);
    assertTrue(users.size() == 2);

    Object[] user = users.get(0);

    assertTrue(user.length == 2);

    String userFirstName = (String) user[0];
    String userLastName  = (String) user[1];

    assertNotNull(userFirstName);
    assertNotNull(userLastName);

    System.out.print("First name = " + userFirstName);
    System.out.print("Last name = " + userLastName);

    // assertTrue(userFirstName.contains(keyword) || userLastName.contains(keyword));
  }

  @Test
  final void testUpdateUserEmailVerificationStatus() {
    boolean newEmailVerificationStatus = false;
    userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, "1ert1");

    UserEntity storedUserDetails = userRepository.findByUserId("1ert1");

    boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();

    assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
  }

  private void createRecords() {
    // Prepare User Entity
    UserEntity userEntity = new UserEntity();
    userEntity.setFirstName("Salva");
    userEntity.setLastName("Rio");
    userEntity.setUserId("1ert1");
    userEntity.setEncrypedPassword("xxx");
    userEntity.setEmail("test@test.com");
    userEntity.setEmailVerificationStatus(true);

    // Prepare User Address
    AddressEntity addressEntity = new AddressEntity();
    addressEntity.setType("shipping");
    addressEntity.setAddressId("ahgyt74hfy");
    addressEntity.setCity("Malaga");
    addressEntity.setCountry("Spain");
    addressEntity.setPostalCode("29003");
    addressEntity.setStreetName("123 Street Address");

    List<AddressEntity> addresses = new ArrayList<>();
    addresses.add(addressEntity);

    userEntity.setAddresses(addresses);

    userRepository.save(userEntity);

    // Prepare User Entity
    UserEntity userEntity2 = new UserEntity();
    userEntity2.setFirstName("Salva");
    userEntity2.setLastName("Rio");
    userEntity2.setUserId("1ert1234");
    userEntity2.setEncrypedPassword("xxx");
    userEntity2.setEmail("test@test.com");
    userEntity2.setEmailVerificationStatus(true);

    // Prepare User Address
    AddressEntity addressEntity2 = new AddressEntity();
    addressEntity2.setType("shipping");
    addressEntity2.setAddressId("ahgyt74hfywww");
    addressEntity2.setCity("Malaga");
    addressEntity2.setCountry("Spain");
    addressEntity2.setPostalCode("29003");
    addressEntity2.setStreetName("123 Street Address");

    List<AddressEntity> addresses2 = new ArrayList<>();
    addresses2.add(addressEntity2);

    userEntity2.setAddresses(addresses2);

    userRepository.save(userEntity2);

    recordsCreated = true;
  }
}

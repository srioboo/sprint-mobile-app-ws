package com.sirantar.app.ws.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sirantar.app.ws.exceptions.UserServiceException;
import com.sirantar.app.ws.io.entity.PasswordResetTokenEntity;
import com.sirantar.app.ws.io.entity.RoleEntity;
import com.sirantar.app.ws.io.entity.UserEntity;
import com.sirantar.app.ws.io.repositories.PasswordResetTokenRespository;
import com.sirantar.app.ws.io.repositories.RoleRepository;
import com.sirantar.app.ws.io.repositories.UserRepository;
import com.sirantar.app.ws.security.UserPrincipal;
import com.sirantar.app.ws.service.UserService;
import com.sirantar.app.ws.shared.AmazonSES;
import com.sirantar.app.ws.shared.Utils;
import com.sirantar.app.ws.shared.dto.AddressDto;
import com.sirantar.app.ws.shared.dto.UserDto;
import com.sirantar.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  Utils utils;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  PasswordResetTokenRespository passwordResetTokenRespository;

  @Autowired
  AmazonSES amazonSES;

  @Autowired
  RoleRepository roleRepository;

  @Override
  public UserDto createUser(UserDto user) {

    if (userRepository.findByEmail(user.getEmail()) != null)
      throw new UserServiceException("record already exists");

    for (int i = 0; i < user.getAddresses().size(); i++) {
      AddressDto address = user.getAddresses().get(i);
      address.setUserDetails(user);
      address.setAddressId(utils.generateAddressId(30));
      user.getAddresses().set(i, address);
    }

    // UserEntity userEntity = new UserEntity();
    // BeanUtils.copyProperties(user, userEntity);
    ModelMapper modelMapper = new ModelMapper();
    UserEntity  userEntity  = modelMapper.map(user, UserEntity.class);

    String publicUserId = utils.generateUserId(30);
    userEntity.setUserId(publicUserId);
    // userEntity.setEncrypedPassword("test");
    userEntity.setEncrypedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
    userEntity.setEmailVerificationStatus(false);

    // Set roles
    Collection<RoleEntity> roleEntities = new HashSet<>();
    for (String role : user.getRoles()) {
      RoleEntity roleEntity = roleRepository.findByName(role);

      if (roleEntity != null) {
        roleEntities.add(roleEntity);
      }
    }
    userEntity.setRoles(roleEntities);

    UserEntity storedUserDetails = userRepository.save(userEntity);

    // UserDto returnValue = new UserDto();
    // BeanUtils.copyProperties(storedUserDetails, returnValue);
    UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

    // Send an email message to user to verify their email address
    amazonSES.verifyEmail(returnValue);

    return returnValue;
  }

  @Override
  public UserDto getUser(String email) {
    UserEntity userEntity = userRepository.findByEmail(email);

    if (userEntity == null)
      throw new UsernameNotFoundException(email);

    UserDto returnValue = new UserDto();
    BeanUtils.copyProperties(userEntity, returnValue);

    return returnValue;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity userEntity = userRepository.findByEmail(email);

    if (userEntity == null)
      throw new UsernameNotFoundException(email);

    return new UserPrincipal(userEntity);

    // This is an old implementation without authorization
    //return new User(userEntity.getEmail(), userEntity.getEncrypedPassword(),
    //		userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<>());

    // return new User(userEntity.getEmail(), userEntity.getEncrypedPassword(), new
    // ArrayList<>());
  }

  @Override
  public UserDto getUserByUserId(String userId) {

    UserDto    returnValue = new UserDto();
    UserEntity userEntity  = userRepository.findByUserId(userId);

    if (userEntity == null)
      throw new UserServiceException("User with ID: " + userId + " not found");

    ModelMapper modelMapper = new ModelMapper();
    returnValue = modelMapper.map(userEntity, UserDto.class);
    // BeanUtils.copyProperties(userEntity, returnValue);

    return returnValue;
  }

  /**
   *
   */
  @Override
  public UserDto updateUser(String userId, UserDto user) {

    UserDto    returnValue = new UserDto();
    UserEntity userEntity  = userRepository.findByUserId(userId);

    if (userEntity == null)
      throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

    userEntity.setFirstName(user.getFirstName());
    userEntity.setLastName(user.getLastName());

    UserEntity updatedUserDetails = userRepository.save(userEntity);

    BeanUtils.copyProperties(updatedUserDetails, returnValue);

    return returnValue;
  }

  @Override
  public void deleteUser(String userId) {
    UserEntity userEntity = userRepository.findByUserId(userId);

    if (userEntity == null)
      throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

    userRepository.delete(userEntity);

  }

  @Override
  public List<UserDto> getUsers(int page, int limit) {
    List<UserDto> returnValue = new ArrayList<>();

    if (page > 0)
      page = page - 1; // para que no sea pagina 0 la primera sino una, evitamos confusion

    Pageable pageableRequest = PageRequest.of(page, limit);

    Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
    List<UserEntity> users     = usersPage.getContent();

    ModelMapper modelMapper = new ModelMapper();

    for (UserEntity userEntity : users) {
      // UserDto userDto = new UserDto();
      // BeanUtils.copyProperties(userEntity, userDto);
      UserDto userDto = modelMapper.map(userEntity, UserDto.class);
      returnValue.add(userDto);
    }

    return returnValue;
  }

  @Override
  public boolean verifyEmailToken(String token) {
    boolean returnValue = false;

    // Find user
    UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

    if (userEntity != null) {
      boolean hastokenExpired = Utils.hasTokenExpired(token);
      if (!hastokenExpired) {
        userEntity.setEmailVerificationToken(null);
        userEntity.setEmailVerificationStatus(Boolean.TRUE);
        userRepository.save(userEntity);

        returnValue = true;
      }
    }

    return returnValue;
  }

  @Override
  public boolean requestPasswordReset(String email) {

    boolean returnValue = false;

    UserEntity userEntity = userRepository.findByEmail(email);

    if (userEntity == null) {
      return returnValue;
    }

    String token = new Utils().generatePasswordResetToken(userEntity.getUserId());

    PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
    passwordResetTokenEntity.setToken(token);
    passwordResetTokenEntity.setUserDetails(userEntity);
    passwordResetTokenRespository.save(passwordResetTokenEntity);

    returnValue = new AmazonSES().sendPasswordResetRequest(userEntity.getFirstName(), userEntity.getEmail(), token);

    return returnValue;
  }

  @Override
  public boolean resetPassword(String token, String password) {
    boolean returnValue = false;

    if (Utils.hasTokenExpired(token)) {
      return returnValue;
    }

    PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRespository.findByToken(token);

    if (passwordResetTokenEntity == null) {
      return returnValue;
    }

    // Prepagre new password
    String encodedPassword = bCryptPasswordEncoder.encode(password);

    // Update User password in database
    UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
    userEntity.setEncrypedPassword(encodedPassword);
    UserEntity savedUserEntity = userRepository.save(userEntity);

    // Verify if password was saved successfully
    if (savedUserEntity != null && savedUserEntity.getEncrypedPassword().equalsIgnoreCase(encodedPassword)) {
      returnValue = true;
    }

    // Remove Password Reset token from database
    passwordResetTokenRespository.delete(passwordResetTokenEntity);

    return returnValue;
  }

}

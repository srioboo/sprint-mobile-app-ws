package com.sirantar.app.ws;

import java.util.Arrays;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sirantar.app.ws.io.entity.AuthorityEntity;
import com.sirantar.app.ws.io.entity.RoleEntity;
import com.sirantar.app.ws.io.entity.UserEntity;
import com.sirantar.app.ws.io.repositories.AuthorityRepository;
import com.sirantar.app.ws.io.repositories.RoleRepository;
import com.sirantar.app.ws.io.repositories.UserRepository;
import com.sirantar.app.ws.shared.Roles;
import com.sirantar.app.ws.shared.Utils;

@Component
public class InitialUsersSetup {

  @Autowired
  AuthorityRepository authorityRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  Utils utils;

  @Autowired
  UserRepository userRepository;

  @EventListener
  @Transactional
  public void onApplicationEvent(ApplicationReadyEvent event) {
    System.out.println("Froma Application ready even ...");

    AuthorityEntity readAuthority   = createAuthority("READ_AUTHORITY");
    AuthorityEntity writeAuthority  = createAuthority("WRITE_AUTHORITY");
    AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

    RoleEntity roleUser  = createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
    RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays
      .asList(readAuthority, writeAuthority, deleteAuthority));

    if (roleAdmin == null) {
      return;
    }

    UserEntity adminUser = new UserEntity();
    adminUser.setFirstName("Salva");
    adminUser.setLastName("Rio");
    adminUser.setEmail("test@test.com");
    adminUser.setEmailVerificationStatus(true);
    adminUser.setUserId(utils.generateUserId(30));
    adminUser.setEncrypedPassword(bCryptPasswordEncoder.encode("12345678"));
    adminUser.setRoles(Arrays.asList(roleAdmin));

    userRepository.save(adminUser);
  }

  @Transactional
  private AuthorityEntity createAuthority(String name) {
    AuthorityEntity authority = authorityRepository.findByName(name);
    if (authority == null) {
      authority = new AuthorityEntity(name);
      authorityRepository.save(authority);
    }
    return authority;
  }

  @Transactional
  private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {

    RoleEntity role = roleRepository.findByName(name);
    if (role == null) {
      role = new RoleEntity(name);
      role.setAuthorities(authorities);
      roleRepository.save(role);
    }
    return role;
  }
}

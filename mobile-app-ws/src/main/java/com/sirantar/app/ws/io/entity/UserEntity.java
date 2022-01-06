package com.sirantar.app.ws.io.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

// @Entity(name="users")
@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

  private static final long serialVersionUID = 6066893820822210606L;

  @Id
  @GeneratedValue
  private long id;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false,
          length = 50)
  private String firstName;

  @Column(nullable = false,
          length = 50)
  private String lastName;

  //@Column(nullable=false, length=120, unique=true) // con indicacion de que es unica
  @Column(nullable = false,
          length = 120)
  private String email;

  @Column(nullable = false)
  private String encrypedPassword;

  private String emailVerificationToken;

  // @Column(nullable=false, columnDefinition = "boolean default false")
  // private Boolean emailVerificationStatus;

  @Column(nullable = false)
  private Boolean emailVerificationStatus = false;

  @OneToMany(mappedBy = "userDetails",
             cascade = CascadeType.ALL)
  private List<AddressEntity> addresses;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEncrypedPassword() {
    return encrypedPassword;
  }

  public void setEncrypedPassword(String encrypedPassword) {
    this.encrypedPassword = encrypedPassword;
  }

  public String getEmailVerificationToken() {
    return emailVerificationToken;
  }

  public void setEmailVerificationToken(String emailVerificationToken) {
    this.emailVerificationToken = emailVerificationToken;
  }

  public Boolean getEmailVerificationStatus() {
    return emailVerificationStatus;
  }

  public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
    this.emailVerificationStatus = emailVerificationStatus;
  }

  public List<AddressEntity> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<AddressEntity> addresses) {
    this.addresses = addresses;
  }

}

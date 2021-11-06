package com.sirantar.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sirantar.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

	//UserEntity findUserByEmail(String email);
	
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId); // findByUserId debe tener estas palabras find By y el campo user_id
}

package com.sirantar.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.sirantar.app.ws.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRespository extends CrudRepository<PasswordResetTokenEntity, Long> {
	PasswordResetTokenEntity findByToken(String token);
}

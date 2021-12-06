package com.sirantar.app.ws.service;

import java.util.List;

import com.sirantar.app.ws.shared.dto.AddressDto;

public interface AddressService {

	List<AddressDto> getAddresses(String userId);
	
}

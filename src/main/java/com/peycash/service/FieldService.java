package com.peycash.service;

import java.util.List;

import com.peycash.persistence.domain.Fields;

public interface FieldService {

	List<Fields> findByMTI(String mti);

	void registerFields(String MTI, List<Fields> list);

}

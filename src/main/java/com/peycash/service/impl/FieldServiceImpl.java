package com.peycash.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.ConfigRegister;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.FieldDAO;
import com.peycash.persistence.domain.Fields;
import com.peycash.service.FieldService;

@Service
public class FieldServiceImpl implements FieldService{

	@Autowired
	private FieldDAO daoField;
	
	@Override
	@Transactional(readOnly=true)
	public List<Fields> findByMTI(String mti){
		try{
			List<Fields> fields = daoField.findFieldsByMTI(mti);
			return fields;
		}catch(PersistenceException e){
			return null;
		}
	}
	
	
	@Override
	public void registerFields(String MTI, List<Fields> list){
		if(ConfigRegister.getInstance().getFieldMap() == null){
			ConfigRegister.getInstance().setFieldMap(new HashMap<String, List<Fields>> (0));
		}
		ConfigRegister.getInstance().getFieldMap().put(MTI, list);
	}
}

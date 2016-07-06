/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.ConfigRegister;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.ResponseCodeDAO;
import com.peycash.persistence.domain.Responsecodes;
import com.peycash.service.ResponseCodeService;
import com.peycash.service.ServiceException;

/**
 * Implementation of the interface of services related to response codes
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class ResponseCodeServiceImpl implements ResponseCodeService{

	/**
	 * Injected spring value
	 */
	@Autowired
	private ResponseCodeDAO daoResponseCodes;
	
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseCodeService#findSuppportedCodes()
	 */
	@Override
	@Transactional(readOnly=true)
	public List<String> findSuppportedCodes() throws ServiceException{
		try{
			List<Responsecodes> listKnown = daoResponseCodes.getAll();
			List<String> knownCodes = listKnown.stream().map(x -> x.getResponsecode()).collect(Collectors.toList());
			return knownCodes;
		}catch (PersistenceException e) {
			throw new ServiceException("Error in findSuppportedCodes:"+e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseCodeService#registerSupportedCodes(java.util.List)
	 */
	@Override
	public void registerSupportedCodes(List<String> listSupportedCodes){
		ConfigRegister.getInstance().setListSupportedCodes(listSupportedCodes);
	}
	
}

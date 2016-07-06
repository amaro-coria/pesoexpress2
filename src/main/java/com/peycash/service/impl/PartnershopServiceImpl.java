/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.PartnershopDAO;
import com.peycash.persistence.domain.Partnershop;
import com.peycash.service.PartnershopService;
import com.peycash.service.ServiceException;

/**
 * Implementation of the service interface
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class PartnershopServiceImpl implements PartnershopService {

	/**
	 * Injected spring value
	 */
	@Autowired
	private PartnershopDAO daoPartnershop;
	
	/* (non-Javadoc)
	 * @see com.peycash.service.PartnershopService#findById(int)
	 */
	@Override
	@Transactional(readOnly=true)
	public Partnershop findById(int id) throws ServiceException{
		try{
			Partnershop partner = daoPartnershop.findByPK(id);
			return partner;
		}catch(PersistenceException e){
			throw new ServiceException("Error in findById:"+e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.PartnershopService#isValidAffiliation(java.lang.String)
	 */
	@Override
	@Transactional(readOnly=true)
	public boolean isValidAffiliation(String affiliation) throws ServiceException{
		try{
			affiliation = affiliation.trim();
			long affiliationPesoExpress = Long.parseLong(affiliation);
			boolean exists = daoPartnershop.existsByAffiliation(affiliationPesoExpress);
			return exists;
		}catch(PersistenceException e){
			throw new ServiceException("Error in isValidAffiliation:"+e.getMessage(), e);
		}
	}
	
}

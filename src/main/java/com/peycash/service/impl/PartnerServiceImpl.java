/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.PartnerDAO;
import com.peycash.service.PartnerService;
import com.peycash.service.ServiceException;

/**
 * Implementation of the service interface
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class PartnerServiceImpl implements PartnerService {

	
	/**
	 * Injected spring value
	 */
	@Autowired
	private PartnerDAO daoPartner;
	
	/* (non-Javadoc)
	 * @see com.peycash.service.PartnerService#isValidAffiliation(java.lang.String)
	 */
	@Override
	@Transactional(readOnly=true)
	public boolean isValidAffiliation(String affiliation) throws ServiceException{
		try{
			affiliation = affiliation.trim();
			long affiliationPesoExpress = Long.parseLong(affiliation);
			boolean exists = daoPartner.existsByAffiliation(affiliationPesoExpress);
			return exists;
		}catch(PersistenceException e){
			throw new ServiceException("Error in isValidAffiliation:"+e.getMessage(), e);
		}
	}
	
}

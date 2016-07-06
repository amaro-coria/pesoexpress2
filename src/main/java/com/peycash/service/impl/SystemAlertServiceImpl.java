/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.util.DateUtils;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.SystemAlertDAO;
import com.peycash.persistence.domain.Systemalerts;
import com.peycash.service.ServiceException;
import com.peycash.service.SystemAlertService;

/**
 * Implementation of the service for system alerts
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class SystemAlertServiceImpl implements SystemAlertService{

	/**
	 * Injected spring value
	 */
	@Autowired
	private SystemAlertDAO dao;
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SystemAlertService#registerAlert(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void registerAlert(String category, String description) throws ServiceException{
		Systemalerts alert = new Systemalerts();
		alert.setAlert(category);
		alert.setDsc(description);
		alert.setDateRegister(DateUtils.getCurrentlDate());
		try {
			dao.save(alert);
		} catch (PersistenceException e) {
			throw new ServiceException("Error in xx:"+e.getMessage(), e);
		}
	}
}

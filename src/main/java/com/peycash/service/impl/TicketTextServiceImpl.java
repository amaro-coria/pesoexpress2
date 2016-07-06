/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.ConfigRegister;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.TicketTextDAO;
import com.peycash.persistence.domain.Tickettext;
import com.peycash.service.TicketTextService;

/**
 * Implementation of the service interface
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class TicketTextServiceImpl implements TicketTextService{

	/**
	 * Injected spring value
	 */
	@Autowired
	private TicketTextDAO daoTT;
	
	/* (non-Javadoc)
	 * @see com.peycash.service.TicketTextService#getTicketText()
	 */
	@Override
	@Transactional(readOnly=true)
	public String getTicketText(){
		try{
			Tickettext tt = daoTT.findLatestActive();
			String text = tt.getTtext();
			return text;
		}catch(PersistenceException e){
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.TicketTextService#registerTicketText(java.lang.String)
	 */
	@Override
	public void registerTicketText(String text){
		ConfigRegister.getInstance().setTicketText(text);
	}
}

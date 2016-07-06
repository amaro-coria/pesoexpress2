/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.util.DateUtils;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.TransactionLogDAO;
import com.peycash.persistence.domain.Transactionlog;
import com.peycash.service.ServiceException;
import com.peycash.service.TransactionLogService;

/**
 * Implementation of the service
 * @author Jorge Amaro
 * @since 1.0
 * @version 1.0
 *
 */
@Service
public class TransactionLogServiceImpl implements TransactionLogService{

	/**
	 * Injected spring value
	 */
	@Autowired
	private TransactionLogDAO daoTransactionLog;
	
	/* (non-Javadoc)
	 * @see com.peycash.service.TransactionLogService#saveTransaction(com.peycash.persistence.domain.Transactionlog)
	 */
	@Override
	@Transactional
	public long saveTransaction(Transactionlog txLog) throws ServiceException{
		try {
			long pk = daoTransactionLog.save(txLog);
			return pk;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in saveTransaction:"+e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.TransactionLogService#updateTransaction(java.lang.String, java.lang.String, long)
	 */
	@Override
	@Transactional
	public void updateTransaction(String sink, String messageSink, long pk) throws ServiceException{
		try{
			Transactionlog txLog = daoTransactionLog.findByPK(pk);
			txLog.setSink(sink);
			txLog.setMessagesink(messageSink);
			txLog.setUpdateserver(DateUtils.getCurrentlDate());
			daoTransactionLog.update(txLog);
		}catch(PersistenceException e){
			throw new ServiceException("Error in updateTransaction:"+e.getMessage(), e);
		}
	}
	
	
	
}

/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import com.peycash.persistence.domain.Transactionlog;

/**
 * Interface for services related to transaction logs
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface TransactionLogService {

	/**
	 * Saves an instance of transactionlog
	 * @param txLog
	 * @return the ID of the record for updating or gettig purposes
	 * @throws ServiceException
	 */
	long saveTransaction(Transactionlog txLog) throws ServiceException;

	/**
	 * Updates the transaction log based on the parameters
	 * @param sink the sink
	 * @param messageSink the message detail
	 * @param pk the pk of the record
	 * @throws ServiceException
	 */
	void updateTransaction(String sink, String messageSink, long pk)
			throws ServiceException;

}

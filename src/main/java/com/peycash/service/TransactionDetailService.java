/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import java.math.BigDecimal;
import java.util.List;

import com.peycash.persistence.domain.Transactiondetail;

/**
 * Interface for services for details of transactions
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface TransactionDetailService {

	/**
	 * <pre>Checks if a transaction exists with given parameters<pre>
	 * <pre>If the result is true, check for existsTransactionDetailDeep that will give an absolute result</pre>
	 * @param auditNumber
	 * @param referenceNumber
	 * @param amount
	 * @param isoDate
	 * @return true if a transaction exists
	 * @throws ServiceException
	 */
	boolean existsTransactionDetail(String auditNumber, String referenceNumber,
			BigDecimal amount, String isoDate) throws ServiceException;

	/**
	 * Checks if a transaction exists with given parameters
	 * @param auditNumber
	 * @param referenceNumber
	 * @param amount
	 * @param terminalid
	 * @param partnerAffiliation
	 * @param partnershopAffiliation
	 * @param isoDate
	 * @return true if a transaction exists
	 * @throws ServiceException
	 */
	boolean existsTransactionDetailDeep(String auditNumber,
			String referenceNumber, BigDecimal amount, String terminalid,
			String partnerAffiliation, String partnershopAffiliation, String isoDate)
			throws ServiceException;

	/**
	 * Finds the id of the record no matter if the transaction has been already reversed
	 * @param auditNumber
	 * @param referenceNumber
	 * @param amount
	 * @param terminalid
	 * @param partnerAffiliation
	 * @param partnershopAffiliation
	 * @param isoDate
	 * @param authorizationresponse
	 * @param uniqueTx
	 * @return the id of the record
	 * @throws ServiceException
	 */
	long findIdForExistingTransaction(String auditNumber,
			String referenceNumber, BigDecimal amount, String terminalid,
			String partnerAffiliation, String partnershopAffiliation,
			String isoDate, String authorizationresponse, String uniqueTx)
			throws ServiceException;

	/**
	 * Checks if the transaction is already reversed
	 * @param pk the id of the record
	 * @return true if reversed, false otherwise
	 * @throws ServiceException
	 */
	boolean isTransactionAlreadyReversed(long pk) throws ServiceException;

	/**
	 * Retrieves the registered authorization response for the given transaction detail
	 * @param pk the id of the record
	 * @return the authorization code
	 * @throws ServiceException
	 */
	String findPreviousAutResponse(long pk) throws ServiceException;

	/**
	 * Retrieves the related details based on the root transaction
	 * @param pkTransaction the id of the root transaction
	 * @return the list of the details
	 * @throws ServiceException
	 */
	List<Transactiondetail> findTransactionDetailsByRoot(long pkTransaction)
			throws ServiceException;

}

/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.jpos.iso.ISOMsg;

import com.peycash.persistence.domain.Daylitransactions;
import com.peycash.persistence.domain.Monthlytransactions;
import com.peycash.persistence.domain.Transactions;

/**
 * Interface for services of transactions
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface TransactionService {

	/**
	 * Check if a transaction existst with given parameters
	 * @param noCelSend
	 * @param noCelReceive
	 * @param amount
	 * @return true if exists
	 * @throws ServiceException
	 */
	boolean existsTransaction(String noCelSend, String noCelReceive,
			BigDecimal amount) throws ServiceException;

	/**
	 * Retrieves all the transactions during the current day
	 * @return
	 * @throws ServiceException
	 */
	List<Daylitransactions> findDayliTransactions() throws ServiceException;

	/**
	 * Retrieves all the transactions during the current month
	 * @return
	 * @throws ServiceException
	 */
	List<Monthlytransactions> findMonthlyTransactions() throws ServiceException;

	/**
	 * Retrieves the amount sent by the given cellular number in the current month 
	 * @param noCelCustomerSender
	 * @return
	 * @throws ServiceException
	 */
	double findCurrentMonthlyAmountByCustomerSender(long noCelCustomerSender)
			throws ServiceException;

	/**
	 * Retrieves the amount send by the given cellular number in the current day
	 * @param noCelCustomerSender
	 * @return
	 * @throws ServiceException
	 */
	double findCurrentDayliAmountByCustomerSender(long noCelCustomerSender)
			throws ServiceException;

	/**
	 * Retrieves the amount received by the given cellular number in the current month
	 * @param noCelCustomerReceiver
	 * @return
	 * @throws ServiceException
	 */
	double findCurrentMonthlyAmountByCustomerReceiver(long noCelCustomerReceiver)
			throws ServiceException;

	/**
	 * Retrieves the amount received by the given cellular number in the current day
	 * @param noCelCustomerReceiver
	 * @return
	 * @throws ServiceException
	 */
	double findCurrentDayliAmountByCustomerReceiver(long noCelCustomerReceiver)
			throws ServiceException;

	/**
	 * Generates an authorization code valid based on the last hour
	 * @param nocelsend the cellular number that deposits the money
	 * @return the aut code
	 * @throws ServiceException
	 */
	String generateAutCode_deposit(long nocelsend) throws ServiceException;

	/**
	 * Generates an authorization code valid based on the last hour
	 * @param celnumreceive the cellular that withdraw the money
	 * @return the aut code
	 * @throws ServiceException
	 */
	String generateAutCode_withdraw(long celnumreceive) throws ServiceException;

	/**
	 * Gets the pin number for the current transaction
	 * @param celreceive the cellular that receive the money
	 * @return the pin number (length of 4)
	 * @throws ServiceException
	 */
	String getPinNumber(long celreceive) throws ServiceException;

	/**
	 * Generates the transfer record on the database
	 * @param m the iso message
	 * @param pin the current pin
	 * @param responseCode the response code present in field 39
	 * @param autResponse the authorization response code present in field 38 if field 39 is present
	 * @param pkTxDetail the id to be set, the same id that the log
	 * @return
	 * @throws ServiceException
	 */
	long generateTransfer(ISOMsg m, String pin, String responseCode, String autResponse, long pkTxDetail) throws ServiceException;

	/**
	 * Retrieves the amount received by the customer 
	 * @param noCelCustomerReceiver
	 * @return
	 * @throws ServiceException
	 */
	double findCurrentDayliAmountByCustomerReceiverWithdraw(
			long noCelCustomerReceiver) throws ServiceException;

	/**
	 * Retrieves the amount withdrawn by the customer
	 * @param noCelCustomerReceiver
	 * @return
	 * @throws ServiceException
	 */
	double findCurrentMonthlyAmountByCustomerReceiverWithdraw(
			long noCelCustomerReceiver) throws ServiceException;

	/**
	 * Checks if the cellular given has pendings withdraws
	 * @param noCelReceive
	 * @return true if at least is one withdraw
	 * @throws ServiceException
	 */
	boolean hasWithdrawsPending(String noCelReceive) throws ServiceException;

	/**
	 * Generates the withdraw movements 
	 * @param m
	 * @param noCelReceive
	 * @param pinClear
	 * @param amount
	 * @param responseCode
	 * @param autResponse
	 * @param pkTxDetail the id to be set, the same id that the log
	 * @return true if the withdraw was executed, false otherwise (like if no withdraws available or pin error)
	 */
	boolean generateWithdraw(ISOMsg m, String noCelReceive, String pinClear,
			double amount, String responseCode, String autResponse, long pkTxDetail);

	/**
	 * Finds out if the given transaction for sending is able to be reversed
	 * @param pkDetail the pk of the detail of the transaction
	 * @return true if the transaction can be reversed, false otherwise
	 * @throws ServiceException
	 */
	boolean isReversibleTransaction_Send(long pkDetail) throws ServiceException;

	/**
	 * Finds the id of the root transaction based on its detail
	 * @param pkDetail the pk of the detail
	 * @return the pk of the root record
	 * @throws ServiceException
	 */
	long findPKRootTransactionByDetailPK(long pkDetail) throws ServiceException;

	/**
	 * Reverses a transaction with the given parameters, works for send and retrieve events
	 * @param request
	 * @param pkTransaction
	 * @param pkDetail
	 * @param responseCode
	 * @param pkTxDetail the id to be set, the same id that the log
	 * @throws ServiceException if the transaction has not been reversed
	 */
	void reverseTransaction(ISOMsg request, long pkTransaction,
			long pkDetail, String responseCode, long pkTxDetail) throws ServiceException;

	/**
	 * Finds transactions between two dates
	 * @param beginDate the initial date
	 * @param endDate the final date
	 * @return the transactions
	 * @throws ServiceException
	 */
	List<Transactions> findTransactions(Date beginDate, Date endDate)
			throws ServiceException;

	/**
	 * Cancels all the transactions for the given number
	 * @param numcelsend the cel number
	 * @throws ServiceException
	 * @return the list of cancelled transactions
	 */
	List<Transactions> cancellTransactions(long numcelsend) throws ServiceException;
	
	/**
	 * Finds transactions by customer
	 * @param numcelsend the cel number
	 * @throws ServiceException
	 * @return the transactions
	 */
	List<Transactions> findByNumCcel(long numcelsend) throws ServiceException;

}

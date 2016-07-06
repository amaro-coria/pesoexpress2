/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISODate;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.PinUtils;
import com.peycash.common.RandomUtils;
import com.peycash.common.util.AmountUtils;
import com.peycash.common.util.Constants;
import com.peycash.common.util.DateUtils;
import com.peycash.common.util.TransferStateFactory;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.DayliTransactionsDAO;
import com.peycash.persistence.dao.MonthlyTransactionsDAO;
import com.peycash.persistence.dao.PartnerDAO;
import com.peycash.persistence.dao.PartnershopDAO;
import com.peycash.persistence.dao.PinSecurityDAO;
import com.peycash.persistence.dao.TransactionDAO;
import com.peycash.persistence.dao.TransactionDetailDAO;
import com.peycash.persistence.dao.TransferStateDAO;
import com.peycash.persistence.dao.TxDetailsDAO;
import com.peycash.persistence.domain.Daylitransactions;
import com.peycash.persistence.domain.Messagetype;
import com.peycash.persistence.domain.Monthlytransactions;
import com.peycash.persistence.domain.Pinsecurity;
import com.peycash.persistence.domain.Processingcode;
import com.peycash.persistence.domain.Transactiondetail;
import com.peycash.persistence.domain.Transactions;
import com.peycash.persistence.domain.Transferstate;
import com.peycash.persistence.domain.Txdetails;
import com.peycash.service.ServiceException;
import com.peycash.service.TransactionService;

/**
 * Implementation of the service interface
 * 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	/**
	 * Injected spring value
	 */
	@Autowired
	private TransactionDAO daoTransaction;
	/**
	 * Injected spring value
	 */
	@Autowired
	private DayliTransactionsDAO daoTxDay;
	/**
	 * Injected spring value
	 */
	@Autowired
	private MonthlyTransactionsDAO daoTxMonth;
	/**
	 * Injected spring value
	 */
	@Autowired
	private PinSecurityDAO daoPinSecurity;
	/**
	 * Injected spring value
	 */
	@Autowired
	private PartnerDAO daoPartner;
	/**
	 * Injected spring value
	 */
	@Autowired
	private PartnershopDAO daoPartnershop;
	/**
	 * Injected spring value
	 */
	@Autowired
	private TransactionDetailDAO daoTxDetail;

	/**
	 * Injected spring value
	 */
	@Autowired
	private TxDetailsDAO daoTxDetailsSet;

	/**
	 * Injected spring value
	 */
	@Autowired
	private PinSecurityDAO daoPIN;

	/**
	 * Injected spring value
	 */
	@Autowired
	private TransferStateDAO daoTransferState;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#existsTransaction(java.lang.String
	 * , java.lang.String, java.math.BigDecimal)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean existsTransaction(String noCelSend, String noCelReceive,
			BigDecimal amount) throws ServiceException {
		try {
			long noCelS = 0;
			if (noCelSend != null && !noCelSend.isEmpty()) {
				noCelS = Long.parseLong(noCelSend);
			}
			long noCelR = 0;
			if (noCelReceive != null && !noCelReceive.isEmpty()) {
				noCelR = Long.parseLong(noCelReceive);
			}
			boolean result = daoTransaction.exitstTransaction(noCelS, noCelR,
					amount);
			return result;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in existsTransaction:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#findDayliTransactions()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Daylitransactions> findDayliTransactions()
			throws ServiceException {
		try {
			List<Daylitransactions> listTx = daoTxDay.getAll();
			return listTx;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in findDayliTransactions:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#findTransactions(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Transactions> findTransactions(Date beginDate, Date endDate)
			throws ServiceException {
		try {
			List<Transactions> list = daoTransaction.findTransactions(
					beginDate, endDate);
			return list;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in findTransactions:"
					+ e.getMessage(), e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#findMonthlyTransactions()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Monthlytransactions> findMonthlyTransactions()
			throws ServiceException {
		try {
			List<Monthlytransactions> listTx = daoTxMonth.getAll();
			return listTx;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in findMonthlyTransactions:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#
	 * findCurrentMonthlyAmountByCustomerSender(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public double findCurrentMonthlyAmountByCustomerSender(
			long noCelCustomerSender) throws ServiceException {
		try {
			List<Monthlytransactions> listTx = daoTxMonth
					.findTransactionsByCustomerSend(noCelCustomerSender);
			if (CollectionUtils.isEmpty(listTx)) {
				return 0;
			}
			List<Double> amountList = listTx.stream()
					.map(x -> x.getTransferamount().doubleValue())
					.collect(Collectors.toList());
			double total = amountList.stream().mapToDouble((x) -> x).sum();
			return total;
		} catch (PersistenceException e) {
			throw new ServiceException(
					"Error in findCurrentMonthlyAmountByCustomerSender:"
							+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#
	 * findCurrentMonthlyAmountByCustomerReceiver(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public double findCurrentMonthlyAmountByCustomerReceiver(
			long noCelCustomerReceiver) throws ServiceException {
		try {
			List<Monthlytransactions> listTx = daoTxMonth
					.findTransactionsByCustomerReceive(noCelCustomerReceiver);
			if (CollectionUtils.isEmpty(listTx)) {
				return 0;
			}
			List<Double> amountList = listTx.stream()
					.map(x -> x.getTransferamount().doubleValue())
					.collect(Collectors.toList());
			double total = amountList.stream().mapToDouble((x) -> x).sum();
			return total;
		} catch (PersistenceException e) {
			throw new ServiceException(
					"Error in findCurrentMonthlyAmountByCustomerReceiver:"
							+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#
	 * findCurrentMonthlyAmountByCustomerReceiverWithdraw(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public double findCurrentMonthlyAmountByCustomerReceiverWithdraw(
			long noCelCustomerReceiver) throws ServiceException {
		try {
			List<Monthlytransactions> listTx = daoTxMonth
					.findTransactionsByCustomerReceiveWithdraw(noCelCustomerReceiver);
			if (CollectionUtils.isEmpty(listTx)) {
				return 0;
			}
			List<Double> amountList = listTx.stream()
					.map(x -> x.getTransferamount().doubleValue())
					.collect(Collectors.toList());
			double total = amountList.stream().mapToDouble((x) -> x).sum();
			return total;
		} catch (PersistenceException e) {
			throw new ServiceException(
					"Error in findCurrentMonthlyAmountByCustomerReceiverWithdraw:"
							+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#findCurrentDayliAmountByCustomerSender
	 * (long)
	 */
	@Override
	@Transactional(readOnly = true)
	public double findCurrentDayliAmountByCustomerSender(
			long noCelCustomerSender) throws ServiceException {
		try {
			List<Daylitransactions> listTx = daoTxDay
					.findTransactionsByCustomerSend(noCelCustomerSender);
			if (CollectionUtils.isEmpty(listTx)) {
				return 0;
			}
			List<Double> amountList = listTx.stream()
					.map(x -> x.getTransferamount().doubleValue())
					.collect(Collectors.toList());
			double total = amountList.stream().mapToDouble((x) -> x).sum();
			return total;
		} catch (PersistenceException e) {
			throw new ServiceException(
					"Error in findCurrentDayliAmountByCustomerSender:"
							+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#
	 * findCurrentDayliAmountByCustomerReceiver(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public double findCurrentDayliAmountByCustomerReceiver(
			long noCelCustomerReceiver) throws ServiceException {
		try {
			List<Daylitransactions> listTx = daoTxDay
					.findTransactionsByCustomerReceive(noCelCustomerReceiver);
			if (CollectionUtils.isEmpty(listTx)) {
				return 0;
			}
			List<Double> amountList = listTx.stream()
					.map(x -> x.getTransferamount().doubleValue())
					.collect(Collectors.toList());
			double total = amountList.stream().mapToDouble((x) -> x).sum();
			return total;
		} catch (PersistenceException e) {
			throw new ServiceException(
					"Error in findCurrentDayliAmountByCustomerReceiver:"
							+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#
	 * findCurrentDayliAmountByCustomerReceiverWithdraw(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public double findCurrentDayliAmountByCustomerReceiverWithdraw(
			long noCelCustomerReceiver) throws ServiceException {
		try {
			List<Daylitransactions> listTx = daoTxDay
					.findTransactionsByCustomerReceiveWithdraw(noCelCustomerReceiver);
			if (CollectionUtils.isEmpty(listTx)) {
				return 0;
			}
			List<Double> amountList = listTx.stream()
					.map(x -> x.getTransferamount().doubleValue())
					.collect(Collectors.toList());
			double total = amountList.stream().mapToDouble((x) -> x).sum();
			return total;
		} catch (PersistenceException e) {
			throw new ServiceException(
					"Error in findCurrentDayliAmountByCustomerReceiverWithdraw:"
							+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#generateAutCode_deposit(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public String generateAutCode_deposit(long nocelsend)
			throws ServiceException {
		try {
			Date date1hourAgo = DateUtils.getCurrentDatePlusMinusHoursMinutes(
					false, true, 1, 0);
			List<String> storedAudCodes = daoTransaction
					.getAutCodeByNoCel_deposit(nocelsend, date1hourAgo);
			String autCode = getAutCodeValid(storedAudCodes);
			return autCode;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in generateAutCode_send:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#generateAutCode_withdraw(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public String generateAutCode_withdraw(long celnumreceive)
			throws ServiceException {
		try {
			Date date1hourAgo = DateUtils.getCurrentDatePlusMinusHoursMinutes(
					false, true, 1, 0);
			List<String> storedAudCodes = daoTransaction
					.getAutCodeByNoCel_withdraw(celnumreceive, date1hourAgo);
			String autCode = getAutCodeValid(storedAudCodes);
			return autCode;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in generateAutCode_send:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#getPinNumber(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public String getPinNumber(long celreceive) throws ServiceException {
		try {
			List<String> listPinSecData = daoTransaction
					.getPinSecByNoCel_deposit(celreceive);
			String pin = getPin();
			if (CollectionUtils.isEmpty(listPinSecData)) {
				return pin;
			}
			if (listPinSecData.contains(pin)) {
				getPinNumber(celreceive);
			}
			return pin;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in getPinNumber:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#hasWithdrawsPending(java.lang.
	 * String)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean hasWithdrawsPending(String noCelReceive)
			throws ServiceException {
		Long nocel = Long.parseLong(noCelReceive);
		try {
			List<String> listPinSecData = daoTransaction
					.getPinSecByNoCel_deposit(nocel);
			if (CollectionUtils.isEmpty(listPinSecData)) {
				return false;
			}
			return true;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in hasWithdrawsPending:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#generateWithdraw(org.jpos.iso.
	 * ISOMsg, java.lang.String, java.lang.String, double, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@Transactional
	public boolean generateWithdraw(ISOMsg m, String noCelReceive,
			String pinClear, double amount, String responseCode,
			String autResponse, long pkTxDetail) {
		long celnumreceive = Long.parseLong(noCelReceive);
		String hashPin = PinUtils.hashPin(pinClear);
		BigDecimal transferamount = new BigDecimal(amount);
		try {
			long pkTransaction = daoTransaction.findTransactionForWithdraw(
					celnumreceive, hashPin, transferamount);
			if (pkTransaction == 0) {
				return false;
			}
			// daoTransaction.generateWithdraw(pkTransaction);
			Transactions trans = daoTransaction.findByPK(pkTransaction);
			Transferstate state = TransferStateFactory
					.getTransferStateRECEIVED();
			trans.setTransferstate(state);
			trans.setLastupdate(DateUtils.getCurrentlDate());
			daoTransaction.update(trans);
			long idPin = daoTransaction.findIdPinSecurity(pkTransaction);
			Pinsecurity pinSec = daoPIN.findByPK(idPin);
			pinSec.setActive(0);
			daoPIN.update(pinSec);
			long pkTxDetails = generateTxDetail(m, pinClear, responseCode,
					autResponse, pkTxDetail);
			Transactiondetail txDetail = daoTxDetail.findByPK(pkTxDetails);
			Txdetails setDetails = new Txdetails();
			Transactions txRetrieved = daoTransaction.findByPK(pkTransaction);
			setDetails.setTransactions(txRetrieved);
			setDetails.setTransactiondetail(txDetail);
			setDetails.setDateInserted(DateUtils.getCurrentlDate());
			daoTxDetailsSet.save(setDetails);

		} catch (PersistenceException e) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#generateTransfer(org.jpos.iso.
	 * ISOMsg, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	@Override
	public long generateTransfer(ISOMsg m, String pin, String responseCode,
			String autResponse, long pkTxDetail) throws ServiceException {
		try {
			Transactions transaction = new Transactions();
			String nocelReceiveS = m.getString(78);
			String nocelSendS = m.getString(76);
			long nocelreceive = Long.parseLong(nocelReceiveS);
			long nocelsend = Long.parseLong(nocelSendS);
			long pkTxDetails = generateTxDetail(m, pin, responseCode,
					autResponse, pkTxDetail);
			String amountString = m.getString(4);
			String amount = AmountUtils.formatAmountString(amountString);
			BigDecimal transferamount = new BigDecimal(amount);
			transaction.setCelnumreceive(nocelreceive);
			transaction.setCelnumsend(nocelsend);
			transaction.setDateserver(DateUtils.getCurrentlDate());
			long pkPin = generatePinSecurity(pin);
			Pinsecurity ps = new Pinsecurity();
			ps.setIdpinsecurity(pkPin);
			transaction.setPinsecurity(ps);
			transaction.setTransferamount(transferamount);
			Transferstate state = new Transferstate();
			state.setIdtransferstate(Constants.TRANSFER_STATE_SENT_0200);
			transaction.setTransferstate(state);
			long pk = daoTransaction.save(transaction);
			Transactiondetail txDetail = daoTxDetail.findByPK(pkTxDetails);
			// transaction.setTransactiondetail(txDetail);
			Txdetails setDetails = new Txdetails();
			Transactions txRetrieved = daoTransaction.findByPK(pk);
			setDetails.setTransactions(txRetrieved);
			setDetails.setTransactiondetail(txDetail);
			setDetails.setDateInserted(DateUtils.getCurrentlDate());
			daoTxDetailsSet.save(setDetails);
			return pk;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in generateTransfer:"
					+ e.getMessage(), e);
		}
	}

	/**
	 * Generates the transaction detail based on the given message
	 * 
	 * @param m
	 *            the iso message
	 * @param pin
	 *            the current pin
	 * @param responseCode
	 *            the response code present in field 39
	 * @param pkTxDetail
	 *            the id to be set in the transaction detail
	 * 
	 *            <pre>
	 * It is needed to be the same id that the registered in the log
	 * </pre>
	 * @return the PK of the record
	 * @throws NumberFormatException
	 * @throws PersistenceException
	 */
	@Transactional
	private long generateTxDetail(ISOMsg m, String pin, String responseCode,
			String authorizationResponse, long pkTxDetail)
			throws NumberFormatException, PersistenceException {
		Transactiondetail txDetail = new Transactiondetail();
		if (m.hasField(102)) {
			String _unique = m.getString(102);
			_unique = _unique.trim();
			long unique = Long.parseLong(_unique);
			txDetail.setUniqueidtransaction(unique);
		}
		String dateTime = m.getString(7);
		Date isoDate = ISODate.parseISODate(dateTime);
		txDetail.setServerdate(isoDate);
		String amount = m.getString(4);
		String amountS = AmountUtils.formatAmountString(amount);
		BigDecimal amountValue = new BigDecimal(amountS);
		txDetail.setAmounttrx(amountValue);
		String auditString = m.getString(11);
		int audit = Integer.parseInt(auditString);
		txDetail.setAuditnumber(audit);
		String partnerAffiliation = m.getString(43);
		String partnershopAffiliation = m.getString(42);
		partnerAffiliation = partnerAffiliation.trim();
		partnershopAffiliation = partnershopAffiliation.trim();
		int companyId = daoPartner.findIdByAffiliation(Long
				.parseLong(partnerAffiliation));
		int storeId = daoPartnershop.findIdByAffiliation(Long
				.parseLong(partnershopAffiliation));
		txDetail.setCompanyid(companyId);
		txDetail.setCurrencycode(m.getString(49));
		Messagetype mt = new Messagetype();
		mt.setIdmessagetype(Constants.MESSAGE_TYPE_0200);
		txDetail.setMessagetype(mt);
		String pinHash = null;
		if (StringUtils.isNotEmpty(pin)) {
			pinHash = PinUtils.hashPin(pin);
		}
		txDetail.setPindata(pinHash);
		txDetail.setPosentrymode((short) 0);
		String processingCode = m.getString(3);
		Processingcode pc = new Processingcode();
		switch (processingCode) {
		case Constants.PROCESSING_CODE_STRING_000809:
			pc.setIdprocessingcode(Constants.PROCESSING_CODE_000809);
			break;
		case Constants.PROCESSING_CODE_STRING_010908:
			pc.setIdprocessingcode(Constants.PROCESSING_CODE_010908);
			break;
		case Constants.PROCESSING_CODE_STRING_000808:
			pc.setIdprocessingcode(Constants.PROCESSING_CODE_000808);
			break;
		}
		txDetail.setProcessingcode(pc);
		txDetail.setReferencenumber(m.getString(37));
		txDetail.setResponsecode(responseCode);
		txDetail.setStoreid(storeId);
		txDetail.setTerminalid(m.getString(41));
		txDetail.setResponsecode(responseCode);
		txDetail.setAuthorizationresponse(authorizationResponse);
		txDetail.setIdtransactiondetail(pkTxDetail);
		long pk = daoTxDetail.save(txDetail);
		return pk;
	}

	/**
	 * Generates the database record related to the pin
	 * 
	 * @param pin
	 *            the pin
	 * @return the PK of the record
	 * @throws PersistenceException
	 */
	@Transactional
	private long generatePinSecurity(String pin) throws PersistenceException {
		String md5 = PinUtils.hashPin(pin);
		Pinsecurity pinSec = new Pinsecurity();
		pinSec.setActive((short) 1);
		pinSec.setPinsecurity(md5);
		long pk = daoPinSecurity.save(pinSec);
		return pk;
	}

	/**
	 * Obtains an authorization code no repeated in the given list
	 * 
	 * @param autCodes
	 *            - the list containing the aut codes
	 * @return a valid authorization code
	 */
	private String getAutCodeValid(List<String> autCodes) {
		String autCode = getAutCode();
		if (CollectionUtils.isEmpty(autCodes)) {
			return autCode;
		}
		if (autCodes.contains(autCode)) {
			getAutCodeValid(autCodes);
		}
		return autCode;
	}

	/**
	 * Obtains an authorization code of length 6
	 * 
	 * @return the aut code
	 */
	private String getAutCode() {
		long longNumber = RandomUtils.getInstance().getRandomGenerator()
				.nextLong(1, 999999);
		String longString = StringUtils.leftPad(Long.toString(longNumber), 6,
				"0");
		return longString;
	}

	/**
	 * Obtains a PIN number for withdraw purposes
	 * 
	 * @return the PIN number
	 */
	private String getPin() {
		long longPin = RandomUtils.getInstance().getRandomGenerator()
				.nextLong(1, 9999);
		String longString = StringUtils.leftPad(Long.toString(longPin), 4, "0");
		return longString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#isReversibleTransaction_Send(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isReversibleTransaction_Send(long pkDetail)
			throws ServiceException {
		try {
			long pkTransaction = daoTransaction
					.findTransctionByTransactiondetailPK(pkDetail);
			int transferStateId = daoTransaction
					.findTransferState(pkTransaction);
			if (transferStateId == 4) {
				return false; // transaction delivered to the final customer,
								// not able to be reversed
			}
			return true;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in reverseTransaction:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#findPKRootTransactionByDetailPK
	 * (long)
	 */
	@Override
	@Transactional(readOnly = true)
	public long findPKRootTransactionByDetailPK(long pkDetail)
			throws ServiceException {
		try {
			long pkTransaction = daoTransaction
					.findTransctionByTransactiondetailPK(pkDetail);
			return pkTransaction;
		} catch (PersistenceException e) {
			throw new ServiceException(
					"Error in findPKRootTransactionByDetailPK:"
							+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#cancellTransactions(long)
	 */
	@Override
	@Transactional
	public List<Transactions> cancellTransactions(long numcelsend)
			throws ServiceException {
		try {
			List<Transactions> list = daoTransaction
					.cancellAllTransactions(numcelsend);
			return list;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in cancellTransactions:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.TransactionService#cancellTransactions(long)
	 */
	@Override
	@Transactional
	public List<Transactions> findByNumCcel(long numcelsend)
			throws ServiceException {
		try {
			List<Transactions> list = daoTransaction
					.findByNumCel(numcelsend);
			return list;
		} catch (PersistenceException e) {
			throw new ServiceException("Error in cancellTransactions:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionService#reverseTransaction(org.jpos.iso
	 * .ISOMsg, long, long, java.lang.String, long)
	 */
	@Override
	@Transactional(rollbackFor = { PersistenceException.class,
			ServiceException.class })
	public void reverseTransaction(ISOMsg request, long pkTransaction,
			long pkDetail, String responseCode, long pkTxDetail)
			throws ServiceException {
		try {
			Transactions tx = daoTransaction.findByPK(pkTransaction);
			int oldTransferState = daoTransaction.findTransferState(tx
					.getIdtransactions());
			Transferstate state = new Transferstate();
			switch (oldTransferState) {
			case Constants.TRANSFER_STATE_SENT_0200:
				state.setIdtransferstate(Constants.TRANSFER_STATE_REVERSE_0420);
				updatePinSecurity(pkTransaction);
				break;
			case Constants.TRANSFER_STATE_WITHDRAW_0210:
				state.setIdtransferstate(Constants.TRANSFER_STATE_SENT_0200);
				reenablePinSecurity(pkTransaction);
				break;
			default:
				state.setIdtransferstate(Constants.TRANSFER_STATE_REVERSE_0420);
				updatePinSecurity(pkTransaction);
				break;
			}
			tx.setTransferstate(state);
			tx.setLastupdate(DateUtils.getCurrentlDate());
			daoTransaction.update(tx);
			Transactiondetail detailTx = daoTxDetail.findByPK(pkDetail);
			detailTx.setReversedtransaction((short) 1);
			detailTx.setLastupdate(DateUtils.getCurrentlDate());
			detailTx.setResponsereverse((short) 0);
			detailTx.setPindata(null);
			daoTxDetail.update(detailTx);
		} catch (PersistenceException e) {
			throw new ServiceException("Error in reverseTransaction:"
					+ e.getMessage(), e);
		}
	}

	/**
	 * Sets unable the pin security related to the transaction
	 * 
	 * @param pkTransaction
	 * @throws PersistenceException
	 */
	@Transactional
	private void updatePinSecurity(long pkTransaction)
			throws PersistenceException {
		long pinID = daoTransaction.findIdPinSecurity(pkTransaction);
		Pinsecurity pin = daoPinSecurity.findByPK(pinID);
		pin.setActive(0);
		daoPinSecurity.update(pin);
	}

	/**
	 * Re-enable the pin security related to the transaction
	 * 
	 * @param pkTransaction
	 * @throws PersistenceException
	 */
	@Transactional
	private void reenablePinSecurity(long pkTransaction)
			throws PersistenceException {
		long pinID = daoTransaction.findIdPinSecurity(pkTransaction);
		Pinsecurity pin = daoPinSecurity.findByPK(pinID);
		pin.setActive(1);
		daoPinSecurity.update(pin);
	}
}

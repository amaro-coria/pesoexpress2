/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.jpos.iso.ISODate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.PartnerDAO;
import com.peycash.persistence.dao.PartnershopDAO;
import com.peycash.persistence.dao.TransactionDetailDAO;
import com.peycash.persistence.domain.Transactiondetail;
import com.peycash.service.ServiceException;
import com.peycash.service.TransactionDetailService;

/**
 * Implementation of the service interface
 * 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class TransactionDetailServiceImpl implements TransactionDetailService {
	/**
	 * Injected spring value
	 */
	@Autowired
	private TransactionDetailDAO daoTransactionDetail;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionDetailService#existsTransactionDetail(
	 * java.lang.String, java.lang.String, java.math.BigDecimal,
	 * java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean existsTransactionDetail(String auditNumber,
			String referenceNumber, BigDecimal amount, String isoDate)
			throws ServiceException {
		int auditInt = Integer.parseInt(auditNumber);
		Date date = ISODate.parseISODate(isoDate);
		try {
			boolean exists = daoTransactionDetail.existsTransactionDetail(
					auditInt, referenceNumber, amount, date);
			return exists;
		} catch (PersistenceException e) {
			throw new ServiceException("Erorr in existsTransactionDetail:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionDetailService#existsTransactionDetailDeep
	 * (java.lang.String, java.lang.String, java.math.BigDecimal,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean existsTransactionDetailDeep(String auditNumber,
			String referenceNumber, BigDecimal amount, String terminalid,
			String partnerAffiliation, String partnershopAffiliation,
			String isoDate) throws ServiceException {
		int auditInt = Integer.parseInt(auditNumber);
		partnerAffiliation = partnerAffiliation.trim();
		partnershopAffiliation = partnershopAffiliation.trim();
		long partnerAffil = Long.parseLong(partnerAffiliation);
		long partnershopAffil = Long.parseLong(partnershopAffiliation);
		Date date = ISODate.parseISODate(isoDate);
		try {
			int idPartner = daoPartner.findIdByAffiliation(partnerAffil);
			int idPartnershop = daoPartnershop
					.findIdByAffiliation(partnershopAffil);
			boolean exists = daoTransactionDetail.existsTransactionDetailDeep(
					auditInt, referenceNumber, amount, terminalid,
					idPartnershop, idPartner, date);
			return exists;
		} catch (PersistenceException e) {
			throw new ServiceException("Erorr in existsTransactionDetailDeep:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionDetailService#findIdForExistingTransaction
	 * (java.lang.String, java.lang.String, java.math.BigDecimal,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public long findIdForExistingTransaction(String auditNumber,
			String referenceNumber, BigDecimal amount, String terminalid,
			String partnerAffiliation, String partnershopAffiliation,
			String isoDate, String authorizationresponse, String uniqueTx)
			throws ServiceException {
		int auditInt = Integer.parseInt(auditNumber);
		partnerAffiliation = partnerAffiliation.trim();
		partnershopAffiliation = partnershopAffiliation.trim();
		long partnerAffil = Long.parseLong(partnerAffiliation);
		long partnershopAffil = Long.parseLong(partnershopAffiliation);
		Date date = ISODate.parseISODate(isoDate);

		Long uniqueTxLong = null;
		if (uniqueTx != null) {
			try{
				uniqueTx = uniqueTx.trim();
				uniqueTxLong = Long.parseLong(uniqueTx);
			}catch(NumberFormatException e){
			}
		}
		try {
			int idPartner = daoPartner.findIdByAffiliation(partnerAffil);
			int idPartnershop = daoPartnershop
					.findIdByAffiliation(partnershopAffil);
			long pk = daoTransactionDetail.existsTransactionDetailDeep(
					auditInt, referenceNumber, amount, terminalid,
					idPartnershop, idPartner, date, authorizationresponse,
					uniqueTxLong);
			return pk;
		} catch (PersistenceException e) {
			throw new ServiceException("Erorr in findIdForExistingTransaction:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionDetailService#isTransactionAlreadyReversed
	 * (long)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isTransactionAlreadyReversed(long pk)
			throws ServiceException {
		try {
			Transactiondetail tx = daoTransactionDetail.findByPK(pk);
			short reversed = tx.getReversedtransaction();
			if (reversed == 1) {
				return true;
			}
			return false;
		} catch (PersistenceException e) {
			throw new ServiceException("Erorr in isTransactionAlreadyReversed:"
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.TransactionDetailService#findPreviousAutResponse(
	 * long)
	 */
	@Override
	@Transactional(readOnly = true)
	public String findPreviousAutResponse(long pk) throws ServiceException {
		try {
			String autResponse = daoTransactionDetail.findAutResponse(pk);
			return autResponse;
		} catch (PersistenceException e) {
			throw new ServiceException("Erorr in findPreviousAutResponse:"
					+ e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.TransactionDetailService#findTransactionDetailsByRoot(long)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Transactiondetail> findTransactionDetailsByRoot(long pkTransaction) throws ServiceException{
		try{
			List<Transactiondetail> listDetails = daoTransactionDetail.findTransactionDetailsByRoot(pkTransaction);
			return listDetails;
		}catch(PersistenceException e){
			throw new ServiceException("Error in findTransactionDetailsByRoot:"+e.getMessage());
		}
	}
}

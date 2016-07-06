/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.facade.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.peycash.SMSBalanceException;
import com.peycash.common.ConfigRegister;
import com.peycash.common.StatusAmounts;
import com.peycash.common.dto.LimitsDTO;
import com.peycash.common.dto.SMSProviderDTO;
import com.peycash.common.dto.SMSProviderDTO.SMSProviderStatus;
import com.peycash.common.util.AmountUtils;
import com.peycash.common.util.Constants;
import com.peycash.common.util.DateUtils;
import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.facade.ProcessingFacade;
import com.peycash.persistence.domain.Transactionlog;
import com.peycash.service.CustomerService;
import com.peycash.service.PartnerService;
import com.peycash.service.PartnershopService;
import com.peycash.service.ResponseBuilder;
import com.peycash.service.ResponseCodeService;
import com.peycash.service.SMSService;
import com.peycash.service.ServiceException;
import com.peycash.service.SystemAlertService;
import com.peycash.service.TicketTextService;
import com.peycash.service.TransactionDetailService;
import com.peycash.service.TransactionLogService;
import com.peycash.service.TransactionService;

/**
 * Implementation of the facade
 * 
 * @author Jorge Amaro
 * @since 1.0
 * @version 1.0
 * 
 *
 */
@Component
public class ProcessingFacadeImpl implements ProcessingFacade {

	private static Logger LOGGER = LoggerFactory
			.getLogger(ProcessingFacadeImpl.class);

	/**
	 * Injected spring value
	 */
	@Autowired
	private TransactionService serviceTransaction;
	/**
	 * Injected spring value
	 */
	@Autowired
	private TransactionDetailService serviceTransactionDetail;
	/**
	 * Injected spring value
	 */
	@Autowired
	private PartnerService servicePartner;
	/**
	 * Injected spring value
	 */
	@Autowired
	private PartnershopService servicePartnershop;

	/**
	 * Injected spring value
	 */
	@Autowired
	private CustomerService serviceCustomer;

	/**
	 * Injected spring value
	 */
	@Autowired
	private TransactionLogService serviceTransactionLog;

	/**
	 * Injected spring value
	 */
	@Autowired
	private SMSService serviceSMS;

	/**
	 * Injected spring value
	 */
	@Autowired
	private SystemAlertService serviceAlerts;

	/**
	 * Injected spring value
	 */
	@Autowired
	private TicketTextService serviceTicketText;

	/**
	 * Injected spring value
	 */
	@Autowired
	private ResponseBuilder serviceResponseBuilder;

	/**
	 * Injected spring value
	 */
	@Autowired
	private ResponseCodeService serviceResponseCode;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.facade.ProcessingFacade#processMessage_0200(org.jpos.iso.
	 * ISOMsg, com.peycash.common.util.ProcessingCodeEnum)
	 */
	@Override
	public ISOMsg processMessage_0200(ISOMsg request, ProcessingCodeEnum pc)
			throws ISOException, ServiceException {
		int affiliations;
		boolean isValidaDateTime;
		boolean isDuplicated;
		ISOMsg response = null;
		/* TKN_DES_A03 */
		long pkLog = saveLogRequirement(request);
		boolean end = false;
		/* TKN_DES_07 */
		affiliations = checkValidAffiliations(request);
		if (affiliations != 0) {
			response = serviceResponseBuilder
					.buildResponse0200_fail_affiliationPartner(request,
							affiliations);
			end = true;
		}
		if (!end) {
			/* TKN_DES_A03 */
			isValidaDateTime = checkDateTime(request);
			if (!isValidaDateTime) {
				response = serviceResponseBuilder
						.buildResponse0200_fail_dateTimeRange(request);
				end = true;
			}
		}
		if (!end) {
			/* TKN_DES_A03 */
			isDuplicated = isDuplicated(request);
			if (isDuplicated) {
				response = serviceResponseBuilder
						.buildResponse0200_fail_duplicity(request);
				end = true;
			}
		}
		if (!end) {
			/* TKN_DES_A05 */
			boolean isSMSProviderAvailable = checkSMSProvider();
			if (!isSMSProviderAvailable) {
				response = serviceResponseBuilder
						.buildResponse0200_fail_smsProviderNotAvailable(request);
				end = true;
			}
			/* TKN_DES_A05 */
			reserveSMS();
		}
		if (!end) {
			/* TKN_DES_06 */
			boolean isInDailyLimmits = checkTransactionLimit(request);
			if (!isInDailyLimmits) {
				response = serviceResponseBuilder
						.buildResponse0200_fail_notInTransactionLimits(request);
				end = true;
			}
		}
		if (!end) {
			customer_label: switch (pc) {
			case SEND:
				/* TKN_DES_A04 */
				StatusAmounts statusAmount = checkMonthlyAndDayliLimits_send(request);
				if (statusAmount != StatusAmounts.STATUS_OK) {
					response = serviceResponseBuilder
							.buildResponse0200_fail_limitExceeded(request,
									statusAmount);
					break;
				}
				int checkCustomerSend = checkCustomerSend(request);
				switch (checkCustomerSend) {
				case Constants.CUSTOMER_STATUS_OK:
					break;
				case Constants.CUSTOMER_STATUS_NOT_REGISTERED: // evaluates the
																// same
																// as
																// CUSTOMER_STATUS_HIGH_RISK_EVALUATION
					response = serviceResponseBuilder
							.buildResponse0200_fail_customerStatus_highRisk_notRegistered(request);
					break customer_label;
				case Constants.CUSTOMER_STATUS_BLACK_LISTED:
					response = serviceResponseBuilder
							.buildResponse0200_fail_customerStatus_blackListed(request);
					break customer_label;
				}
				/* TKN_DES_A04 */
				int checkCustomerReceive = checkCustomerReceive(request);
				switch (checkCustomerReceive) {
				case Constants.CUSTOMER_STATUS_OK:
					break;
				case Constants.CUSTOMER_STATUS_NOT_REGISTERED:
					break;
				case Constants.CUSTOMER_STATUS_BLACK_LISTED:
					response = serviceResponseBuilder
							.buildResponse0200_fail_customerStatus_blackListed(request);
					break customer_label;
				}

				/* TKN_DES_08 */
				String autCode = getAutCode_send(request);
				String pin = getPin(request);
				if (response == null) {
					response = serviceResponseBuilder.buildResponse0200_OK(
							request, autCode);
				}
				long pkTransfer = generateTransfer(request, response, pin,
						pkLog);
				sendSMS(pkTransfer, request, pin);
				break;
			case RETRIEVE:
				/* CU_02 */
				int checkCustomerReceive_retrieve = checkCustomerReceive(request);
				switch (checkCustomerReceive_retrieve) {
				case Constants.CUSTOMER_STATUS_OK:
					break;
				case Constants.CUSTOMER_STATUS_NOT_REGISTERED:
					break;
				case Constants.CUSTOMER_STATUS_BLACK_LISTED:
					response = serviceResponseBuilder
							.buildResponse0200_fail_customerStatus_blackListed(request);
					break customer_label;
				}
				StatusAmounts statusAmount_retrieve = checkMonthlyAndDayliLimits_retrieve(request);
				if (statusAmount_retrieve != StatusAmounts.STATUS_OK) {
					response = serviceResponseBuilder
							.buildResponse0200_fail_limitExceeded(request,
									statusAmount_retrieve);
					break;
				}
				boolean pendings = hasWithdrawsPending(request);
				if (!pendings) {
					response = serviceResponseBuilder
							.buildResponse0200_withdrawErrorNoPendings(request);
				}
				String autCode_retrieve = getAutCode_receive(request);
				if (response == null) {
					response = serviceResponseBuilder.buildResponse0200_OK(
							request, autCode_retrieve);
					boolean success = generateWithdraw(request, response, pkLog);
					if (!success) {
						response = serviceResponseBuilder
								.buildResponse0200_withdrawError(request);
					}
				}
				break;
			case CANCEL:
				/* TKN_DES_08 */				
				cancellPrevious(request);
				String autCodeCancel = getAutCode_send(request);
				String pinCancel = getPin(request);
				if (response == null) {
					response = serviceResponseBuilder.buildResponse0200_OK(
							request, autCodeCancel);
				}
				long pkTransferCancel = generateTransfer(request, response, pinCancel,
						pkLog);
				sendSMS(pkTransferCancel, request, pinCancel);
			break;
			}
		}
		updateLogRequirement(response, pkLog);
		return response;
	}

	private void cancellPrevious(ISOMsg m){
		try {
			String noCelSend = m.getString(76);
			long nocelsend = Long.parseLong(noCelSend);
			serviceTransaction.cancellTransactions(nocelsend);
		} catch (ServiceException e) {
			LOGGER.debug("No transaction has been cancelled");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.facade.ProcessingFacade#processMessage_0420(org.jpos.iso.
	 * ISOMsg, com.peycash.common.util.ProcessingCodeEnum)
	 */
	@Override
	public ISOMsg processMessage_0420(ISOMsg request, ProcessingCodeEnum pc)
			throws ISOException, ServiceException {
		int affiliations;
		boolean isValidaDateTime;
		boolean isDuplicated;
		ISOMsg response = null;
		/* TKN_DES_A03 */
		long pkLog = saveLogRequirement(request);
		boolean end = false;
		/* TKN_DES_07 */
		affiliations = checkValidAffiliations(request);
		if (affiliations != 0) {
			response = serviceResponseBuilder
					.buildResponse0200_fail_affiliationPartner(request,
							affiliations);
			end = true;
		}
		if (!end) {
			/* TKN_DES_A03 */
			isValidaDateTime = checkDateTime(request);
			if (!isValidaDateTime) {
				response = serviceResponseBuilder
						.buildResponse0200_fail_dateTimeRange(request);
				end = true;
			}
		}
		if (!end) {
			/* TKN_DES_A03 */
			/*
			 * has to be duplicated, because for a reverse, has to be in the
			 * system a previous 0200 record
			 */
			isDuplicated = isTransactionDuplicated(request);
			if (!isDuplicated) {
				// response =
				// serviceResponseBuilder.buildResponse0200_fail_duplicity(request);
				response = serviceResponseBuilder
						.buildResponse0420_previousRequestNotFound(request);
				end = true;
			}
		}

		if (!end) {
			switch (pc) {
			case SEND:
				response = reverseTransaction(request, pkLog, pc);
				break;
			case RETRIEVE:
				response = reverseTransaction(request, pkLog, pc);
				break;
			}
		}
		updateLogRequirement(response, pkLog);
		return response;
	}

	/**
	 * Reverses a send transaction 000809
	 * 
	 * <pre>
	 * Put status in transaction and transaction detail
	 * </pre>
	 * 
	 * <pre>
	 * Unsend the sms (if available)
	 * </pre>
	 * 
	 * @param request
	 * @param pkLog
	 * @return the ISOmsg response
	 * @throws ServiceException
	 * @throws ISOException
	 */
	private ISOMsg reverseTransaction(ISOMsg request, long pkLog,
			ProcessingCodeEnum pc) throws ServiceException, ISOException {
		long pkDetail = findIdTransactionDetailToBeReversed(request);
		boolean reversed = serviceTransactionDetail
				.isTransactionAlreadyReversed(pkDetail);
		ISOMsg response = null;
		String responseCode = request.getString(39);
		boolean supported = isSupportedResponseCode(responseCode);
		if (!supported) {
			response = serviceResponseBuilder
					.buildResponse0420_responseCodeNotSupported(request);
			return response;
		}
		if (reversed) {
			response = serviceResponseBuilder.buildResponse0420_OK(request);
			return response;
		}
		boolean isReversible = isReversibleTransaction_send(pkDetail);
		if (!isReversible) {
			response = serviceResponseBuilder
					.buildResponse0420_previousRequestNotFound(request);
			return response;
		}
		long pkTransaction = serviceTransaction
				.findPKRootTransactionByDetailPK(pkDetail);
		serviceTransaction.reverseTransaction(request, pkTransaction, pkDetail,
				responseCode, pkLog);
		response = serviceResponseBuilder.buildResponse0420_OK(request);
		switch (pc) {
		case SEND:
			serviceSMS.cancellSMS(pkTransaction);
			break;
		case RETRIEVE:
			break;
		}
		return response;
	}

	/**
	 * Checks if the query is supported as a response code
	 * 
	 * @param query
	 * @return
	 */
	private boolean isSupportedResponseCode(String query) {
		boolean supported = ConfigRegister.getInstance()
				.getListSupportedCodes().contains(query);
		return supported;
	}

	/**
	 * Find out if the current transaction is able to be reversed
	 * 
	 * @param pkDetail
	 * @return
	 * @throws ServiceException
	 */
	private boolean isReversibleTransaction_send(long pkDetail)
			throws ServiceException {
		boolean reversible = serviceTransaction
				.isReversibleTransaction_Send(pkDetail);
		return reversible;
	}

	/**
	 * Finds the pk of the detail of the transaction
	 * 
	 * @param m
	 *            the current iso message
	 * @return the pk of the root transaction
	 * @throws ServiceException
	 */
	private long findIdTransactionDetailToBeReversed(ISOMsg m)
			throws ServiceException {
		String audit = m.getString(11);
		String reference = m.getString(37);
		BigDecimal amountValue = null;
		String uniqueTx = null;
		if(m.hasField(102)){
			uniqueTx = m.getString(102);
		}
		if(m.hasField(4)){
			String amountStr = m.getString(4);/* Not present in 711 */
			String amount = AmountUtils.formatAmountString(amountStr);
			amountValue = new BigDecimal(amount);
			if(amountValue.intValue() == 0){
				amountValue = null;
			}
		}
		String terminal = m.getString(41);
		String partnerAff = m.getString(43);
		String parnershopAff = m.getString(42);
		String isoDate = m.getString(7);
		String authorizationresponse = m.getString(38);
		long pk = serviceTransactionDetail.findIdForExistingTransaction(audit,
				reference, amountValue, terminal, partnerAff, parnershopAff,
				isoDate, authorizationresponse, uniqueTx);
		return pk;
	}

	/**
	 * Sends the current sms to the queue ready to be sent by a provider
	 * 
	 * @param pkTransaction
	 *            the transaction id previously generated
	 * @param m
	 *            the iso message
	 * @param pin
	 *            the in in clear
	 */
	private void sendSMS(long pkTransaction, ISOMsg m, String pin) {
		String text = generateSMSContent(m, pin);
		serviceSMS.sendSMS(text, pkTransaction);
	}

	/**
	 * Generates the sms content ready to send
	 * 
	 * @param m
	 *            the iso message
	 * @param pin
	 *            the current pin in clear
	 * @return the text to send via sms
	 */
	private String generateSMSContent(ISOMsg m, String pin) {
		String celReceive = m.getString(76);
		String amount = m.getString(4);
		String celFormatted = serviceSMS.padCellularNumber(celReceive);
		String amountFormatted = serviceSMS.formatQuantity(amount);
		String text = serviceSMS.generateSMSContent(amountFormatted,
				celFormatted, pin);
		return text;
	}

	/**
	 * Generates all the movements related to a transfer
	 * 
	 * @param request
	 *            the iso message initial request
	 * @param response
	 *            the iso message response
	 * @param pin
	 *            the pin in clear
	 * @param pkLog
	 *            the id of the log record
	 * @return the pk of the database record
	 * @throws ServiceException
	 */
	private long generateTransfer(ISOMsg request, ISOMsg response, String pin,
			long pkLog) throws ServiceException {
		String responseCode = response.getString(39);
		String autResponse = response.getString(38);
		long pk = serviceTransaction.generateTransfer(request, pin,
				responseCode, autResponse, pkLog);
		return pk;
	}

	/**
	 * Checks if the given customer has pending withdraws
	 * 
	 * @param m
	 * @return
	 * @throws ServiceException
	 */
	private boolean hasWithdrawsPending(ISOMsg m) throws ServiceException {
		String noCel = m.getString(78);
		boolean b = serviceTransaction.hasWithdrawsPending(noCel);
		return b;
	}

	/**
	 * Generates the withdraw movements on the system
	 * 
	 * @param request
	 *            iso message instance
	 * @param response
	 *            iso message instance
	 * @param pkLog
	 *            the id of the log record
	 * @return
	 */
	private boolean generateWithdraw(ISOMsg request, ISOMsg response, long pkLog) {
		/*
		 *  String pin_ = t001.getF12();
                pin_ = StringUtils.leftPad(pin_, 8, "0");
                String pinHex = toHex(pin_);
        		byte[] pin = ISOUtil.hex2byte(pinHex);
        		request.set(52, pin);
        		*/
		String pinClear = request.getString(52);
		byte[] pin = request.getBytes(52);
		String pinHex = ISOUtil.hexString(pin);
		byte[] bytes;
		try {
			bytes = Hex.decodeHex(pinHex .toCharArray());
			pinClear = new String(bytes, "UTF8");
			pinClear = pinClear.substring(4);
		} catch (DecoderException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String noCelReceive = request.getString(78);
		String amountString = request.getString(4);
		String amountS = AmountUtils.formatAmountString(amountString);
		double amount = Double.parseDouble(amountS);
		String responseCode = response.getString(39);
		String autResponse = response.getString(38);
		boolean success = serviceTransaction.generateWithdraw(request,
				noCelReceive, pinClear, amount, responseCode, autResponse,
				pkLog);
		return success;
	}

	/**
	 * Obtains the pin in clear
	 * 
	 * @param m
	 *            the iso message
	 * @return the clear pin
	 * @throws ServiceException
	 */
	private String getPin(ISOMsg m) throws ServiceException {
		String noCelReceive = m.getString(78);
		long nocelreceive = Long.parseLong(noCelReceive);
		String pin = serviceTransaction.getPinNumber(nocelreceive);
		return pin;
	}

	/**
	 * Obtains an authorization code for 0200 MTI messages
	 * 
	 * @param m
	 * @return
	 * @throws ServiceException
	 */
	private String getAutCode_send(ISOMsg m) throws ServiceException {
		String noCelSend = m.getString(76);
		long nocelsend = Long.parseLong(noCelSend);
		String noAutCode = serviceTransaction
				.generateAutCode_deposit(nocelsend);
		return noAutCode;
	}

	/**
	 * Obtains an authorization code for 0200 MTI messages
	 * 
	 * @param m
	 * @return
	 * @throws ServiceException
	 */
	private String getAutCode_receive(ISOMsg m) throws ServiceException {
		String noCelSend = m.getString(78);
		long nocelsend = Long.parseLong(noCelSend);
		String noAutCode = serviceTransaction
				.generateAutCode_withdraw(nocelsend);
		return noAutCode;
	}

	/**
	 * Convinienve method for checkint both limits in one operation
	 * 
	 * @param m
	 *            the iso message
	 * @return an instance of {@code StatusAmounts}
	 * @throws ServiceException
	 */
	private StatusAmounts checkMonthlyAndDayliLimits_send(ISOMsg m)
			throws ServiceException {
		if(!m.hasField(76)){
			throw new ServiceException("No field 76 in SEND operation");
		}
		String amountStr = m.getString(4);
		String amountS = AmountUtils.formatAmountString(amountStr);
		double amount = Double.parseDouble(amountS);
		String celSend = m.getString(76);
		String celReceive = m.getString(78);
		long celSendNum = Long.parseLong(celSend);
		long celReceiveNum = Long.parseLong(celReceive);
		StatusAmounts status1 = checkDayliLimit_send(amount, celSendNum,
				celReceiveNum);
		if (status1 != StatusAmounts.STATUS_OK) {
			return status1;
		}
		status1 = checkMonthlyLimit_send(amount, celSendNum, celReceiveNum);
		return status1;
	}

	/**
	 * Convinienve method for checkint both limits in one operation
	 * 
	 * @param m
	 *            the iso message
	 * @return an instance of {@code StatusAmounts}
	 * @throws ServiceException
	 */
	private StatusAmounts checkMonthlyAndDayliLimits_retrieve(ISOMsg m)
			throws ServiceException {
		String amountStr = m.getString(4);
		String amountS = AmountUtils.formatAmountString(amountStr);
		double amount = Double.parseDouble(amountS);
		String celReceive = m.getString(78);
		long celReceiveNum = Long.parseLong(celReceive);
		StatusAmounts status1 = checkDayliLimit_retrieve(amount, celReceiveNum);
		if (status1 != StatusAmounts.STATUS_OK) {
			return status1;
		}
		status1 = checkMonthlyLimit_retrieve(amount, celReceiveNum);
		return status1;
	}

	/**
	 * Checks the monthly limit configured in the system
	 * 
	 * @param amount
	 * @param celSenderNum
	 * @param celReceiverNum
	 * @return an instance of {@code StatusAmounts}
	 * @throws ServiceException
	 */
	private StatusAmounts checkMonthlyLimit_send(double amount,
			long celSenderNum, long celReceiverNum) throws ServiceException {
		double amountReceived = serviceTransaction
				.findCurrentMonthlyAmountByCustomerReceiver(celReceiverNum);
		double amountQuery = amountReceived + amount;
		double amountRMAX = ConfigRegister.getInstance().getSystemLimits()
				.getReceiveMonth();
		if (amountQuery > amountRMAX) {
			return StatusAmounts.MONTHLY_LIMIT_EXCEED_BY_RECEIVER;
		}
		double amountSent = serviceTransaction
				.findCurrentMonthlyAmountByCustomerSender(celSenderNum);
		double amountQuerySent = amountSent + amount;
		double amountREMAX = ConfigRegister.getInstance().getSystemLimits()
				.getSendMonth();
		if (amountQuerySent > amountREMAX) {
			return StatusAmounts.MONTHLY_LIMIT_EXCEED_BY_SENDER;
		}
		return StatusAmounts.STATUS_OK;
	}

	/**
	 * Checks the monthly limit configured in the system
	 * 
	 * @param amount
	 * @param celReceiverNum
	 * @return
	 * @throws ServiceException
	 */
	private StatusAmounts checkMonthlyLimit_retrieve(double amount,
			long celReceiverNum) throws ServiceException {
		double amountReceived = serviceTransaction
				.findCurrentMonthlyAmountByCustomerReceiverWithdraw(celReceiverNum);
		double amountQuery = amountReceived + amount;
		double amountRMAX = ConfigRegister.getInstance().getSystemLimits()
				.getWithdrawMonth();
		if (amountQuery > amountRMAX) {
			return StatusAmounts.MONTHLY_LIMIT_EXCEED_BY_RECEIVER;
		}
		return StatusAmounts.STATUS_OK;
	}

	/**
	 * Checks the daily limit configured in the system
	 * 
	 * @param amount
	 * @param celSenderNum
	 * @param celReceiverNum
	 * @return an instance of {@code StatusAmounts}
	 * @throws ServiceException
	 */
	private StatusAmounts checkDayliLimit_send(double amount,
			long celSenderNum, long celReceiverNum) throws ServiceException {
		double amountReceived = serviceTransaction
				.findCurrentDayliAmountByCustomerReceiver(celReceiverNum);
		double amountQuery = amountReceived + amount;
		double amountRMAX = ConfigRegister.getInstance().getSystemLimits()
				.getReceiveDay();
		if (amountQuery > amountRMAX) {
			return StatusAmounts.DAYLI_LIMIT_EXCEED_BY_RECEIVER;
		}
		double amountSent = serviceTransaction
				.findCurrentDayliAmountByCustomerSender(celSenderNum);
		double amountQuerySent = amountSent + amount;
		double amountREMAX = ConfigRegister.getInstance().getSystemLimits()
				.getSendDay();
		if (amountQuerySent > amountREMAX) {
			return StatusAmounts.DAYLI_LIMIT_EXCEED_BY_SENDER;
		}
		return StatusAmounts.STATUS_OK;
	}

	/**
	 * Checks the dayli limit configured in the system
	 * 
	 * @param amount
	 * @param celSenderNum
	 * @param celReceiverNum
	 * @return an instance of {@code StatusAmounts}
	 * @throws ServiceException
	 */
	private StatusAmounts checkDayliLimit_retrieve(double amount,
			long celReceiverNum) throws ServiceException {
		double amountReceived = serviceTransaction
				.findCurrentDayliAmountByCustomerReceiverWithdraw(celReceiverNum);
		double amountQuery = amountReceived + amount;
		double amountRMAX = ConfigRegister.getInstance().getSystemLimits()
				.getWithdrawDay();
		if (amountQuery > amountRMAX) {
			return StatusAmounts.DAYLI_LIMIT_EXCEED_BY_RECEIVER;
		}
		return StatusAmounts.STATUS_OK;
	}

	/**
	 * Checks the transactions limits
	 * 
	 * @param amount
	 *            - the amount received
	 * @return true if the amount is inside the limits (inclusive). False
	 *         otherwise
	 */
	private boolean checkTransactionLimit(double amount) {
		LimitsDTO limit = ConfigRegister.getInstance().getSystemLimits();
		boolean isLess = limit.getMinTrax() <= amount;
		boolean isMore = limit.getMaxTrax() >= amount;
		boolean result = isLess && isMore;
		return result;
	}

	/**
	 * Checks the transactions limits
	 * 
	 * @param m
	 *            - the iso message containing the amount in field no. 4
	 * @return true if the amount is inside the limits (inclusive). False
	 *         otherwise
	 */
	private boolean checkTransactionLimit(ISOMsg m) {
		String amount = m.getString(4);
		String amountS = AmountUtils.formatAmountString(amount);
		double amountNumber = Double.parseDouble(amountS);
		boolean result = checkTransactionLimit(amountNumber);
		return result;
	}

	/**
	 * Reserves a SMS in order to satisfy the requirement
	 * 
	 * @throws ServiceException
	 */
	private void reserveSMS() throws ServiceException {
		try {
			serviceSMS.reserveSMS();
		} catch (SMSBalanceException e) {
			replaceSMSProvider();
		}
	}

	/**
	 * Checks the status of the current sms provider. Makes replacement if
	 * needed
	 * 
	 * @return boolean is the sms provider is available, false otherwise
	 * @throws ServiceException
	 */
	private boolean checkSMSProvider() throws ServiceException {
		SMSProviderDTO smsprovider = ConfigRegister.getInstance()
				.getSmsProvider();
		if (smsprovider.getStatus().equals(SMSProviderStatus.NOT_AVAILABLE)) {
			boolean success = replaceSMSProvider();
			return success;
		}
		return true;
	}

	/**
	 * This method has to be invoked when the current SMS Provider is not
	 * available. Replaces the current SMS Provider
	 * 
	 * @return true if the action was successful, false otherwise
	 * @throws ServiceException
	 */
	private boolean replaceSMSProvider() throws ServiceException {
		SMSProviderDTO dto = serviceSMS.findAvailableProvider();
		if (dto == null) {
			serviceAlerts.registerAlert(Constants.ERROR_CATEGORY_SMS,
					Constants.ERROR_SMS_NO_SMS_PROVIDER);
			return false;
		}
		ConfigRegister.getInstance().setSmsProvider(dto);
		return true;
	}

	/**
	 * Checks for status of the customer that will receive the transaction
	 * 
	 * @param m
	 *            the iso message
	 * @return Returns a number indicating the status:
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_OK
	 * </pre>
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_HIGH_RISK_EVALUATION
	 * </pre>
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_NOT_REGISTERED
	 * </pre>
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_OK
	 * </pre>
	 * @throws ServiceException
	 */
	private int checkCustomerReceive(ISOMsg m) throws ServiceException {
		String celNumber = m.getString(78);
		int value = checkCustomer(celNumber, false);
		return value;
	}

	/**
	 * Checks for status of the customer that originates the transaction
	 * 
	 * @param m
	 *            the iso message
	 * @return Returns a number indicating the status:
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_OK
	 * </pre>
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_HIGH_RISK_EVALUATION
	 * </pre>
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_NOT_REGISTERED
	 * </pre>
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_OK
	 * </pre>
	 * @throws ServiceException
	 */
	private int checkCustomerSend(ISOMsg m) throws ServiceException {
		String celSend = m.getString(76);
		int value = checkCustomer(celSend, true);
		return value;
	}

	/**
	 * Checks for status of the customer.
	 * 
	 * @param noCel
	 *            the noCel to check
	 * @param register
	 *            if the customer is new, the flag to pre register it in the
	 *            system
	 * @return Returns a number indicating the status:
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_OK
	 * </pre>
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_HIGH_RISK_EVALUATION
	 * </pre>
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_NOT_REGISTERED
	 * </pre>
	 * 
	 *         <pre>
	 * Constants.CUSTOMER_STATUS_OK
	 * </pre>
	 * @throws ServiceException
	 */
	private int checkCustomer(String noCel, boolean register)
			throws ServiceException {
		Long noCelLong = Long.parseLong(noCel);
		long pkCustomer = validateCustomerExistenceAndRegister(noCelLong,
				register);
		if (pkCustomer == 0) {
			return Constants.CUSTOMER_STATUS_OK;
		}
		boolean isHighRisk = checkHighRiskiness(pkCustomer);
		if (isHighRisk) {
			return Constants.CUSTOMER_STATUS_HIGH_RISK_EVALUATION;
		}
		int status = checkCustomerStatus(pkCustomer);
		if (status == 5) {
			return Constants.CUSTOMER_STATUS_BLACK_LISTED;
		} else if (status != 1) {
			return Constants.CUSTOMER_STATUS_NOT_REGISTERED;
		}
		return Constants.CUSTOMER_STATUS_OK;

	}

	/**
	 * Checks for the status of the customer
	 * 
	 * @param idCustomer
	 * @return <pre>
	 * 1 = Registered
	 * </pre>
	 * 
	 *         <pre>
	 * 2=Register pending
	 * </pre>
	 * 
	 *         <pre>
	 * 3=Temporary blocked
	 * </pre>
	 * 
	 *         <pre>
	 * 4 = PEP
	 * </pre>
	 * 
	 *         <pre>
	 * 5 = BlackListed
	 * </pre>
	 * 
	 *         .... according to the DB config.
	 * @throws ServiceException
	 */
	private int checkCustomerStatus(long idCustomer) throws ServiceException {
		int status = serviceCustomer.checkCustomerStatus(idCustomer);
		return status;
	}

	/**
	 * Evaluates the riskiness of a registered customer
	 * 
	 * @param idCustomer
	 *            - Must be a registered customer
	 * @return true if the customer is high risk and the service should be
	 *         denied, false otherwise
	 * @throws ServiceException
	 */
	private boolean checkHighRiskiness(long idCustomer) throws ServiceException {
		boolean isRisk = serviceCustomer.isCustomerRiskiness(idCustomer);
		return isRisk;
	}

	/**
	 * Validates the existence of a customer based on its cel number
	 * 
	 * @param noCel
	 *            the cel number of the customer
	 * @param register
	 *            if its new customer, the flag to pre register in the system
	 * @return the id of the customer, if no customer registered (new customer)
	 *         returns 0
	 * @throws ServiceException
	 */
	private long validateCustomerExistenceAndRegister(Long noCel,
			boolean register) throws ServiceException {
		long customerId = serviceCustomer.findExistingCustomerByCelNo(noCel);
		if (customerId == 0 && register) {
			serviceCustomer.registerNewCustomer(noCel);
		}
		return customerId;
	}

	/**
	 * Save the current requirement to log in database
	 * 
	 * @param m
	 *            the iso message
	 * @return the ID of the DB record for updating purposes
	 */
	private long saveLogRequirement(ISOMsg m) {
		Transactionlog txLog = new Transactionlog();
		txLog.setDateserver(DateUtils.getCurrentlDate());
		ISOMsg msgLog;
		try {
			msgLog = buildLogMessage(m);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			msgLog.dump(ps, "");
			String message = os.toString();
			ps.close();
			txLog.setMessagesource(message);
		} catch (ISOException e) {
			txLog.setMessagesource(m.toString());
		}
		txLog.setSource("PE CORE");
		try {
			long pk = serviceTransactionLog.saveTransaction(txLog);
			return pk;
		} catch (ServiceException e) {
			return 0;
		}
	}

	/**
	 * Updates the log
	 * 
	 * @param m
	 *            the iso message
	 * @param pk
	 *            the id of the log record
	 */
	private void updateLogRequirement(ISOMsg m, long pk) {
		ISOMsg msgLog;
		String message = "";
		try {
			msgLog = buildLogMessage(m);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			msgLog.dump(ps, "");
			message = os.toString();
			ps.close();
		} catch (ISOException e) {
			message = "";
		}
		try {
			serviceTransactionLog.updateTransaction("PE CORE", message, pk);
		} catch (ServiceException e) {
			LOGGER.error("Unable to update log requirement: {}", message);
		}
	}

	/**
	 * Builds a log message padding some fields
	 * 
	 * @param m2
	 *            the iso message instance
	 * @return an instance ready for logging
	 * @throws ISOException
	 */
	private ISOMsg buildLogMessage(ISOMsg m2) throws ISOException {
		ISOMsg m = (ISOMsg) m2.clone();
		if (m.hasField(76)) {
			String celSender = m.getString(76);
			celSender = celSender.replaceFirst("^\\d{6}", "******");
			;
			m.set(76, celSender);
		}
		if (m.hasField(78)) {
			String celSender = m.getString(78);
			celSender = celSender.replaceFirst("^\\d{6}", "******");
			;
			m.set(78, celSender);
		}
		return m;
	}

	/**
	 * Checks if the affiliations are correct
	 * 
	 * @param m
	 * @return 1 if partnerAffiliation fails, 2 if partnershop affiliation
	 *         fails, 0 otherwise
	 * @throws ServiceException
	 */
	private int checkValidAffiliations(ISOMsg m) throws ServiceException {
		String partnerAffiliation = m.getString(43);
		String partnershopAffiliation = m.getString(42);
		boolean partner = isValidPartnerAffiliation(partnerAffiliation);
		if (!partner) {
			return 1;
		}
		boolean partnershop = isValidPartnershopAffiliation(partnershopAffiliation);
		if (!partnershop) {
			return 2;
		}
		return 0;
	}

	/**
	 * Checks if the given affiliation is correct
	 * 
	 * @param partnerAffil
	 * @return true if parameter is correct
	 * @throws ServiceException
	 */
	private boolean isValidPartnerAffiliation(String partnerAffil)
			throws ServiceException {
		boolean isValid = servicePartner.isValidAffiliation(partnerAffil);
		return isValid;
	}

	/**
	 * Checks if the given affiliation is correct
	 * 
	 * @param partnershopAffil
	 * @return true if parameter is correct
	 * @throws ServiceException
	 */
	private boolean isValidPartnershopAffiliation(String partnershopAffil)
			throws ServiceException {
		boolean isValid = servicePartnershop
				.isValidAffiliation(partnershopAffil);
		return isValid;
	}

	/**
	 * Checks if the date and time received are correct within the application
	 * configurations limits
	 * 
	 * @param m
	 * @return
	 * @throws ISOException
	 */
	private boolean checkDateTime(ISOMsg m) throws ISOException {
		String dateTime = m.getString(7);
		String date = m.getString(13);
		String time = m.getString(12);
		Date isoDate = ISODate.parseISODate(dateTime);
		boolean isDate;
		boolean isTime;
		boolean isAcceptable = DateUtils.validateDateTimeRange(isoDate);
		if (isAcceptable) {
			isDate = dateTime.contains(date);
			isTime = dateTime.contains(time);
			boolean isDateTime = isDate && isTime;
			return isDateTime;
		}
		return false;
	}

	/**
	 * Check if a transaction is duplicated. Encapsulates all the filter levels
	 * offering a single entrance for business
	 * 
	 * @param m
	 * @return true if the given transaction is previously registered
	 * @throws ISOException
	 * @throws ServiceException
	 */
	private boolean isDuplicated(ISOMsg m) throws ISOException,
			ServiceException {
		boolean previousRecords = hasPreviousRecords(m);
		if (!previousRecords) {
			return false;
		}
		boolean isDuplicated = isTransactionDuplicated(m);
		return isDuplicated;
	}

	/**
	 * Checks if a transaction has previous records or similar records. Serves
	 * as first filter
	 * 
	 * @param m
	 * @return
	 * @throws ISOException
	 * @throws ServiceException
	 */
	private boolean hasPreviousRecords(ISOMsg m) throws ISOException,
			ServiceException {
		String celSender = m.getString(76);
		String celReceiver = m.getString(78);
		String amountStr = m.getString(4);
		String amount = AmountUtils.formatAmountString(amountStr);
		BigDecimal amountValue = new BigDecimal(amount);
		boolean existsTransactions = serviceTransaction.existsTransaction(
				celSender, celReceiver, amountValue);
		return existsTransactions;
	}

	/**
	 * Checks if a transaction has been registered
	 * 
	 * @param m
	 * @return true if a transaction exists
	 * @throws ISOException
	 * @throws ServiceException
	 */
	private boolean isTransactionDuplicated(ISOMsg m) throws ISOException,
			ServiceException {
		String audit = m.getString(11);
		String reference = m.getString(37);
		String amountStr = m.getString(4);
		String amount = AmountUtils.formatAmountString(amountStr);
		BigDecimal amountValue = new BigDecimal(amount);
		String terminal = m.getString(41);
		String partnerAff = m.getString(43);
		String parnershopAff = m.getString(42);
		String isoDate = m.getString(7);
		boolean foundFirstLevel = serviceTransactionDetail
				.existsTransactionDetail(audit, reference, amountValue, isoDate);
		if (foundFirstLevel) {
			return true;
		}
		boolean foundSecondLevel = serviceTransactionDetail
				.existsTransactionDetailDeep(audit, reference, amountValue,
						terminal, partnerAff, parnershopAff, isoDate);
		return foundSecondLevel;

	}

}

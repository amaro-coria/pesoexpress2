/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import java.util.List;

import com.peycash.SMSBalanceException;
import com.peycash.common.dto.SMSProviderDTO;
import com.peycash.persistence.domain.Smsstatussend;

/**
 * Interface for accesing services related to SMS
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface SMSService {

	/**
	 * Finds the first SMS provider available. 
	 * @return the Configuration encapsulated in a DTO. <b>May return null</b>
	 */
	SMSProviderDTO findAvailableProvider();

	/**
	 * Registers the configuration in singleton {@code ConfigRegister}
	 * @param dto - the configuration to be registered
	 */
	void registerAvailableProvider(SMSProviderDTO dto);

	/**
	 * Reserves an SMS in the system
	 * @throws SMSBalanceException
	 */
	void reserveSMS() throws SMSBalanceException;

	/**
	 * Refund the SMS to the system
	 */
	void refundSMS();

	/**
	 * Sends an SMS with the give text
	 * @param ticketText
	 * @param idTransaction
	 */
	void sendSMS(String ticketText, long idTransaction);

	/**
	 * Finds the current sms text template
	 * @return the template text
	 */
	String findSMSTemplate();

	/**
	 * Register the sms text template in {@code ConfigRegister}
	 * @param text
	 */
	void registerSMSTemplateText(String text);

	/**
	 * Generates the SMS text to send based on a template pre-registered on the system
	 * @param quantity the quantity to send
	 * @param paddedNumberSend the padded number like XXXXXXX1234
	 * @param pinClear the pin in clear like 1234
	 * @return the text to send via SMS
	 */
	String generateSMSContent(String quantity, String paddedNumberSend,
			String pinClear);

	/**
	 * Formats the string received in ISOMsg format like 000020050
	 * @param quantity the quantity in iso format
	 * @return the quantity in format like 200.50
	 */
	String formatQuantity(String quantity);

	/**
	 * Formats the string received with X character
	 * @param celnumber the cel number like 5512345678
	 * @return the cel number like XXXXXX5678
	 */
	String padCellularNumber(String celnumber);

	/**
	 * Finds the list with the sms status send records
	 * @return the list with all the records
	 * @throws ServiceException
	 */
	List<Smsstatussend> findSmsStatusSend() throws ServiceException;

	/**
	 * Attempts to cancel an sms
	 * @param idTransactionRoot
	 */
	void cancellSMS(long idTransactionRoot);

}

/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.facade;

import org.jpos.iso.ISOMsg;

import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.common.util.ProcessingCodeException;

/**
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 * Facade for ISOMsg validation purposes 
 *
 */
public interface ValidationFacade {

	/**
	 * Check if the ISOMsf complain business rules in its fields and if it's
	 * well formed
	 * 
	 * @param msg
	 * @param mti
	 * @return Returns 0 if there is an error. Otherwise return the number of the field that presents the category of the message like 2 for 0200 messages
	 */
	public int isValidMTI(String mti);
	
	/**
	 * Check if the MTI is supported
	 * 
	 * @param mti
	 * @return Returns 0 if the message is not supported. Otherwise return 2 for 0200 and 0210 messages, 4 for 0420, 0421 and 0430 messages and so on.
	 */
	public int validateFields(ISOMsg msg, String mti);

	/**
	 * Checks if the processing code is recognized by the system 
	 * @param processingCode
	 * @return
	 */
	boolean isValidProcessingCode(String processingCode);

	/**
	 * Checks if the recognized processing code is assigned for a process in the system. Throws an exception if there is no service set for that processing code
	 * @param processingCode
	 * @return
	 * @throws ProcessingCodeException
	 */
	ProcessingCodeEnum findTypeOfService(String processingCode)
			throws ProcessingCodeException;
}

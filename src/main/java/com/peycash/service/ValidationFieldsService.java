/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import org.jpos.iso.ISOMsg;

/**
 * Interface for services for validating fields 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface ValidationFieldsService {


	/**
	 * Check if the ISOMsf complain business rules in its fields and if it's
	 * well formed
	 * 
	 * @param msg
	 * @param mti
	 * @return Returns 0 if there is an error. Otherwise return the number that represents the message category like 2 for 0200 messages
	 */
	public int isValidMTI(String mti);

	/**
	 * Check if the MTI is supported
	 * 
	 * @param mti
	 * @return Returns 0 if the message is not supported. Otherwise return 2 for 0200 and 0210 messages, 4 for 0420, 0421 and 0430 messages and so on.
	 */
	public int validateFields(ISOMsg msg, String mti);

}

/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.common.util.ProcessingCodeException;

/**
 * Interface for validation of processing codes
 * 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface ValidationPCService {

	/**
	 * Checks if the process code is recognized by the system
	 * @param processingCode
	 * @return
	 */
	boolean isValidProcessingCode(String processingCode);

	/**
	 * Gets the service associated with specified processing code. Throws an exception if no service is associated with recognized processing code
	 * @param processingCode
	 * @return
	 * @throws ProcessingCodeException
	 */
	ProcessingCodeEnum findTypeOfPC(String processingCode)
			throws ProcessingCodeException;

}

package com.peycash.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessingCodeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ProcessingCodeException.class);
	
	/**
	 * Constructor for Exception with processing code parameter
	 * @param processingCode
	 */
	public ProcessingCodeException(String processingCode) {
		LOGGER.error("Processing code not supported: [{}]", processingCode);
	}
	
}

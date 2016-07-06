/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an exception that may be thrown across the initial configuration of the application
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public class ConfigurationException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER  = LoggerFactory.getLogger(ConfigurationException.class);

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
		LOGGER.error(message, cause);
	}
	
	public ConfigurationException(String msg) {
		super(msg);
		LOGGER.error(msg);
	}


}

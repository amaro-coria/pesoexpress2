/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception for services tier
 * @author Jorge Amaro
 *
 */
public class ServiceException extends Exception{

	/**
     * Serial
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceException.class);

    
    public ServiceException() {
        super();
        LOGGER.error("Error in service tier");
    }

    public ServiceException(String msg) {
        super(msg);
        LOGGER.error(msg);
    }

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
        LOGGER.error(msg, cause);
    }
}

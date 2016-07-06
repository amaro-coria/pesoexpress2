/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.facade;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.service.ServiceException;

/**
 * Facade for processing ISO8583 messages
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface ProcessingFacade {

	/**
	 * Processes a 0200 ISO8583 message
	 * @param m
	 * @param pc
	 * @return the instance to be answered to the client
	 * @throws ISOException
	 * @throws ServiceException
	 */
	ISOMsg processMessage_0200(ISOMsg m, ProcessingCodeEnum pc)
			throws ISOException, ServiceException;

	ISOMsg processMessage_0420(ISOMsg request, ProcessingCodeEnum pc)
			throws ISOException, ServiceException;


}

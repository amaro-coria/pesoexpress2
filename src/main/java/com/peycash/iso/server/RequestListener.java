/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.iso.server;

import java.io.IOException;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peycash.PesoExpressContext;
import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.common.util.ProcessingCodeException;
import com.peycash.facade.ProcessingFacade;
import com.peycash.facade.ValidationFacade;
import com.peycash.service.ResponseBuilder;
import com.peycash.service.ServiceException;

/**
 * Class for working with received messages through JPOS channels. This class
 * should be registered with in an xml inside the deploy directory
 * 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 * 
 */
public class RequestListener implements ISORequestListener {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(RequestListener.class);

	/**
	 * Value to be retrieved by spring context
	 */
	private ValidationFacade facadeValidation;

	/**
	 * Value to be retrieved by spring context
	 */
	private ProcessingFacade facadeProcessing;

	/**
	 * Value to be retrieved by spring context
	 */
	private ResponseBuilder serviceResponseBuilder;

	/**
	 * Default no-arg constructor
	 */
	public RequestListener() {
		super();
		facadeValidation = PesoExpressContext.getValidationFacade();
		facadeProcessing = PesoExpressContext.getProcessingFacade();
		serviceResponseBuilder = PesoExpressContext.getResponseBuilder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.ISORequestListener#process(org.jpos.iso.ISOSource,
	 * org.jpos.iso.ISOMsg)
	 */
	public boolean process(ISOSource source, ISOMsg m) {
		LOGGER.debug("Incoming message");
		ISOMsg response = null;
		try {
			/* TKN_DES_A02 */
			int mtiCase = isValidMTI(m.getMTI());
			int validation = validateFields(m, m.getMTI());
			/* TKN_DES_A02 */
			if (validation != 0) {
				response = buildValidationFailMsg(m);
			}
			switch (mtiCase) {
			case 0:
				break;
			case 2:
				/* MTI 02XX */
				/* TKN_DES_A02 */
				if(response == null){
					ProcessingCodeEnum pc = findTypeOfService(m.getValue(3)
							.toString());
					response = processRequest_0200(m, pc);
				}
				break;
			/* MTI 04XX */

			case 4:
				if(response == null){
					ProcessingCodeEnum pc = findTypeOfService(m.getValue(3)
							.toString());
					response = processRequest_0400(m, pc);
				}
				break;
			case 8:
				m.setResponseMTI();
				m.set(3, "001122");
				m.set(39, "00");
				m.set(40, "123");
				m.set(4, "48383");
				break;
			default:
				break;
			}
		} catch (ProcessingCodeException pe) {
			response = buildProcessingCodeFailMsg(m);
		} catch (ServiceException se) {
			response = buildGeneralErrorFailMsg(m);
		} catch (Exception e) {
			response = buildGeneralErrorFailMsg(m);
		}
		try {
			source.send(response);
		} catch (IOException | ISOException e) {
			LOGGER.error("Unable to send response to customer", e);
			return false;
		}
		LOGGER.debug("Dispatched message");
		return true;
	}

	/**
	 * Builds a response for general failure purposes
	 * 
	 * @param source
	 * @return
	 */
	private ISOMsg buildGeneralErrorFailMsg(ISOMsg source) {
		ISOMsg response;
		try {
			response = serviceResponseBuilder
					.buildResponse0200_generalFaiulre(source);
		} catch (ISOException e) {
			return null;
		}
		return response;
	}

	/**
	 * Builds a response for errors when processing code is no supported
	 * 
	 * @param source
	 * @return
	 */
	private ISOMsg buildProcessingCodeFailMsg(ISOMsg source) {
		ISOMsg response = null;
		try {
			response = serviceResponseBuilder
					.buildResponse0200_processingCodeNotSupported(source);
		} catch (ISOException e) {
			return null;
		}
		return response;
	}

	/**
	 * Builds a response for errors when validation on fields fails
	 * 
	 * @param source
	 * @return
	 * @throws ISOException
	 */
	private ISOMsg buildValidationFailMsg(ISOMsg source) throws ISOException {
		ISOMsg response = serviceResponseBuilder
				.buildResponse0200_validationFieldsFailure(source);
		return response;
	}

	/**
	 * Process the current request
	 * 
	 * @param m
	 * @param pc
	 * @return
	 * @throws ISOException
	 * @throws ServiceException
	 */
	private ISOMsg processRequest_0200(ISOMsg m, ProcessingCodeEnum pc)
			throws ISOException, ServiceException {
		ISOMsg response = facadeProcessing.processMessage_0200(m, pc);
		return response;
	}
	
	/**
	 * Process the current request
	 * 
	 * @param m
	 * @param pc
	 * @return
	 * @throws ISOException
	 * @throws ServiceException
	 */
	private ISOMsg processRequest_0400(ISOMsg m, ProcessingCodeEnum pc) throws ISOException, ServiceException{
		ISOMsg response = facadeProcessing.processMessage_0420(m, pc);
		return response;
	}

	/**
	 * Find the type of service requested
	 * 
	 * @param processingCode
	 * @return
	 * @throws ProcessingCodeException
	 */
	private ProcessingCodeEnum findTypeOfService(String processingCode)
			throws ProcessingCodeException {
		boolean b = facadeValidation.isValidProcessingCode(processingCode);
		if (!b) {
			throw new ProcessingCodeException(processingCode);
		}
		ProcessingCodeEnum pc = facadeValidation
				.findTypeOfService(processingCode);
		return pc;
	}

	/**
	 * Validates the message through {@code ValidationFacade}
	 * 
	 * @param m
	 * @param mti
	 * @return
	 */
	private int validateFields(ISOMsg m, String mti) {
		int returnValue = facadeValidation.validateFields(m, mti);
		return returnValue;
	}

	/**
	 * Validates the MTI through {@code ValidationFacade}
	 * 
	 * @param mti
	 * @return
	 */
	private int isValidMTI(String mti) {
		int returnValue = facadeValidation.isValidMTI(mti);
		return returnValue;
	}

}

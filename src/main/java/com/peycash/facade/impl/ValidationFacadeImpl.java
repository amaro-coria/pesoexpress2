/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.facade.impl;

import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.common.util.ProcessingCodeException;
import com.peycash.facade.ValidationFacade;
import com.peycash.service.ValidationFieldsService;
import com.peycash.service.ValidationPCService;

/**
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 * Implementation of the Facade for validating services
 *
 */
@Component
public class ValidationFacadeImpl implements ValidationFacade{

	/**
	 * Injected Spring value
	 */
	@Autowired
	private ValidationFieldsService serviceFields;
	/**
	 * Injected Spring value
	 */
	@Autowired
	private ValidationPCService servicePC;
	
	/* (non-Javadoc)
	 * @see com.peycash.facade.ValidationFacade#isValidMTI(java.lang.String)
	 */
	@Override
	public int isValidMTI(String mti) {
		int returnValue = serviceFields.isValidMTI(mti);
		return returnValue;
	}

	/* (non-Javadoc)
	 * @see com.peycash.facade.ValidationFacade#validateFields(org.jpos.iso.ISOMsg, java.lang.String)
	 */
	@Override
	public int validateFields(ISOMsg msg, String mti) {
		int returnValue = serviceFields.validateFields(msg, mti);
		return returnValue;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.facade.ValidationFacade#isValidProcessingCode(java.lang.String)
	 */
	@Override
	public boolean isValidProcessingCode(String processingCode){
		boolean b = servicePC.isValidProcessingCode(processingCode);
		return b;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.facade.ValidationFacade#findTypeOfService(java.lang.String)
	 */
	@Override
	public ProcessingCodeEnum findTypeOfService(String processingCode) throws ProcessingCodeException{
		ProcessingCodeEnum pc = servicePC.findTypeOfPC(processingCode);
		return pc;
	}

}

/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import org.springframework.stereotype.Service;

import com.peycash.common.ConfigRegister;
import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.common.util.ProcessingCodeException;
import com.peycash.service.ValidationPCService;

/**
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 * Implementation for validation of processing codes
 *
 */
@Service
public class ValidationPCServiceImpl implements ValidationPCService{
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ValidationPCService#isValidProcessingCode(java.lang.String)
	 */
	@Override
	public boolean isValidProcessingCode(String processingCode){
		boolean b = ConfigRegister.getInstance().getProcessingCodes().contains(processingCode);
		return b;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ValidationPCService#findTypeOfPC(java.lang.String)
	 */
	@Override
	public ProcessingCodeEnum findTypeOfPC(String processingCode) throws ProcessingCodeException{
		ProcessingCodeEnum pc = null;
		try{
			pc = ProcessingCodeEnum.findService(processingCode);
		}catch(IllegalArgumentException ie){
			throw new ProcessingCodeException(processingCode);
		}
		return pc;
	}
	

}

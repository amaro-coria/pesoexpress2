/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.common.util;

/**
 * Enum for identifying the processing codes
 * 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public enum ProcessingCodeEnum {
	
	/**
	 * Processing code for sending services
	 */
	SEND("000809"), 
	/**
	 * Processing code for retrieval services
	 */
	RETRIEVE("010908"),
	/**
	 * Processing code for cancel services 
	 */
	CANCEL("000808");

	/**
	 * Private constructor for enum
	 * @param processingCode
	 */
	private ProcessingCodeEnum(String processingCode){
		this.processingCode = processingCode;
	}
	
	private String processingCode;
	
	/**
	 * Retrieves the processing code element by its value. If the processing code is not recognized throws an exception
	 * @param processingCode
	 * @return
	 * @throws ProcessingCodeException
	 */
	public static ProcessingCodeEnum findService(String processingCode) throws ProcessingCodeException{
		if(SEND.getProcessingCode().equals(processingCode)){
			return SEND;
		}else if(RETRIEVE.getProcessingCode().equals(processingCode)){
			return RETRIEVE;
		}else if(CANCEL.getProcessingCode().equals(processingCode)){
			return CANCEL;
		}else{
			throw new ProcessingCodeException(processingCode);
		}
	}

	/**
	 * @return the processingCode
	 */
	public String getProcessingCode() {
		return processingCode;
	}

	/**
	 * @param processingCode the processingCode to set
	 */
	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}
}

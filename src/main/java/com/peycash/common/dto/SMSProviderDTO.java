/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.common.dto;

import java.io.Serializable;

/**
 * DTO class for configuration about SMS Provider and status
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public class SMSProviderDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idSMSProvider;
	private long smsBalance;
	private String providerName;
	private SMSProviderStatus status;
	
	
	/**
	 * Enum for status of the SMS provider
	 * @author Jorge Amaro
	 * @version 1.0
	 * @since 1.0
	 *
	 */
	public enum SMSProviderStatus{
		AVAILABLE(1), NOT_AVAILABLE(2);
		
		private SMSProviderStatus(int idStatus){
			status = idStatus;
		}
		private int status;
		/**
		 * @return the status
		 */
		public int getStatus() {
			return status;
		}
		/**
		 * @param status the status to set
		 */
		public void setStatus(int status) {
			this.status = status;
		}
	}


	/**
	 * @return the idSMSProvider
	 */
	public int getIdSMSProvider() {
		return idSMSProvider;
	}


	/**
	 * @param idSMSProvider the idSMSProvider to set
	 */
	public void setIdSMSProvider(int idSMSProvider) {
		this.idSMSProvider = idSMSProvider;
	}


	/**
	 * @return the smsBalance
	 */
	public long getSmsBalance() {
		return smsBalance;
	}


	/**
	 * @param smsBalance the smsBalance to set
	 */
	public void setSmsBalance(long smsBalance) {
		this.smsBalance = smsBalance;
	}


	/**
	 * @return the providerName
	 */
	public String getProviderName() {
		return providerName;
	}


	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}


	/**
	 * @return the status
	 */
	public SMSProviderStatus getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(SMSProviderStatus status) {
		this.status = status;
	}
}

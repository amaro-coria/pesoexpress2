/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.common;

import java.util.List;
import java.util.Map;

import com.peycash.common.dto.ConfigDTO;
import com.peycash.common.dto.LimitsDTO;
import com.peycash.common.dto.SMSProviderDTO;
import com.peycash.persistence.domain.Fields;

/**
 * Singleton class for registering configuration values
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public class ConfigRegister {
	
	/**
	 * Singleton instance
	 */
	private static ConfigRegister instance = new ConfigRegister();
	/**
	 * DTO value for main configuration values
	 */
	private ConfigDTO configDTO;
	/**
	 * List of the message types supported by the application
	 */
	private List<String> messageTypes;
	/**
	 * Deploy directory for Q2 server instance
	 */
	private String deployDir;
	
	/**
	 * Holds a collection of MTI - Fields
	 */
	private Map<String, List<Fields>> fieldMap;
	
	/**
	 * Holds all the supported processing codes
	 */
	private List<String> processingCodes;
	
	/**
	 * Holds the configuration for current SMS Provider
	 */
	private SMSProviderDTO smsProvider;
	
	/**
	 * Holds the limits of quantities to receive/send through the system
	 */
	private LimitsDTO systemLimits;
	
	/**
	 * Holds the current SMS Text template
	 */
	private String smsTextTemplate;
	
	/**
	 * Holds the ticket text to send
	 */
	private String ticketText;
	
	/**
	 * Holds the list of supported response codes
	 */
	private List<String> listSupportedCodes;
	
	private ConfigRegister(){
	}

	/**
	 * @return the instance of the singleton class
	 */
	public static ConfigRegister getInstance(){
		return instance;
	}

	/**
	 * @return the configDTO
	 */
	public ConfigDTO getConfigDTO() {
		return configDTO;
	}

	/**
	 * @param configDTO the configDTO to set
	 */
	public void setConfigDTO(ConfigDTO configDTO) {
		this.configDTO = configDTO;
	}

	/**
	 * @return the messageTypes
	 */
	public List<String> getMessageTypes() {
		return messageTypes;
	}

	/**
	 * @param messageTypes the messageTypes to set
	 */
	public void setMessageTypes(List<String> messageTypes) {
		this.messageTypes = messageTypes;
	}

	/**
	 * @return the deployDir
	 */
	public String getDeployDir() {
		return deployDir;
	}

	/**
	 * @param deployDir the deployDir to set
	 */
	public void setDeployDir(String deployDir) {
		this.deployDir = deployDir;
	}

	/**
	 * @return the fieldMap
	 */
	public Map<String, List<Fields>> getFieldMap() {
		return fieldMap;
	}

	/**
	 * @param fieldMap the fieldMap to set
	 */
	public void setFieldMap(Map<String, List<Fields>> fieldMap) {
		this.fieldMap = fieldMap;
	}

	/**
	 * @return the processingCodes
	 */
	public List<String> getProcessingCodes() {
		return processingCodes;
	}

	/**
	 * @param processingCodes the processingCodes to set
	 */
	public void setProcessingCodes(List<String> processingCodes) {
		this.processingCodes = processingCodes;
	}

	/**
	 * @return the smsProvider
	 */
	public SMSProviderDTO getSmsProvider() {
		return smsProvider;
	}

	/**
	 * @param smsProvider the smsProvider to set
	 */
	public void setSmsProvider(SMSProviderDTO smsProvider) {
		this.smsProvider = smsProvider;
	}

	/**
	 * @return the systemLimits
	 */
	public LimitsDTO getSystemLimits() {
		return systemLimits;
	}

	/**
	 * @param systemLimits the systemLimits to set
	 */
	public void setSystemLimits(LimitsDTO systemLimits) {
		this.systemLimits = systemLimits;
	}

	/**
	 * @return the smsTextTemplate
	 */
	public String getSmsTextTemplate() {
		return smsTextTemplate;
	}

	/**
	 * @param smsTextTemplate the smsTextTemplate to set
	 */
	public void setSmsTextTemplate(String smsTextTemplate) {
		this.smsTextTemplate = smsTextTemplate;
	}

	/**
	 * @return the ticketText
	 */
	public String getTicketText() {
		return ticketText;
	}

	/**
	 * @param ticketText the ticketText to set
	 */
	public void setTicketText(String ticketText) {
		this.ticketText = ticketText;
	}

	/**
	 * @return the listSupportedCodes
	 */
	public List<String> getListSupportedCodes() {
		return listSupportedCodes;
	}

	/**
	 * @param listSupportedCodes the listSupportedCodes to set
	 */
	public void setListSupportedCodes(List<String> listSupportedCodes) {
		this.listSupportedCodes = listSupportedCodes;
	}
	
}

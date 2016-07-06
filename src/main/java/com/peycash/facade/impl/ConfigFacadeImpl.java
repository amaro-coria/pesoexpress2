/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.facade.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.peycash.ConfigurationException;
import com.peycash.common.ConfigRegister;
import com.peycash.common.dto.ConfigDTO;
import com.peycash.common.dto.SMSProviderDTO;
import com.peycash.facade.ConfigFacade;
import com.peycash.persistence.domain.Customerriskiness;
import com.peycash.persistence.domain.Customerstate;
import com.peycash.persistence.domain.Fields;
import com.peycash.persistence.domain.Limits;
import com.peycash.persistence.domain.Messagetype;
import com.peycash.persistence.domain.Smsstatussend;
import com.peycash.service.ConfigService;
import com.peycash.service.CustomerService;
import com.peycash.service.DeployDirService;
import com.peycash.service.FieldService;
import com.peycash.service.LimitService;
import com.peycash.service.MessageTypeService;
import com.peycash.service.ProcessingCodeService;
import com.peycash.service.ResponseCodeService;
import com.peycash.service.SMSService;
import com.peycash.service.ServiceException;
import com.peycash.service.TicketTextService;

/**
 * Implementation of the facade.
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Component
public class ConfigFacadeImpl implements ConfigFacade{
	
	/**
	 * Injected spring value
	 */
	@Autowired
	private DeployDirService serviceDeployDir;
	/**
	 * Injected spring value
	 */
	@Autowired
	private MessageTypeService serviceMessageType;
	/**
	 * Injected spring value
	 */
	@Autowired
	private ConfigService serviceConfig;
	/**
	 * Injected spring value
	 */
	@Autowired
	private FieldService serviceField;
	/**
	 * Injected spring value
	 */
	@Autowired
	private ProcessingCodeService serviceProcessingCode;
	/**
	 * Injected spring value
	 */
	@Autowired
	private SMSService serviceSMS;
	
	/**
	 * Injected spring value
	 */
	@Autowired
	private LimitService serviceLimits;
	
	/**
	 * Injected spring value
	 */
	@Autowired
	private TicketTextService serviceTicketText;
	
	/**
	 * Injected spring value
	 */
	@Autowired
	private CustomerService serviceCustomer;
	
	/**
	 * Injected spring value
	 */
	@Autowired
	private ResponseCodeService serviceResponseCodes;
	
	
	/* (non-Javadoc)
	 * @see com.peycash.facade.ConfigFacade#initConfig()
	 */
	@Override
	public void initConfig() throws ConfigurationException{
		/* TKN_DES_A01 */
		readAndRegisterMessageTypes();
		readAndRegisterDeployDir();
		readAndRegisterActiveConfig();
		readAndRegisterFields();
		readAndRegisterProcessingCodes();
		readAndRegisterSMSProviderConfig();
		readAndRegisterSystemLimits();
		readAndRegisterSmsTextTemplate();
		checkCustomerRiskinessExistence();
		checkCustomeStatesExistence();
		checkSMSStatusSendExistence();
		readAndRegisterTicketText();
		readAndRegisterResponseCodes();
	}
	
	/**
	 * Checks for the supported response codes
	 * @throws ConfigurationException
	 */
	private void readAndRegisterResponseCodes() throws ConfigurationException{
		try {
			List<String> supportedCodes = serviceResponseCodes.findSuppportedCodes();
			if(CollectionUtils.isEmpty(supportedCodes)){
				throw new ConfigurationException("No response codes registered in the system");
			}
			serviceResponseCodes.registerSupportedCodes(supportedCodes);
		} catch (ServiceException e) {
			throw new ConfigurationException("Problems finding supported response codes");
		}
		
	}
	
	/**
	 * Checks the text template for sending ticket responses to the customer
	 * @throws ConfigurationException
	 */
	private void readAndRegisterTicketText() throws ConfigurationException{
		String text = serviceTicketText.getTicketText();
		if(text == null || text.isEmpty()){
			throw new ConfigurationException("No ticket text found");
		}
		serviceTicketText.registerTicketText(text);
	}
	
	/**
	 * Checks the catalog for SMS Send status
	 * @throws ConfigurationException
	 */
	private void checkSMSStatusSendExistence() throws ConfigurationException{
		try {
			List<Smsstatussend> list = serviceSMS.findSmsStatusSend();
			if(CollectionUtils.isEmpty(list)){
				throw new ConfigurationException("No Sms status send found");
			}
		} catch (ServiceException e) {
			throw new ConfigurationException("Problem when looking for Sms status send");
		}
	}
	
	/**
	 * Checks the <b>required</b> existence of the customer states
	 * @throws ConfigurationException
	 */
	private void checkCustomeStatesExistence() throws ConfigurationException{
		try{
			List<Customerstate> list = serviceCustomer.findCustomerStates();
			if(CollectionUtils.isEmpty(list)){
				throw new ConfigurationException("No Customerstates found");
			}
		}catch (ServiceException e) {
			throw new ConfigurationException("Problem when looking for Customerstates");
		}
	}
	
	/**
	 * Checks the <b>required</b> existence of the customer riskiness
	 * @throws ConfigurationException
	 */
	private void checkCustomerRiskinessExistence() throws ConfigurationException{
		try {
			List<Customerriskiness> list = serviceCustomer.findCustomerRiskiness();
			if(CollectionUtils.isEmpty(list)){
				throw new ConfigurationException("No Customerriskiness found");
			}
		} catch (ServiceException e) {
			throw new ConfigurationException("Problem when looking for Customerriskiness");
		}
	}
	
	/**
	 * Reads and register the sms text template 
	 * @throws ConfigurationException
	 */
	private void readAndRegisterSmsTextTemplate() throws ConfigurationException{
		String templateText = serviceSMS.findSMSTemplate();
		if(templateText == null || templateText.isEmpty()){
			throw new ConfigurationException("No SMS Text template found");
		}
		serviceSMS.registerSMSTemplateText(templateText);
	}
	
	/**
	 * Reads and register the system limits for financial transactions
	 * @throws ConfigurationException
	 */
	private void readAndRegisterSystemLimits() throws ConfigurationException{
		Limits limit = serviceLimits.findActiveLimits();
		if(limit == null){
			throw new ConfigurationException("No limits in the system are configured");
		}
		serviceLimits.registerLimits(limit);
	}
	
	/**
	 * Reads and registers the configuration for SMS provider
	 * @throws ConfigurationException
	 */
	private void readAndRegisterSMSProviderConfig() throws ConfigurationException{
		SMSProviderDTO dto = serviceSMS.findAvailableProvider();
		if(dto == null){
			throw new ConfigurationException("SMS Provider not found");
		}
		if(dto.getStatus().equals(SMSProviderDTO.SMSProviderStatus.NOT_AVAILABLE)){
			throw new ConfigurationException("SMS Provider not available::"+dto.getIdSMSProvider());
		}
		serviceSMS.registerAvailableProvider(dto);
	}
	
	/**
	 * Reads the Processing codes and register them in singleton {@code ConfigRegister}
	 * @throws ConfigurationException
	 */
	private void readAndRegisterProcessingCodes() throws ConfigurationException{
		List<String> list = serviceProcessingCode.readProcessingCodes();
		if(CollectionUtils.isEmpty(list)){
			throw new ConfigurationException("Processing codes not found");
		}
		serviceProcessingCode.registerProcessingCodes(list);
	}
	
	/**
	 * Reads the message types and register in singleton {@code ConfigRegister}
	 * @see ConfigRegister
	 * @throws ConfigurationException 
	 */
	private void readAndRegisterMessageTypes() throws ConfigurationException{
		List<Messagetype> list = serviceMessageType.readMessageTypes();
		if(CollectionUtils.isEmpty(list)){
			throw new ConfigurationException("Messages types not found");
		}
		serviceMessageType.registerMessageTypes(list);
	}
	
	/**
	 * Reads the fields configured and register them in {@code ConfigRegister}
	 * @throws ConfigurationException 
	 */
	private void readAndRegisterFields() throws ConfigurationException{
		for(String MTI : ConfigRegister.getInstance().getMessageTypes()){
			readAndRegisterFields(MTI);
		}
	}
	
	/**
	 * Reads the fields specified by the Message MTI
	 * @param MTI
	 * @throws ConfigurationException 
	 */
	private void readAndRegisterFields(String MTI) throws ConfigurationException{
		List<Fields> listFields = serviceField.findByMTI(MTI);
		if(CollectionUtils.isEmpty(listFields)){
			throw new ConfigurationException("Fields not found for MTI:"+MTI);
		}
		serviceField.registerFields(MTI, listFields);
	}
	
	/**
	 * Reads and register the current deploy directory
	 * @throws ConfigurationException 
	 */
	private void readAndRegisterDeployDir() throws ConfigurationException{
		String deployDir =  serviceDeployDir.readDeployDir();
		if(deployDir == null){
			throw new ConfigurationException("Deploy dir not found");
		}
		serviceDeployDir.registerDeployDir(deployDir);
	}

	/**
	 * Reads and register the active configuration
	 * @throws ConfigurationException 
	 */
	private void readAndRegisterActiveConfig() throws ConfigurationException{
		ConfigDTO dto = serviceConfig.findActiveConfig();
		if(dto == null){
			throw new ConfigurationException("Active config not found");
		}
		serviceConfig.registerActiveConfig(dto);
	}
}

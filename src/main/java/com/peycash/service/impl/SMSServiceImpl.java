/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.SMSBalanceException;
import com.peycash.common.ConfigRegister;
import com.peycash.common.dto.SMSProviderDTO;
import com.peycash.common.dto.SMSProviderDTO.SMSProviderStatus;
import com.peycash.common.util.AmountUtils;
import com.peycash.common.util.Constants;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.SMSProviderDAO;
import com.peycash.persistence.dao.SMSQueueDAO;
import com.peycash.persistence.dao.SMSTextDAO;
import com.peycash.persistence.dao.SmsStatusSendDAO;
import com.peycash.persistence.domain.Smsprovider;
import com.peycash.persistence.domain.Smsqueue;
import com.peycash.persistence.domain.Smsstatussend;
import com.peycash.persistence.domain.Smstext;
import com.peycash.persistence.domain.Transactions;
import com.peycash.service.SMSService;
import com.peycash.service.ServiceException;

/**
 * Implementation of the service for accessing services for SMS
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class SMSServiceImpl implements SMSService {
	
	/**
	 * Injected spring value
	 */
	@Autowired
	private SMSProviderDAO daoSMSProvider;
	/**
	 * Injected spring value
	 */
	@Autowired
	private SMSQueueDAO daoSMSQueue;
	/**
	 * Injected spring value
	 */
	@Autowired
	private SMSTextDAO daoSMStext;
	/**
	 * Injected spring value
	 */
	@Autowired
	private SmsStatusSendDAO daoSMSStatusSend;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SMSServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#findAvailableProvider()
	 */
	@Override
	@Transactional(readOnly=true)
	public SMSProviderDTO findAvailableProvider(){
		try {
			Smsprovider provider = daoSMSProvider.findAvailableSMSProvider();
			if(provider == null){
				return null;
			}
			SMSProviderDTO dtoProvider = new SMSProviderDTO();
			dtoProvider.setIdSMSProvider(provider.getIdsmsprovider());
			dtoProvider.setProviderName(provider.getProvider());
			dtoProvider.setSmsBalance(provider.getIdsmsbalance());
			if(provider.getSmsstatus().getIdsmsstatus() == 1){
				dtoProvider.setStatus(SMSProviderDTO.SMSProviderStatus.AVAILABLE);
			}else{
				dtoProvider.setStatus(SMSProviderDTO.SMSProviderStatus.NOT_AVAILABLE);
			}
			return dtoProvider;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	@Transactional
	private void sinchronizeSMSBalance(){
		try {
			Smsprovider provider = daoSMSProvider.findAvailableSMSProvider();
			provider.setIdsmsbalance((int) ConfigRegister.getInstance().getSmsProvider().getSmsBalance());
			daoSMSProvider.update(provider);
		} catch (PersistenceException e) {
			LOGGER.info("SMS Balance could not be sinchronized at {}"+ new Date());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#registerAvailableProvider(com.peycash.common.dto.SMSProviderDTO)
	 */
	@Override
	public void registerAvailableProvider(SMSProviderDTO dto){
		ConfigRegister.getInstance().setSmsProvider(dto);
	}
	
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#reserveSMS()
	 */
	@Override
	public void reserveSMS() throws SMSBalanceException{
		long balance = ConfigRegister.getInstance().getSmsProvider().getSmsBalance();
		if(balance == 0){
			throw new SMSBalanceException();
		}
		--balance;
		if(balance == 0){
			ConfigRegister.getInstance().getSmsProvider().setStatus(SMSProviderStatus.NOT_AVAILABLE);
		}
		 ConfigRegister.getInstance().getSmsProvider().setSmsBalance(balance);
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#refundSMS()
	 */
	@Override
	public void refundSMS(){
		long balance = ConfigRegister.getInstance().getSmsProvider().getSmsBalance();
		++balance;
		 ConfigRegister.getInstance().getSmsProvider().setSmsBalance(balance);
	}
	
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#sendSMS(java.lang.String, long)
	 */
	@Override
	@Transactional
	public void sendSMS(String ticketText, long idTransaction){
		Smsqueue sms = new Smsqueue();
			Smsprovider provider = new Smsprovider();
			provider.setIdsmsprovider(ConfigRegister.getInstance().getSmsProvider().getIdSMSProvider());
		sms.setSmsprovider(provider);
			Smsstatussend status = new Smsstatussend();
			status.setIdsmsstatussend((byte) Constants.SMS_STATUS_SEND_READY_FOR_SENT);
		sms.setSmsstatussend(status);
		sms.setTextticket(ticketText);
			Transactions tx = new Transactions();
			tx.setIdtransactions(idTransaction);
		sms.setTransactions(tx);
		try {
			daoSMSQueue.save(sms);
			sinchronizeSMSBalance();
		} catch (PersistenceException e) {
			LOGGER.error("FATAL: Message could not be sent - Tx: {}", idTransaction);
		}
	}
	
	@Override
	@Transactional
	public void cancellSMS(long idTransactionRoot){
		try {
			boolean isCancelled = daoSMSQueue.cancellSMS(idTransactionRoot);
			if(!isCancelled){
				LOGGER.info("SMS for the [{}] transaction could not be cancelled");
				return;
			}
			refundSMS();
			sinchronizeSMSBalance();
			
		} catch (PersistenceException e) {
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#generateSMSContent(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String generateSMSContent(String quantity, String paddedNumberSend, String pinClear){
		String template = ConfigRegister.getInstance().getSmsTextTemplate();
		template = template.replace(Constants.SMS_TEMPLATE_TEXT_KEYWORD_QUANTITY, quantity);
		template = template.replace(Constants.SMS_TEMPLATE_TEXT_KEYWORD_PADDED_NUMBER, paddedNumberSend);
		template = template.replace(Constants.SMS_TEMPLATE_TEXT_KEYWORD_PIN, pinClear);
		return template;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#formatQuantity(java.lang.String)
	 */
	@Override
	public String formatQuantity(String quantity){
		String quantityAmount = AmountUtils.formatAmountString(quantity);
		double amount = Double.parseDouble(quantityAmount);
		String amountValue = String.format("%.2f", amount);
		return amountValue;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#padCellularNumber(java.lang.String)
	 */
	@Override
	public String padCellularNumber(String celnumber){
		celnumber = celnumber.replaceFirst("^\\d{6}", "******");
		return celnumber;
	}
	
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#findSMSTemplate()
	 */
	@Override
	@Transactional(readOnly=true)
	public String findSMSTemplate(){
		try {
			Smstext smstext = daoSMStext.findActiveSmstext();
			String template = smstext.getTextSms();
			return template;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#registerSMSTemplateText(java.lang.String)
	 */
	@Override
	public void registerSMSTemplateText(String text){
		ConfigRegister.getInstance().setSmsTextTemplate(text);
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.SMSService#findSmsStatusSend()
	 */
	@Override
	@Transactional(readOnly=true)
	public List<Smsstatussend> findSmsStatusSend() throws ServiceException{
		try{
			List<Smsstatussend> list = daoSMSStatusSend.getAll();
			return list;
		}catch(PersistenceException e){
			throw new ServiceException("Error in findSmsStatusSend:"+e.getMessage(), e);
		}
	}

}

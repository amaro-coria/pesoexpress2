package com.peycash.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.ConfigurationException;
import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.facade.ConfigFacade;
import com.peycash.facade.ProcessingFacade;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.CustomerDAO;
import com.peycash.persistence.dao.SMSProviderDAO;
import com.peycash.persistence.dao.SMSQueueDAO;
import com.peycash.persistence.domain.Smsqueue;
import com.peycash.service.ServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@TransactionConfiguration(defaultRollback = true)
public class ProcessingTestRetrieve {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProcessingTestRetrieve.class);

	@Autowired
	private ConfigFacade facadeCongig;
	@Autowired
	private ProcessingFacade facadeProcessing;
	@Autowired
	private CustomerDAO daoCustomer;
	@Autowired
	private SMSProviderDAO daoSMSProvider;
	@Autowired
	private SMSQueueDAO daoSMS;

	private String toHex(String arg) {
		return String.format("%04x", new BigInteger(1, arg.getBytes(/*
																	 * YOUR_CHARSET?
																	 */)));
	}

	@Test
	@Transactional
	public void testRetrieve_OK() throws ConfigurationException, ISOException,
			ServiceException, PersistenceException, InterruptedException {
		facadeCongig.initConfig();
		ISOMsg request = ProcessingTestSend.ISOMsgFactory
				.create0200Successful();
		ISOMsg response = facadeProcessing.processMessage_0200(request,
				ProcessingCodeEnum.SEND);
		logMessage(request);
		logMessage(response);
		String responseCode = response.getString(39);
		assertEquals("00", responseCode);
		/* Transfer made */
		List<Smsqueue> listsms = daoSMS.getAll();
		assertNotNull(listsms);
		Smsqueue sms = listsms.get(0);
		String textSMS = sms.getTextticket();
		LOGGER.info("Info ticket: {}", textSMS);
		String pin = textSMS.substring(textSMS.length() - 4, textSMS.length());
		LOGGER.info("Pin: {}", pin);
		Thread.sleep(1000);
        pin = "0000"+pin;
        String pinHex = toHex(pin);
		byte[] pin_ = ISOUtil.hex2byte(pinHex);
		ISOMsg retrieveRequest = ISOMsgFactoryRetrieve
				.create0200Successful(pin_);
		ISOMsg responseRetrieve = facadeProcessing.processMessage_0200(
				retrieveRequest, ProcessingCodeEnum.RETRIEVE);
		logMessage(retrieveRequest);
		logMessage(responseRetrieve);
		String responseCode1 = responseRetrieve.getString(39);
		assertEquals("00", responseCode1);
	}

	@Test
	@Transactional
	public void testRetrieve_errorPIN() throws ConfigurationException,
			ISOException, ServiceException, PersistenceException,
			InterruptedException {
		facadeCongig.initConfig();
		ISOMsg request = ProcessingTestSend.ISOMsgFactory
				.create0200Successful();
		ISOMsg response = facadeProcessing.processMessage_0200(request,
				ProcessingCodeEnum.SEND);
		logMessage(request);
		logMessage(response);
		String responseCode = response.getString(39);
		assertEquals("00", responseCode);
		/* Transfer made */
		List<Smsqueue> listsms = daoSMS.getAll();
		assertNotNull(listsms);
		Smsqueue sms = listsms.get(0);
		String textSMS = sms.getTextticket();
		LOGGER.info("Info ticket: {}", textSMS);
		String pin = textSMS.substring(textSMS.length() - 4, textSMS.length());
		LOGGER.info("Pin: {}", pin);
		Thread.sleep(1000);
		 pin = "0000"+"1111";
	        String pinHex = toHex(pin);
			byte[] pin_ = ISOUtil.hex2byte(pinHex);
		ISOMsg retrieveRequest = ISOMsgFactoryRetrieve
				.create0200Successful(pin_);
		ISOMsg responseRetrieve = facadeProcessing.processMessage_0200(
				retrieveRequest, ProcessingCodeEnum.RETRIEVE);
		logMessage(retrieveRequest);
		logMessage(responseRetrieve);
		String responseCode1 = responseRetrieve.getString(39);
		assertEquals("59", responseCode1);
	}

	@Test
	@Transactional
	public void testRetrieve_errorNoTransactionsPending()
			throws ConfigurationException, ISOException, ServiceException,
			PersistenceException, InterruptedException {
		facadeCongig.initConfig();
		String pin = "0000"+"1111";
        String pinHex = toHex(pin);
		byte[] pin_ = ISOUtil.hex2byte(pinHex);
	ISOMsg retrieveRequest = ISOMsgFactoryRetrieve
			.create0200Successful(pin_);
		ISOMsg responseRetrieve = facadeProcessing.processMessage_0200(
				retrieveRequest, ProcessingCodeEnum.RETRIEVE);
		logMessage(retrieveRequest);
		logMessage(responseRetrieve);
		String responseCode1 = responseRetrieve.getString(39);
		assertEquals("58", responseCode1);
	}

	@Test
	@Transactional
	public void testRetrieve_errorNoTransactionsPending_otherCel()
			throws ConfigurationException, ISOException, ServiceException,
			PersistenceException, InterruptedException {
		facadeCongig.initConfig();
		ISOMsg request = ProcessingTestSend.ISOMsgFactory
				.create0200Successful();
		ISOMsg response = facadeProcessing.processMessage_0200(request,
				ProcessingCodeEnum.SEND);
		logMessage(request);
		logMessage(response);
		String responseCode = response.getString(39);
		assertEquals("00", responseCode);
		/* Transfer made */
		List<Smsqueue> listsms = daoSMS.getAll();
		assertNotNull(listsms);
		Smsqueue sms = listsms.get(0);
		String textSMS = sms.getTextticket();
		LOGGER.info("Info ticket: {}", textSMS);
		String pin = textSMS.substring(textSMS.length() - 4, textSMS.length());
		LOGGER.info("Pin: {}", pin);
		Thread.sleep(1000);
		pin = "0000"+pin;
        String pinHex = toHex(pin);
		byte[] pin_ = ISOUtil.hex2byte(pinHex);
		ISOMsg retrieveRequest = ISOMsgFactoryRetrieve
			.create0200Successful_otherCel(pin_);
		ISOMsg responseRetrieve = facadeProcessing.processMessage_0200(
				retrieveRequest, ProcessingCodeEnum.RETRIEVE);
		logMessage(retrieveRequest);
		logMessage(responseRetrieve);
		String responseCode1 = responseRetrieve.getString(39);
		assertEquals("58", responseCode1);
	}

	private void logMessage(ISOMsg m) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		m.dump(ps, "");
		String message = os.toString();
		ps.close();
		LOGGER.debug(message);
	}

	static class ISOMsgFactoryRetrieve {
		public static ISOMsg create0200Successful(byte[] pin)
				throws ISOException {
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3, "010908");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11, "000001");
			request.set(12, ISODate.getTime(d));
			request.set(13, ISODate.getDate(d));
			request.set(22, "000");
			request.set(37, "000000000011");
			request.set(41, "CAJA0001");
			request.set(42, "     5544332211");
			request.set(43, "                              123456789");
			request.set(49, "484");
			request.set(76, "5540812022");
			request.set(52, pin);
			request.set(78, "5539674650");
			return request;
		}
		
		

		public static ISOMsg create0200Successful_otherCel(byte[] pin)
				throws ISOException {
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3, "010908");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11, "000001");
			request.set(12, ISODate.getTime(d));
			request.set(13, ISODate.getDate(d));
			request.set(22, "000");
			request.set(37, "000000000011");
			request.set(41, "CAJA0001");
			request.set(42, "     5544332211");
			request.set(43, "                              123456789");
			request.set(49, "484");
			request.set(76, "5540812022");
			request.set(52, pin);
			request.set(78, "5539674651");
			return request;
		}
	}

}

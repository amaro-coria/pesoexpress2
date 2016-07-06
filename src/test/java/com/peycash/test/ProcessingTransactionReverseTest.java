package com.peycash.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
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
import com.peycash.common.util.AmountUtils;
import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.facade.ConfigFacade;
import com.peycash.facade.ProcessingFacade;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.SMSQueueDAO;
import com.peycash.persistence.dao.TransactionDetailDAO;
import com.peycash.persistence.domain.Smsqueue;
import com.peycash.persistence.domain.Transactiondetail;
import com.peycash.service.ServiceException;
import com.peycash.service.TransactionDetailService;
import com.peycash.test.ProcessingTestRetrieve.ISOMsgFactoryRetrieve;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class ProcessingTransactionReverseTest {
	
	@Autowired
	private ConfigFacade facadeCongig;
	@Autowired
	private ProcessingFacade facadeProcessing;
	@Autowired
	private TransactionDetailService serviceTransactionDetail;
	@Autowired
	private TransactionDetailDAO daoTxDetail;
	@Autowired
	private SMSQueueDAO daoSMS;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingTestRetrieve.class);
	@Test
	@Transactional
	public void testPreviousTransaction() throws ISOException, ServiceException, ConfigurationException, PersistenceException{
		facadeCongig.initConfig();
		Date d = new Date();
		ISOMsg request = ISOMsgFactoryReverse.create0200Successful(d);
		String audit = request.getString(11);
		int auditNumber1 = Integer.parseInt(audit);
		String referenceNumber1 = request.getString(37);
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
		String autResponse1 = response.getString(38);
			logMessage(request);
			logMessage(response);
		String responseCode = response.getString(39);
		assertEquals("00", responseCode);
		/* Transfer made */
		ISOMsg reverse = ISOMsgFactoryReverse.create0420Successful(d);
		long pkTransferOrifin = findIdTransactionDetailToBeReversed(reverse);
		Transactiondetail detail = daoTxDetail.findByPK(pkTransferOrifin);
		String autResponse = detail.getAuthorizationresponse();
		int auditNumber = detail.getAuditnumber();
		String referenceNumber = detail.getReferencenumber();
		assertEquals(autResponse, autResponse1);
		assertEquals(auditNumber1, auditNumber);
		assertEquals(referenceNumber, referenceNumber1);
	}
	
	@Test
	@Transactional
	public void test_noPreviousTransaction() throws ISOException, ServiceException, ConfigurationException, PersistenceException{
		facadeCongig.initConfig();
		Date d = new Date();
		ISOMsg reverse = ISOMsgFactoryReverse.create0420Successful(d);
		ISOMsg response = facadeProcessing.processMessage_0420(reverse, ProcessingCodeEnum.SEND);
		logMessage(reverse);
		logMessage(response);
		String responseCode = response.getString(39);
		assertEquals("63", responseCode);
	}
	
	@Test
	@Transactional
	public void test_noSupportedResponseCode() throws ISOException, ServiceException, ConfigurationException, PersistenceException{
		facadeCongig.initConfig();
		Date d = new Date();
		ISOMsg request = ISOMsgFactoryReverse.create0200Successful(d);
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
			logMessage(request);
			logMessage(response);
		String responseCode = response.getString(39);
		assertEquals("00", responseCode);
		ISOMsg reverse = ISOMsgFactoryReverse.create0420NoSupportedResponseCode(d);
		ISOMsg response_1 = facadeProcessing.processMessage_0420(reverse, ProcessingCodeEnum.SEND);
		logMessage(reverse);
		logMessage(response_1);
		String responseCode_1 = response_1.getString(39);
		assertEquals("21", responseCode_1);
	}
	
	@Test
	@Transactional
	public void test_reverseTransactionMultipleTimes()throws ISOException, ServiceException, ConfigurationException, PersistenceException{
		facadeCongig.initConfig();
		Date d = new Date();
		ISOMsg request = ISOMsgFactoryReverse.create0200Successful(d);
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
			logMessage(request);
			logMessage(response);
		String responseCode = response.getString(39);
		assertEquals("00", responseCode);
		/* Transfer made */
		for(int i = 0; i<3; i++){
			ISOMsg reverse = ISOMsgFactoryReverse.create0420Successful(d);
			ISOMsg responseReverse = facadeProcessing.processMessage_0420(reverse, ProcessingCodeEnum.SEND);
			logMessage(reverse);
			logMessage(responseReverse);
			String responseCodeReverse = responseReverse.getString(39);
			assertEquals("00", responseCodeReverse);
		}
	}
	
	private String toHex(String arg) {
		return String.format("%04x", new BigInteger(1, arg.getBytes(/*
																	 * YOUR_CHARSET?
																	 */)));
	}
	
	@Test
	@Transactional
	public void test_reverseAndTryToRetrieve() throws ISOException, ServiceException, ConfigurationException, PersistenceException, InterruptedException {
		facadeCongig.initConfig();
		Date d = new Date();
		ISOMsg request = ISOMsgFactoryReverse.create0200Successful(d);
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
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
		String pin = textSMS.substring(textSMS.length()-4, textSMS.length());
		pin = "0000"+"1111";
        String pinHex = toHex(pin);
		byte[] pin_ = ISOUtil.hex2byte(pinHex);
		LOGGER.info("Pin: {}", pin);
			Thread.sleep(1000);
			ISOMsg reverse = ISOMsgFactoryReverse.create0420Successful(d);
			ISOMsg responseReverse = facadeProcessing.processMessage_0420(reverse, ProcessingCodeEnum.SEND);
			logMessage(reverse);
			logMessage(responseReverse);
			String responseCodeReverse = responseReverse.getString(39);
			assertEquals("00", responseCodeReverse);
			Thread.sleep(1000);
		ISOMsg retrieveRequest = ISOMsgFactoryRetrieve.create0200Successful(pin_);
		ISOMsg responseRetrieve = facadeProcessing.processMessage_0200(retrieveRequest, ProcessingCodeEnum.RETRIEVE);
			logMessage(retrieveRequest);
			logMessage(responseRetrieve);
		String responseCode1 = responseRetrieve.getString(39);
		assertEquals("58", responseCode1);
	}
	
	@Test
	@Transactional
	public void test_reverseRetrieve()throws ISOException, ServiceException, ConfigurationException, PersistenceException, InterruptedException{
		facadeCongig.initConfig();
		Date d = new Date();
		ISOMsg request = ISOMsgFactoryReverse.create0200Successful(d);
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
			logMessage(request);
			logMessage(response);
		String responseCode = response.getString(39);
		assertEquals("00", responseCode);
		/* Transfer made */
		Thread.sleep(1000);
		d = new Date();
		List<Smsqueue> listsms = daoSMS.getAll();
		assertNotNull(listsms);
		Smsqueue sms = listsms.get(0);
		String textSMS = sms.getTextticket();
		LOGGER.info("Info ticket: {}", textSMS);
		String pin = textSMS.substring(textSMS.length()-4, textSMS.length());
		LOGGER.info("Pin: {}", pin);
		pin = "0000"+pin;
        String pinHex = toHex(pin);
		byte[] pin_ = ISOUtil.hex2byte(pinHex);
		ISOMsg retrieveRequest = ISOMsgFactoryReverse.create0200Successful(pin_, d);
		ISOMsg responseRetrieve = facadeProcessing.processMessage_0200(retrieveRequest, ProcessingCodeEnum.RETRIEVE);
			logMessage(retrieveRequest);
			logMessage(responseRetrieve);
		String responseCode1 = responseRetrieve.getString(39);
		assertEquals("00", responseCode1);
		Thread.sleep(1000);
		/* begin reverse*/
		ISOMsg reverse = ISOMsgFactoryReverse.create0420Retrieve(d);
		ISOMsg responseReverse = facadeProcessing.processMessage_0420(reverse, ProcessingCodeEnum.RETRIEVE);
			logMessage(reverse);
			logMessage(responseReverse);
			String responseCodeReverse = responseReverse.getString(39);
			assertEquals("00", responseCodeReverse);
		/* reverse made */
			Thread.sleep(1000);
			d = new Date();
			ISOMsg retrieveRequest2 = ISOMsgFactoryReverse.create0200Successful(pin_, d);
			ISOMsg responseRetrieve2 = facadeProcessing.processMessage_0200(retrieveRequest2, ProcessingCodeEnum.RETRIEVE);
			logMessage(retrieveRequest2);
			logMessage(responseRetrieve2);
		String responseCode12 = responseRetrieve2.getString(39);
		assertEquals("00", responseCode12);
	}
	
	
	private long findIdTransactionDetailToBeReversed(ISOMsg m) throws ServiceException, PersistenceException{
		String audit = m.getString(11);
		String reference = m.getString(37);
		String amountStr = m.getString(4);
		String amount = AmountUtils.formatAmountString(amountStr);
		BigDecimal amountValue = new BigDecimal(amount);
		String terminal = m.getString(41);
		String partnerAff = m.getString(43);
		String parnershopAff = m.getString(42);
		String isoDate = m.getString(7);
		String authorizationresponse = m.getString(38);
		long pk = serviceTransactionDetail.findIdForExistingTransaction(audit, reference, amountValue, terminal, partnerAff, parnershopAff, isoDate, authorizationresponse, null);
		return pk;
	}
	
	private void logMessage(ISOMsg m){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		m.dump(ps, "");
		String message = os.toString();
		ps.close();
		LOGGER.debug(message);
	}
	
	static class ISOMsgFactoryReverse{
		
		public static ISOMsg create0420Retrieve(Date d)throws ISOException{
			ISOMsg request = new ISOMsg();
			request.setMTI("0420");
			request.set(3,"010908");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000001");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000011");
			request.set(38," ");
			request.set(39,"68");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			return request;
		}
		
		public static ISOMsg create0200Successful(byte[] pin, Date d) throws ISOException{
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"010908");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000001");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000011");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			request.set(49,"484");
			request.set(76,"5540812022");
			request.set(52, pin);
			request.set(78,"5539674650");
			return request;
		}
		
		public static ISOMsg create0420Successful(Date d) throws ISOException{
			ISOMsg request = new ISOMsg();
			request.setMTI("0420");
			request.set(3,"000809");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000001");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000001");
			request.set(38," ");
			request.set(39,"68");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			return request;
		}
		
		public static ISOMsg create0420NoSupportedResponseCode(Date d) throws ISOException{
			ISOMsg request = new ISOMsg();
			request.setMTI("0420");
			request.set(3,"000809");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000001");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000001");
			request.set(38," ");
			request.set(39,"78");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			request.set(49,"484");
			request.set(76,"5540812022");
			request.set(78,"5539674650");
			return request;
		}
		
		public static ISOMsg create0200Successful(Date d) throws ISOException{
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"000809");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000001");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000001");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			request.set(49,"484");
			request.set(76,"5540812022");
			request.set(78,"5539674650");
			return request;
		}
		
		public static ISOMsg create0200Successful_retrieve(String pin) throws ISOException{
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"010908");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000001");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000011");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			request.set(49,"484");
			//request.set(76,"5540812022");
			request.set(52, pin);
			request.set(78,"5539674650");
			return request;
		}
	}
}

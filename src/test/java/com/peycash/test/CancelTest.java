package com.peycash.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
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
import com.peycash.persistence.dao.CustomerDAO;
import com.peycash.persistence.dao.SMSProviderDAO;
import com.peycash.service.ServiceException;
import com.peycash.service.TransactionService;
import com.peycash.test.ProcessingTestSend.ISOMsgFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@TransactionConfiguration(defaultRollback = true)
public class CancelTest {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(CancelTest.class);

	@Autowired
	private TransactionService serviceTx;
	@Autowired
	private ConfigFacade facadeCongig;
	@Autowired
	private ProcessingFacade facadeProcessing;
	@Autowired
	private CustomerDAO daoCustomer;
	@Autowired
	private SMSProviderDAO daoSMSProvider;
	
	
	@Test
	@Transactional
	public void testCancel()throws ConfigurationException, ISOException, ServiceException{
		facadeCongig.initConfig();
		ISOMsg request = ISOMsgFactory.create0200Successful();
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			response.dump(ps, "");
			String message = os.toString();
			ps.close();
			LOGGER.debug(message);
		String responseCode = response.getString(39);
		assertEquals("00", responseCode);
		/*REQUEST SEND FINISH*/
		ISOMsg cancelRequest = ISOMsgFactory.create0200CancelSuccessful();
		ISOMsg cancelResponse = facadeProcessing.processMessage_0200(cancelRequest, ProcessingCodeEnum.CANCEL);
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			PrintStream ps1 = new PrintStream(os1);
			response.dump(ps1, "");
			String message1 = os1.toString();
			ps1.close();
			LOGGER.debug(message1);
		String responseCode1 = cancelResponse.getString(39);
		assertEquals("00", responseCode1);
	}
	
	static class ISOMsgFactory{
		public static ISOMsg create0200Successful() throws ISOException{
			Date d = new Date();
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
			request.set(76,"5540812012");
			request.set(78,"5539674690");
			return request;
		}
		
		public static ISOMsg create0200CancelSuccessful() throws ISOException{
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"000808");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000021");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000021");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			request.set(49,"484");
			request.set(76,"5540812012");
			request.set(78,"5540812012");
			return request;
		}
	}
}

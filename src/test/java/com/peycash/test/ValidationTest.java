package com.peycash.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

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

import com.peycash.ConfigurationException;
import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.common.util.ProcessingCodeException;
import com.peycash.facade.ConfigFacade;
import com.peycash.facade.ValidationFacade;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class ValidationTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationTest.class);
	
	@Autowired
	private ValidationFacade validation;
	@Autowired
	private ConfigFacade facadeCongig;
	
	@Test
	public void validateMTI() throws ConfigurationException{
		facadeCongig.initConfig();
		String[] mtiValids = {"0200", "0210"};
		String[] mtiNotValids = {"0345", "9000"};
		for(String s : mtiValids){
			int valid = validation.isValidMTI(s);
			assertEquals(2, valid);
		}
		for(String s : mtiNotValids){
			int valid = validation.isValidMTI(s);
			assertNotSame(2, valid);
			assertNotSame(4, valid);
			assertNotSame(8, valid);
		}
	}
	
	
	@Test
	public void validateFields() throws ConfigurationException{
		facadeCongig.initConfig();
		try {
			ISOMsg isoOK = ISOMsgFactory.create0200Successful();
			ISOMsg isoFailure = ISOMsgFactory.create0200Failure();
				/*ByteArrayOutputStream os = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(os);
				isoOK.dump(ps, "");
				String message = os.toString();
				ps.close();
				LOGGER.debug(message);*/
			int validOK = validation.validateFields(isoOK, isoOK.getMTI());
				/*ByteArrayOutputStream os1 = new ByteArrayOutputStream();
				PrintStream ps1 = new PrintStream(os1);
				isoOK.dump(ps1, "");
				String message1 = os1.toString();
				ps1.close();
				LOGGER.debug(message1);*/
			int validFail = validation.validateFields(isoFailure, isoFailure.getMTI());
			assertEquals(0, validOK);
			assertNotSame(0, validFail);
		} catch (ISOException e) {
			LOGGER.error("ISO ERROR", e);
		}
		
		
	}
	
	@Test
	public void validateProcessingCodes() throws ConfigurationException{
		String[] processingCodes = {"000809", "010908"};
		String[] processingCodesNotValid = {"000908", "010809", "112233"};
		facadeCongig.initConfig();
		for(String s : processingCodes){
			boolean b = validation.isValidProcessingCode(s);
			assertTrue(b);
		}
		for(String s : processingCodesNotValid){
			boolean b = validation.isValidProcessingCode(s);
			assertFalse(b);
		}
	}
	
	@Test
	public void validateProcessingCodeEnums() throws ConfigurationException, ProcessingCodeException{
		String send = "000809";
		String retrieve = "010908";
		facadeCongig.initConfig();
		ProcessingCodeEnum pcSend = validation.findTypeOfService(send);
		ProcessingCodeEnum pcRetrieve = validation.findTypeOfService(retrieve);
		assertEquals(ProcessingCodeEnum.SEND, pcSend);
		assertEquals(ProcessingCodeEnum.RETRIEVE, pcRetrieve);
	}
	
	static class ISOMsgFactory{
		public static ISOMsg create0200Successful() throws ISOException{
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"000809");
			request.set(4, "000000001050");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000001");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000001");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              1122334455");
			request.set(49,"484");
			request.set(76,"5540812022");
			request.set(78,"5539674650");
			return request;
		}
		
		public static ISOMsg create0200Failure() throws ISOException{
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"000809");
			request.set(4, "1050");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000001");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"123");
			request.set(37,"000000000001");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                             1122334455");
			request.set(49,"484");
			request.set(76,"5540812");
			request.set(78,"5539674");
			return request;
		}
	}
}

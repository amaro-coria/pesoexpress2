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
import com.peycash.common.util.DateUtils;
import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.facade.ConfigFacade;
import com.peycash.facade.ProcessingFacade;
import com.peycash.persistence.dao.CustomerDAO;
import com.peycash.persistence.dao.SMSProviderDAO;
import com.peycash.service.ServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@TransactionConfiguration(defaultRollback=false)
public class Test_INSERT_OK_TX {

	private static final Logger LOGGER = LoggerFactory.getLogger(Test_INSERT_OK_TX.class);

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
	public void testMessage_ok() throws ConfigurationException, ISOException, ServiceException{
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
			request.set(43,"                             1234567889");
			request.set(49,"484");
			request.set(76,"5540810022");
			request.set(78,"5588674650");
			return request;
		}
		
		public static ISOMsg create0200_partnershop() throws ISOException{
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
			request.set(43,"                              1234567889");
			request.set(49,"484");
			request.set(76,"5540812022");
			request.set(78,"5539674650");
			return request;
		}
		
		public static ISOMsg create0200_partner() throws ISOException{
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
			request.set(43,"                              1122004455");
			request.set(49,"484");
			request.set(76,"5540812022");
			request.set(78,"5539674650");
			return request;
		}
		
		public static ISOMsg create0200_datetime() throws ISOException{
			Date d = DateUtils.getCurrentDatePlusMinusHoursMinutes(false, false, 2, 20);
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
		
		public static ISOMsg create0200Successful_1a() throws ISOException{
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
			request.set(76,"5540812022");
			request.set(78,"5539674651");
			return request;
		}
		
		public static ISOMsg create0200Successful_2a() throws ISOException{
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
			request.set(76,"5540812022");
			request.set(78,"5539674652");
			return request;
		}
		
		public static ISOMsg create0200Successful_3a() throws ISOException{
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
			request.set(76,"5540812022");
			request.set(78,"5539674653");
			return request;
		}
		
		public static ISOMsg create0200Successful_1() throws ISOException{
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"000809");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000002");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000002");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			request.set(49,"484");
			request.set(76,"5540812023");
			request.set(78,"5539674650");
			return request;
		}
		
		public static ISOMsg create0200Successful_2() throws ISOException{
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"000809");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000003");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000003");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			request.set(49,"484");
			request.set(76,"5540812024");
			request.set(78,"5539674650");
			return request;
		}
		
		public static ISOMsg create0200Successful_3() throws ISOException{
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"000809");
			request.set(4, "000000030000");
			request.set(7, ISODate.getDateTime(d));
			request.set(11,"000004");
			request.set(12,ISODate.getTime(d));
			request.set(13,ISODate.getDate(d));
			request.set(22,"000");
			request.set(37,"000000000004");
			request.set(41,"CAJA0001");
			request.set(42,"     5544332211");
			request.set(43,"                              123456789");
			request.set(49,"484");
			request.set(76,"5540812025");
			request.set(78,"5539674650");
			return request;
		}
		
		public static ISOMsg create0200_notInTxLimits() throws ISOException{
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"000809");
			request.set(4, "000000100000");
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
		
		public static ISOMsg create0200_failureCustomerRiskiness() throws ISOException{
			Date d = new Date();
			ISOMsg request = new ISOMsg();
			request.setMTI("0200");
			request.set(3,"000809");
			request.set(4, "000000010000");
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
			request.set(76,"5512345678");
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
			request.set(43,"                             123456789");
			request.set(49,"484");
			request.set(76,"5540812");
			request.set(78,"5539674");
			return request;
		}
	}

	/**
	 * @return the facadeCongig
	 */
	public ConfigFacade getFacadeCongig() {
		return facadeCongig;
	}

	/**
	 * @param facadeCongig the facadeCongig to set
	 */
	public void setFacadeCongig(ConfigFacade facadeCongig) {
		this.facadeCongig = facadeCongig;
	}

	/**
	 * @return the facadeProcessing
	 */
	public ProcessingFacade getFacadeProcessing() {
		return facadeProcessing;
	}

	/**
	 * @param facadeProcessing the facadeProcessing to set
	 */
	public void setFacadeProcessing(ProcessingFacade facadeProcessing) {
		this.facadeProcessing = facadeProcessing;
	}

	/**
	 * @return the daoCustomer
	 */
	public CustomerDAO getDaoCustomer() {
		return daoCustomer;
	}

	/**
	 * @param daoCustomer the daoCustomer to set
	 */
	public void setDaoCustomer(CustomerDAO daoCustomer) {
		this.daoCustomer = daoCustomer;
	}

	/**
	 * @return the daoSMSProvider
	 */
	public SMSProviderDAO getDaoSMSProvider() {
		return daoSMSProvider;
	}

	/**
	 * @param daoSMSProvider the daoSMSProvider to set
	 */
	public void setDaoSMSProvider(SMSProviderDAO daoSMSProvider) {
		this.daoSMSProvider = daoSMSProvider;
	}
	
}

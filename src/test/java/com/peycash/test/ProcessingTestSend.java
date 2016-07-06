package com.peycash.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

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
import com.peycash.common.ConfigRegister;
import com.peycash.common.dto.SMSProviderDTO;
import com.peycash.common.dto.SMSProviderDTO.SMSProviderStatus;
import com.peycash.common.util.DateUtils;
import com.peycash.common.util.ProcessingCodeEnum;
import com.peycash.facade.ConfigFacade;
import com.peycash.facade.ProcessingFacade;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.CustomerDAO;
import com.peycash.persistence.dao.SMSProviderDAO;
import com.peycash.persistence.domain.Customer;
import com.peycash.persistence.domain.Customerriskiness;
import com.peycash.persistence.domain.Customerstate;
import com.peycash.persistence.domain.Smsprovider;
import com.peycash.persistence.domain.Smsstatus;
import com.peycash.service.ServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class ProcessingTestSend {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingTestSend.class);

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
	
	@Test
	@Transactional
	public void testMessage_customerRiskiness() throws PersistenceException, ConfigurationException, ISOException, ServiceException{
		facadeCongig.initConfig();
		Customer customer = new Customer();
		Customerstate status = new Customerstate();
		status.setIdcustomerstate(5);
		Customerriskiness risk = new Customerriskiness();
		risk.setIdcustomerriskiness(3);
		customer.setCustomerriskiness(risk);
		customer.setCustomerstate(status);
		customer.setCelnumber(5512345678l);
		customer.setLastsession(DateUtils.getCurrentlDate());
		customer.setUsername("PE CORE auto-gen first attempt");
		customer.setDatemodified(DateUtils.getCurrentlDate());
		customer.setIsactive((short) 1);
		daoCustomer.save(customer);
		ISOMsg request = ISOMsgFactory.create0200_failureCustomerRiskiness();
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		response.dump(ps, "");
		String message = os.toString();
		ps.close();
		LOGGER.debug(message);
		String responseCode = response.getString(39);
		assertEquals("60", responseCode);
	}
	
	@Test
	@Transactional
	public void testMessage_customerBlackListed() throws PersistenceException, ConfigurationException, ISOException, ServiceException{
		facadeCongig.initConfig();
		Customer customer = new Customer();
		Customerstate status = new Customerstate();
		status.setIdcustomerstate(5);
		Customerriskiness risk = new Customerriskiness();
		risk.setIdcustomerriskiness(1);
		customer.setCustomerriskiness(risk);
		customer.setCustomerstate(status);
		customer.setCelnumber(5512345678l);
		customer.setLastsession(DateUtils.getCurrentlDate());
		customer.setUsername("PE CORE auto-gen first attempt");
		customer.setDatemodified(DateUtils.getCurrentlDate());
		customer.setIsactive((short) 1);
		daoCustomer.save(customer);
		ISOMsg request = ISOMsgFactory.create0200_failureCustomerRiskiness();
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		response.dump(ps, "");
		String message = os.toString();
		ps.close();
		LOGGER.debug(message);
		String responseCode = response.getString(39);
		assertEquals("59", responseCode);
	}
	
	
	@Test
	@Transactional
	public void testMessage_smsProviderNotAvailable() throws ConfigurationException, ISOException, ServiceException, PersistenceException{
		facadeCongig.initConfig();
		SMSProviderDTO provider = ConfigRegister.getInstance().getSmsProvider();
		provider.setStatus(SMSProviderStatus.NOT_AVAILABLE);
		
		List<Smsprovider> listProviders = daoSMSProvider.getAll();
		for(Smsprovider smsprov : listProviders){
			Smsstatus stat = new Smsstatus();
			stat.setIdsmsstatus(2);
			smsprov.setSmsstatus(stat);
			daoSMSProvider.update(smsprov);
		}
		
		ConfigRegister.getInstance().setSmsProvider(provider);
		ISOMsg request = ISOMsgFactory.create0200Successful();
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			response.dump(ps, "");
			String message = os.toString();
			ps.close();
			LOGGER.debug(message);
		String responseCode = response.getString(39);
		assertEquals("91", responseCode);
	}
	
	@Test
	@Transactional
	public void testMessage_notInTxLimits() throws ConfigurationException, ISOException, ServiceException{
		facadeCongig.initConfig();
		ISOMsg request = ISOMsgFactory.create0200_notInTxLimits();
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			response.dump(ps, "");
			String message = os.toString();
			ps.close();
			LOGGER.debug(message);
		String responseCode = response.getString(39);
		assertEquals("80", responseCode);
	}
	
	@Test
	@Transactional
	public void testMessage_duplicity() throws ConfigurationException, ISOException, ServiceException{
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
		/*duplicity*/
		ISOMsg request1 = ISOMsgFactory.create0200Successful();
		ISOMsg response1 = facadeProcessing.processMessage_0200(request1, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			PrintStream ps1 = new PrintStream(os1);
			response1.dump(ps1, "");
			String message1 = os1.toString();
			ps1.close();
			LOGGER.debug(message1);
		String responseCode1 = response1.getString(39);
		assertEquals("31", responseCode1);//31 or 60, 
	}
	
	@Test
	@Transactional
	public void testMessage_limitDayli_receive() throws ConfigurationException, ISOException, ServiceException{
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
		//300
		ISOMsg request1 = ISOMsgFactory.create0200Successful_1();
		ISOMsg response1 = facadeProcessing.processMessage_0200(request1, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			PrintStream ps1 = new PrintStream(os1);
			response1.dump(ps1, "");
			String message1= os1.toString();
			ps1.close();
			LOGGER.debug(message1);
		String responseCode1 = response1.getString(39);
		assertEquals("00", responseCode1);
		//600
		ISOMsg request2 = ISOMsgFactory.create0200Successful_2();
		ISOMsg response2 = facadeProcessing.processMessage_0200(request2, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os2 = new ByteArrayOutputStream();
			PrintStream ps2 = new PrintStream(os2);
			response2.dump(ps2, "");
			String message2 = os2.toString();
			ps2.close();
			LOGGER.debug(message2);
		String responseCode2 = response2.getString(39);
		assertEquals("00", responseCode2);
		//900
		ISOMsg request3 = ISOMsgFactory.create0200Successful_3();
		ISOMsg response3 = facadeProcessing.processMessage_0200(request3, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os3 = new ByteArrayOutputStream();
			PrintStream ps3 = new PrintStream(os3);
			response3.dump(ps3, "");
			String message3 = os3.toString();
			ps3.close();
			LOGGER.debug(message3);
		String responseCode3 = response3.getString(39);
		assertEquals("82", responseCode3);
	}
	
	@Test
	@Transactional
	public void testMessage_limitDayli_send() throws ConfigurationException, ISOException, ServiceException, PersistenceException{
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
		
		/* update user */
		long customerPK = daoCustomer.findByNoCel(5540812022l);
		Customer customer = daoCustomer.findByPK(customerPK);
			Customerriskiness cr = new Customerriskiness();
			cr.setIdcustomerriskiness(1);
			Customerstate state = new Customerstate();
			state.setIdcustomerstate(1);
		customer.setCustomerriskiness(cr);
		customer.setCustomerstate(state);
		daoCustomer.update(customer);
		
		//300
		ISOMsg request1 = ISOMsgFactory.create0200Successful_1a();
		ISOMsg response1 = facadeProcessing.processMessage_0200(request1, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			PrintStream ps1 = new PrintStream(os1);
			response1.dump(ps1, "");
			String message1= os1.toString();
			ps1.close();
			LOGGER.debug(message1);
		String responseCode1 = response1.getString(39);
		assertEquals("00", responseCode1);
		//600
		ISOMsg request2 = ISOMsgFactory.create0200Successful_2a();
		ISOMsg response2 = facadeProcessing.processMessage_0200(request2, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os2 = new ByteArrayOutputStream();
			PrintStream ps2 = new PrintStream(os2);
			response2.dump(ps2, "");
			String message2 = os2.toString();
			ps2.close();
			LOGGER.debug(message2);
		String responseCode2 = response2.getString(39);
		assertEquals("00", responseCode2);
		//900
		ISOMsg request3 = ISOMsgFactory.create0200Successful_3a();
		ISOMsg response3 = facadeProcessing.processMessage_0200(request3, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os3 = new ByteArrayOutputStream();
			PrintStream ps3 = new PrintStream(os3);
			response3.dump(ps3, "");
			String message3 = os3.toString();
			ps3.close();
			LOGGER.debug(message3);
		String responseCode3 = response3.getString(39);
		assertEquals("81", responseCode3);
	}
	
	@Test
	@Transactional
	public void testMessage_datetime() throws ConfigurationException, ISOException, ServiceException{
		facadeCongig.initConfig();
		ISOMsg request = ISOMsgFactory.create0200_datetime();
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			response.dump(ps, "");
			String message = os.toString();
			ps.close();
			LOGGER.debug(message);
		String responseCode = response.getString(39);
		assertEquals("41", responseCode);
	}
	
	@Test
	@Transactional
	public void testMessage_affiliationPartner_partnershop() throws ConfigurationException, ISOException, ServiceException{
		facadeCongig.initConfig();
		ISOMsg request = ISOMsgFactory.create0200_partner();
		ISOMsg response = facadeProcessing.processMessage_0200(request, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			response.dump(ps, "");
			String message = os.toString();
			ps.close();
			LOGGER.debug(message);
		String responseCode = response.getString(39);
		assertEquals("87", responseCode);
		ISOMsg request2 = ISOMsgFactory.create0200_partnershop();
		ISOMsg response2 = facadeProcessing.processMessage_0200(request2, ProcessingCodeEnum.SEND);
			ByteArrayOutputStream os2 = new ByteArrayOutputStream();
			PrintStream ps2 = new PrintStream(os2);
			response2.dump(ps2, "");
			String message2 = os2.toString();
			ps2.close();
			LOGGER.debug(message2);
		String responseCode2 = response2.getString(39);
		assertEquals("88", responseCode2);
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
			request.set(76,"5540812022");
			request.set(78,"5539674650");
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
			request.set(42,"     5544330011");
			request.set(43,"                              123456789");
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
}

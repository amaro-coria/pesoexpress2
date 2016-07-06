package com.peycash.test;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.service.CustomerService;
import com.peycash.service.ServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class CustomerServiceTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceTest.class);

	@Autowired
	private CustomerService service;
	
	@Test
	public void testFindNONExistingCustomerByCellNumber(){
		long celNumber = 9000000000l;
		try {
			long pkCustomer = service.findExistingCustomerByCelNo(celNumber);
			Assert.assertEquals(0l, pkCustomer);
			LOGGER.info("Result expected reached, not customer with given value");
		} catch (ServiceException e) {
			LOGGER.error("Test method fails, an exception is thrown:",e);
		}
	}
	
	@Test
	@Transactional
	public void testRegisterCustomer(){
		long celNumber = 9000000000l;
		try {
			service.registerNewCustomer(celNumber);
			LOGGER.info("No exception. Good");
		} catch (ServiceException e) {
			LOGGER.error("Test method fails, an exception is thrown:",e);
		}
	}

}

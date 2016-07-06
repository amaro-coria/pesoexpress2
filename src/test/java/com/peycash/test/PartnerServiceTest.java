package com.peycash.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.peycash.service.PartnerService;
import com.peycash.service.PartnershopService;
import com.peycash.service.ServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class PartnerServiceTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartnerServiceTest.class);
	
	@Autowired
	private PartnerService servicePartner;
	@Autowired
	private PartnershopService servicePartnershop;
	
	@Test
	public void testPartnerAffiliation(){
		try {
			boolean b = servicePartner.isValidAffiliation("1122334455");
			boolean b2 = servicePartner.isValidAffiliation("6666666666");
			assertTrue(b);
			assertFalse(b2);
			LOGGER.info("Test testPartnerAffiliation passed");
		} catch (ServiceException e) {
			throw new AssertionError(e);
		}
	}
	
	@Test
	public void testPartnershopAffiliation(){
		try {
			boolean b =servicePartnershop.isValidAffiliation("5544332211");
			boolean b2 = servicePartnershop.isValidAffiliation("6666666666"); 
			assertTrue(b);
			assertFalse(b2);
			LOGGER.info("Test testPartnershopAffiliation passed");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

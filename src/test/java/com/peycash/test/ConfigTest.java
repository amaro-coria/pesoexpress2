package com.peycash.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.peycash.ConfigurationException;
import com.peycash.facade.ConfigFacade;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class ConfigTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigTest.class);
	
	@Autowired
	private ConfigFacade facadeCongig;
	
	 @Test
	public void testConfigService(){
		try {
			facadeCongig.initConfig();
			LOGGER.info("Good, no exceptions has ben thrown");
		} catch (ConfigurationException e) {
			LOGGER.error("Error in configuration::"+e.getMessage());
			throw new AssertionError(e);
		}
	}

}

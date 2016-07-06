/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.iso.server.impl;

import org.jpos.q2.Q2;
import org.jpos.q2.QBeanSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peycash.ConfigurationException;
import com.peycash.common.ConfigRegister;
import com.peycash.facade.ConfigFacade;
import com.peycash.iso.server.Server;

/**
 * Implementation class of the service for Q2 server
 * @author Jorge Amaro
 *
 */
@Service
public class ServerImpl extends QBeanSupport implements Server{
	
	final static Logger LOGGER = LoggerFactory.getLogger(ServerImpl.class);
	
	/**
	 * Injected spring value
	 */
	@Autowired
	private ConfigFacade facadeConfig;
	
	/* (non-Javadoc)
	 * @see com.peycash.iso.server.Server#initApp()
	 */
	public void initApp() throws InterruptedException {
		LOGGER.debug("Initializing Q2 Server");
		try {
			/* TKN_DES_A01 */
			facadeConfig.initConfig();
		} catch (ConfigurationException e) {
			LOGGER.error("Configuration cannot be read");
			throw new InterruptedException("Configuration cannot be read");
		}
		
		Q2 server = null;
		String deployDir = findDeployDir();
		LOGGER.debug("Found deployDir={}", deployDir == null ? "N/A": deployDir);
		if(deployDir == null){
			server = new Q2();
		}else{
			server = new Q2(deployDir);
		}
		setServer(server);
		getServer().start();
		LOGGER.debug("Q2 servier initialized successfully");
		// getServer().shutdown();
	}
		
	
	/* (non-Javadoc)
	 * @see com.peycash.iso.server.Server#shutdownApp()
	 */
	public void shutdownApp(){
		getServer().shutdown();
	}
	
	
	/**
	 * Finds the deploy directory previously registered
	 * @return the deploy directory
	 */
	private String findDeployDir(){
		String deployDir = ConfigRegister.getInstance().getDeployDir();
		return deployDir;
	}
	 
}

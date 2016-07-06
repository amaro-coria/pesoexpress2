/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.facade;

import com.peycash.ConfigurationException;

/**
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 * Interface for Facade configuration services
 *
 */
public interface ConfigFacade {

	/**
	 * Initializes main configuration of the server
	 * @throws ConfigurationException
	 */
	public void initConfig() throws ConfigurationException;

}

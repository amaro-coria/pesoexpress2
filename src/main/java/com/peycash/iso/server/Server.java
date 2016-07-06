/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.iso.server;

/**
 * Service interface for the Q2 server 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface Server {

	/**
	 * This method initialize the Q2 server with loaded values and if there is no loaded value with default values
	 * @throws InterruptedException
	 */
	public void initApp() throws InterruptedException;

	/**
	 * Shuts down the Q2 server instance
	 */
	public void shutdownApp();

}

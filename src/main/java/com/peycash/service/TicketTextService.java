/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;


/**
 * Interface for services of ticket text
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface TicketTextService {

	/**
	 * Finds the ticket text to send
	 * @return the text
	 */
	String getTicketText();

	/**
	 * Registers the value in the configuration class {@code ConfigRegister}
	 * @param text
	 */
	void registerTicketText(String text);

}

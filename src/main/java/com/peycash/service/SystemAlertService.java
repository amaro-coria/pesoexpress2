/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

/**
 * Interface of services related to system alerts
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface SystemAlertService {

	/**
	 * Register an alert in the system
	 * @param category the category of the alert
	 * @param description the description of the alert
	 * @throws ServiceException
	 */
	void registerAlert(String category, String description)
			throws ServiceException;

}

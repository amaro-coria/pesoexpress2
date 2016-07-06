/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import java.util.List;

/**
 * Interface for services related to response codes
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface ResponseCodeService {

	/**
	 * Register in {@code ConfigRegister} the currently supported response codes
	 * @param listSupportedCodes
	 */
	void registerSupportedCodes(List<String> listSupportedCodes);

	/**
	 * Finds the currently supported response codes
	 * @return the list
	 * @throws ServiceException
	 */
	List<String> findSuppportedCodes() throws ServiceException;

}

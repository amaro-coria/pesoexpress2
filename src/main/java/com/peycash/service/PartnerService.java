/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

/**
 * Interface for services for Partner
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 */
public interface PartnerService {

	/**
	 * Checks if the given affiliation is correct
	 * @param affiliation
	 * @return true if its correct
	 * @throws ServiceException
	 */
	boolean isValidAffiliation(String affiliation) throws ServiceException;

}

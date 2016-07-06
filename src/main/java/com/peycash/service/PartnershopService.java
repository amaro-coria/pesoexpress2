/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import com.peycash.persistence.domain.Partnershop;

/**
 * Interface for services for Partnershop
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface PartnershopService {

	/**
	 * Check if the given affiliation is correct
	 * @param affiliation
	 * @return true if its correct
	 * @throws ServiceException
	 */
	boolean isValidAffiliation(String affiliation) throws ServiceException;

	/**
	 * Finds the specific partnershop by id
	 * @param id the id of the partnershop
	 * @return the partnershop entity
	 * @throws ServiceException
	 */
	Partnershop findById(int id) throws ServiceException;

}

/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import java.util.List;

import com.peycash.persistence.domain.Customerriskiness;
import com.peycash.persistence.domain.Customerstate;

/**
 * Interface for accessing services related to customers 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface CustomerService {

	/**
	 * Return the ID of the customer based on its cel number
	 * @param celNumber
	 * @return the ID of the record. If no record found, returns 0 
	 * @throws ServiceException
	 */
	long findExistingCustomerByCelNo(long celNumber) throws ServiceException;

	/**
	 * Checks if the customer is riskiness
	 * @param pkCustomer
	 * @return true if is riskiness, false otherwise
	 * @throws ServiceException
	 */
	boolean isCustomerRiskiness(long pkCustomer) throws ServiceException;

	/**
	 * Checks the customer status based on its cel number
	 * @param pkCustomer
	 * @return  <pre>1=Registered</pre><pre>2=Register pending</pre><pre>3=Temporary blocked</pre><pre>4=PEP</pre><pre>5=BlackListed</pre> .... according to the DB config.
	 * @throws ServiceException
	 */
	int checkCustomerStatus(long pkCustomer) throws ServiceException;

	/**
	 * Pre-registers a new customer on the system
	 * @param noCel
	 * @throws ServiceException
	 */
	void registerNewCustomer(Long noCel) throws ServiceException;

	/**
	 * Finds the customer riskiness records
	 * @return the list with all the elements
	 * @throws ServiceException
	 */
	List<Customerriskiness> findCustomerRiskiness() throws ServiceException;

	/**
	 * Finds the customer states records
	 * @return the list with all the elements
	 * @throws ServiceException
	 */
	List<Customerstate> findCustomerStates() throws ServiceException;

}

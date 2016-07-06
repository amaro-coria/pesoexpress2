/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import java.util.List;

import com.peycash.common.ConfigRegister;
import com.peycash.persistence.domain.Messagetype;

/**
 * Interface for services related to message types supported
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface MessageTypeService {

	/**
	 * Read the message types registered to be registered in the application
	 * @return
	 */
	List<Messagetype> readMessageTypes();

	/**
	 * Register the message types in {@link ConfigRegister}
	 * @param list
	 */
	void registerMessageTypes(List<Messagetype> list);

}

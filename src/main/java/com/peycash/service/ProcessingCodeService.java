/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import java.util.List;

/**
 * 
 * Interface for services related to processing codes
 * 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public interface ProcessingCodeService {

	/**
	 * Reads and retrieves the processing codes configured in data base
	 * @return
	 */
	List<String> readProcessingCodes();

	/**
	 * Register the processing codes in the {@code ConfigRegister} singleton
	 * @param list
	 */
	void registerProcessingCodes(List<String> list);

}

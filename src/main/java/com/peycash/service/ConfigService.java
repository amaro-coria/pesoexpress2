/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import com.peycash.common.dto.ConfigDTO;

/**
 * @author Jorge Amaro
 * @version 1.0
 * Service interface for main configuration
 *
 */
public interface ConfigService {

	ConfigDTO findActiveConfig();

	void registerActiveConfig(ConfigDTO dto);



	

}

/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.ConfigRegister;
import com.peycash.common.dto.ConfigDTO;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.PeConfigDAO;
import com.peycash.persistence.domain.Peconfig;
import com.peycash.service.ConfigService;

/**
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 * Implementation of the service interface
 *
 */
@Service
public class ConfigServiceImpl implements ConfigService{

	/**
	 * Injected spring value
	 */
	@Autowired
	private PeConfigDAO daoConfig;
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ConfigService#findActiveConfig()
	 */
	@Override
	@Transactional(readOnly=true)
	public ConfigDTO findActiveConfig(){
			try {
				Peconfig config = daoConfig.findISOConfig();
				if(config == null){
					return null;
				}
				ConfigDTO dto = new ConfigDTO();
				dto.setTcpIp(config.getIp());
				dto.setTcpPort(Integer.parseInt(config.getPort()));
				return dto;
			} catch (PersistenceException e) {
				return null;
			}
	}
	
	@Override
	public void registerActiveConfig(ConfigDTO dto){
		ConfigRegister.getInstance().setConfigDTO(dto);
	}
	
	
	
	
}

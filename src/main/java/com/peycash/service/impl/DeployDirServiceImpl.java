package com.peycash.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.ConfigRegister;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.PeConfigDAO;
import com.peycash.persistence.domain.Peconfig;
import com.peycash.service.DeployDirService;

@Service
public class DeployDirServiceImpl implements DeployDirService{
	/**
	 * Injected spring value
	 */
	@Autowired
	private PeConfigDAO daoConfig;
	
	/**
	 * Reads the deploy directory for JPOS services and register the value in the singleton {@code ConfigRegister}
	 * @see ConfigRegister
	 */
	@Override
	@Transactional(readOnly=true)
	public String readDeployDir(){
		try {
			Peconfig domainConfig = daoConfig.findISOConfig();
			if(domainConfig == null){
				return null;
			}
			String domainConfig_deployDir = domainConfig.getDeployDir();
			return domainConfig_deployDir;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	@Override
	public void registerDeployDir(String str){
		ConfigRegister.getInstance().setDeployDir(str);
	}
}

/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.ConfigRegister;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.ProcessingCodeDAO;
import com.peycash.persistence.domain.Processingcode;
import com.peycash.service.ProcessingCodeService;

/**
 * 
 * Implementation class for processing code services
 * 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class ProcessingCodeServiceImpl implements ProcessingCodeService{

	/**
	 * Injected Spring value
	 */
	@Autowired
	private ProcessingCodeDAO daoProcessingCode;

	/* (non-Javadoc)
	 * @see com.peycash.service.ProcessingCodeService#readProcessingCodes()
	 */
	@Transactional(readOnly=true)
	@Override
	public List<String> readProcessingCodes() {
		try {
			List<Processingcode> listPC = daoProcessingCode.getAll();
			if(CollectionUtils.isEmpty(listPC)){
				return null;
			}
			List<String> pcList = new LinkedList<String>();
			listPC.forEach(element -> pcList.add(element.getDescription()));
			return pcList;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ProcessingCodeService#registerProcessingCodes(java.util.List)
	 */
	@Override
	public void registerProcessingCodes(List<String> list){
		ConfigRegister.getInstance().setProcessingCodes(list);
	}

}

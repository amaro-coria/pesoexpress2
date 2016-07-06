/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.ConfigRegister;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.MessageTypeDAO;
import com.peycash.persistence.domain.Messagetype;
import com.peycash.service.MessageTypeService;

/**
 * Implementation of the service interface
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class MessageTypeServiceImpl implements MessageTypeService {

	/**
	 * Injected spring value
	 */
	@Autowired
	private MessageTypeDAO daoMessageType;

	/* (non-Javadoc)
	 * @see com.peycash.service.MessageTypeService#readMessageTypes()
	 */
	@Transactional(readOnly = true)
	@Override
	public List<Messagetype> readMessageTypes() {
		try {
			List<Messagetype> list = daoMessageType.getAll();
			return list;
		} catch (PersistenceException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.peycash.service.MessageTypeService#registerMessageTypes(java.util.List)
	 */
	@Override
	public void registerMessageTypes(List<Messagetype> list) {
		if (CollectionUtils.isEmpty(ConfigRegister.getInstance()
				.getMessageTypes())) {
			List<String> mlist = new ArrayList<String>();
			ConfigRegister.getInstance().setMessageTypes(mlist);
		}
		list.forEach(e -> ConfigRegister.getInstance().getMessageTypes().add(e.getMTI()));
	}
}

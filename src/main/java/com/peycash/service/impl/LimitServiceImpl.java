/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.ConfigRegister;
import com.peycash.common.dto.LimitsDTO;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.LimitsDAO;
import com.peycash.persistence.domain.Limits;
import com.peycash.service.LimitService;

@Service
public class LimitServiceImpl implements LimitService {

	/**
	 * Injected spring value
	 */
	@Autowired
	private LimitsDAO daoLimits;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.peycash.service.LimitService#findActiveLimits()
	 */
	@Override
	@Transactional(readOnly = true)
	public Limits findActiveLimits() {
		try {
			List<Limits> listLimits = daoLimits.getAll();
			if (CollectionUtils.isEmpty(listLimits)) {
				return null;
			}
			Limits limit = listLimits.get(listLimits.size() - 1);
			return limit;
		} catch (PersistenceException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.peycash.service.LimitService#registerLimits(com.peycash.persistence.domain.Limits)
	 */
	@Override
	public void registerLimits(Limits limit) {
		LimitsDTO dto = new LimitsDTO();
		dto.setMaxTrax(limit.getMaximumamount().floatValue());
		dto.setMinTrax(limit.getMinimumamount().floatValue());
		dto.setWithdrawDay(limit.getWithdrawaldailyamount());
		dto.setWithdrawMonth(limit.getWithdrawalmontlyamount());
		dto.setReceiveDay(limit.getReceivedrawdayliamount());
		dto.setReceiveMonth(limit.getReceivewmonthlyamount());
		dto.setSendDay(limit.getShippingdailyamount());
		dto.setSendMonth(limit.getShippinmontlyamount());
		ConfigRegister.getInstance().setSystemLimits(dto);
	}

}

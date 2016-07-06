/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service;

import com.peycash.persistence.domain.Limits;

public interface LimitService {

	/**
	 * Find the active limits on the system
	 * @return the active limit on the system. <b>Can return null</b>
	 */
	Limits findActiveLimits();

	/**
	 * Register the system limits in {@code ConfigRegister}
	 * @param limit the entity to be transformed and set
	 */
	void registerLimits(Limits limit);

}

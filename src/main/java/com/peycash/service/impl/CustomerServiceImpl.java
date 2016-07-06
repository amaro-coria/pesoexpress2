/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.util.DateUtils;
import com.peycash.persistence.PersistenceException;
import com.peycash.persistence.dao.CustomerDAO;
import com.peycash.persistence.dao.CustomerRiskinessDAO;
import com.peycash.persistence.dao.CustomerStateDAO;
import com.peycash.persistence.domain.Customer;
import com.peycash.persistence.domain.Customerriskiness;
import com.peycash.persistence.domain.Customerstate;
import com.peycash.service.CustomerService;
import com.peycash.service.ServiceException;

/**
 * Implementation 
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class CustomerServiceImpl implements CustomerService{

	/**
	 * Injected spring value
	 */
	@Autowired
	private CustomerDAO daoCustomer;
	
	/**
	 * Injected spring value
	 */
	@Autowired
	private CustomerRiskinessDAO daoCustomerRiskiness;
	
	/**
	 * Injected spring value
	 */
	@Autowired
	private CustomerStateDAO daoCustomerState;
	
	/* (non-Javadoc)
	 * @see com.peycash.service.CustomerService#findExistingCustomerByCelNo(long)
	 */
	@Override
	@Transactional(readOnly=true)
	public long findExistingCustomerByCelNo(long celNumber) throws ServiceException{
		try{
			long pk = daoCustomer.findByNoCel(celNumber);			
			return pk;
		}catch(PersistenceException e){
			throw new ServiceException("Error in findExistingCustomerByCelNo:"+e.getMessage(), e);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.CustomerService#registerNewCustomer(java.lang.Long)
	 */
	@Override
	@Transactional
	public void registerNewCustomer(Long noCel) throws ServiceException{
		Customer customer = new Customer();
			Customerstate status = new Customerstate();
			status.setIdcustomerstate(2);
			Customerriskiness risk = new Customerriskiness();
			risk.setIdcustomerriskiness(1);
		customer.setCustomerriskiness(risk);
		customer.setCustomerstate(status);
		customer.setCelnumber(noCel);
		customer.setLastsession(DateUtils.getCurrentlDate());
		customer.setUsername("PE CORE auto-gen first attempt");
		customer.setDatemodified(DateUtils.getCurrentlDate());
		customer.setIsactive((short) 1);
		customer.setFailedattempscounter((byte)0);
		try {
			daoCustomer.save(customer);
		} catch (PersistenceException e) {
			throw new ServiceException("Error in registerNewCustomer:"+e.getMessage(), e);
		}
			
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.CustomerService#isCustomerRiskiness(long)
	 */
	@Override
	@Transactional(readOnly=true)
	public boolean isCustomerRiskiness(long pkCustomer) throws ServiceException{
		try{
			boolean result = daoCustomer.isCustomerHighRisk(pkCustomer);
			return result;
		}catch(Exception e){
			throw new ServiceException("Error in isCustomerRiskiness:"+e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.CustomerService#checkCustomerStatus(long)
	 */
	@Override
	@Transactional(readOnly=true)
	public int checkCustomerStatus(long pkCustomer) throws ServiceException{
		try{
			int status = daoCustomer.checkCustomerStatus(pkCustomer);
			return status;
		}catch(Exception e){
			throw new ServiceException("Error in checkCustomerStatus:"+e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.CustomerService#findCustomerRiskiness()
	 */
	@Override
	@Transactional(readOnly=true)
	public List<Customerriskiness> findCustomerRiskiness() throws ServiceException{
		try{
			List<Customerriskiness> list = daoCustomerRiskiness.getAll();
			return list;
		}catch(PersistenceException e){
			throw new ServiceException("Error in findCustomerRiskiness:"+e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.CustomerService#findCustomerStates()
	 */
	@Override
	@Transactional(readOnly=true)
	public List<Customerstate> findCustomerStates() throws ServiceException{
		try{
			List<Customerstate> list = daoCustomerState.getAll();
			return list;
		}catch(PersistenceException e){
			throw new ServiceException("Error in findCustomerRiskiness:"+e.getMessage(), e);
		}
	}
	
	
}

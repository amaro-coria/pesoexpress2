/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.peycash.facade.ProcessingFacade;
import com.peycash.facade.ValidationFacade;
import com.peycash.service.ResponseBuilder;

/**
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 * Class that holds a Spring ApplicationContext for bean getting purposes
 *
 */
@Component
public class PesoExpressContext implements ApplicationContextAware{

	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(
			ApplicationContext applicationContext_)
			throws BeansException {
		PesoExpressContext.applicationContext = applicationContext_;
	}
	
	/**
	 * Returns the current instance of he applicationContext
	 * @return the bean 
	 */
	public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
	
	/**
	 * Returns the Spring bean for {@code ValidationFacade}
	 * @return the bean 
	 */
	public static ValidationFacade getValidationFacade(){
		ValidationFacade bean = applicationContext.getBean(ValidationFacade.class);
		return bean;
	}
	
	/**
	 * Returns the Spring bean for {@code ProcessingFacade}
	 * @return the bean
	 */
	public static ProcessingFacade getProcessingFacade(){
		ProcessingFacade bean = applicationContext.getBean(ProcessingFacade.class);
		return bean;
	}
	
	/**
	 * Returns the Spring bean for {@code ResponseBuilder}
	 * @return the bean
	 */
	public static ResponseBuilder getResponseBuilder(){
		ResponseBuilder bean = applicationContext.getBean(ResponseBuilder.class);
		return bean;
	}

}

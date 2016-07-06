/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.peycash.iso.server.Server;


/**
 * @author Jorge Amaro
 * @version 1.0
 * Main class of the application. 
 * Loads the spring context and initializes the Q2 JPOS Server
 * 
 */
public class Main {
	
	private static Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		/* TODO 1.- Verificar escritura en log, pendiente */
		/* TODO 2 Verificar error al no haber conexion a la bd */
		LOGGER.info("Initializing PE 2.0.0");
		try{
			ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
			Server server = applicationContext.getBean(Server.class);
			server.initApp();
			LOGGER.info("Initializing status: 00");
		}catch(Exception fe){
			LOGGER.error("Initializing Spring context failure in PE v 2.0.0:", fe);
			LOGGER.info("Initializing status: 10");
		}
	}
}

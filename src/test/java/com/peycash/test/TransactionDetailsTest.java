package com.peycash.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.persistence.domain.Transactiondetail;
import com.peycash.service.ServiceException;
import com.peycash.service.TransactionDetailService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class TransactionDetailsTest {

	@Autowired
	private TransactionDetailService serviceTx;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDetailsTest.class);
	
	@Test
	@Transactional
	public void testDetailsByRoot() throws ServiceException{
		List<Transactiondetail> list = serviceTx.findTransactionDetailsByRoot(3l);
		for(Transactiondetail detail : list){
			LOGGER.debug("ID Detail:{}",detail.getIdtransactiondetail());
		}
	}
}

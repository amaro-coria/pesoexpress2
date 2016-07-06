package com.peycash.test;

import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.peycash.common.util.DateUtils;
import com.peycash.persistence.domain.Transactionlog;
import com.peycash.service.ServiceException;
import com.peycash.service.TransactionLogService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class TransactionLogTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionLogTest.class);
	
	@Autowired
	private TransactionLogService service;
	
	@Test
	@Transactional
	public void testTransactionLogSave(){
		Transactionlog txlog = new Transactionlog();
		txlog.setDateserver(DateUtils.getCurrentlDate());
		txlog.setMessagesink("Message sink");
		txlog.setMessagesource("msg source");
		txlog.setSink("sink");
		txlog.setSource("source");
		try {
			long pk = service.saveTransaction(txlog);
			assertNotSame(0l, pk);
			LOGGER.info("Test testTransactionLogSave passed");
		} catch (ServiceException e) {
			throw new AssertionError(e);
		}
	}

}

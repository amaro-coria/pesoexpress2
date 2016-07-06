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

import com.peycash.persistence.domain.Daylitransactions;
import com.peycash.persistence.domain.Monthlytransactions;
import com.peycash.service.ServiceException;
import com.peycash.service.TransactionService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@TransactionConfiguration(defaultRollback = true)
public class TransactionTest {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(TransactionTest.class);

	@Autowired
	private TransactionService serviceTx;

	@Test
	public void textDayli() {
		try {
			List<Daylitransactions> list = serviceTx.findDayliTransactions();
			list.forEach(x -> LOGGER.info("Tx:{}", x.getCelnumsend()));
		} catch (ServiceException e) {
			LOGGER.error("Error in textDayli");
		}
	}

	@Test
	public void testMonthly() {
		try {
			List<Monthlytransactions> list2 = serviceTx.findMonthlyTransactions();
			list2.forEach(y -> LOGGER.info("Tx:{}", y.getCelnumreceive()));
		} catch (ServiceException e) {
			LOGGER.error("Error in testMonthly");
		}

	}
}

package com.peycash.common;

import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomUtils {
	
	RandomDataGenerator randomGenerator;
	
	private RandomUtils (){
		randomGenerator = new RandomDataGenerator();
		randomGenerator.reSeedSecure(1000);
	}
	
	private static RandomUtils instance = new RandomUtils();
	
	public static RandomUtils getInstance(){
		return instance;
	}

	/**
	 * @return the randomGenerator
	 */
	public RandomDataGenerator getRandomGenerator() {
		return randomGenerator;
	}

	/**
	 * @param randomGenerator the randomGenerator to set
	 */
	public void setRandomGenerator(RandomDataGenerator randomGenerator) {
		this.randomGenerator = randomGenerator;
	}
	
	
	

}

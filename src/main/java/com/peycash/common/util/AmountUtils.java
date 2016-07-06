/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.common.util;

/**
 * Utility class for formatting and processing amount from ISO8583 messages
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public class AmountUtils {

	/**
	 * Formats the string 
	 * @param amountStr
	 * @return the string formatted, ready to fill a BigDecimal or double value
	 */
	public static String formatAmountString(String amountStr){
		String amount_1 = amountStr.substring(0, amountStr.length()-2);
		String amount_2 = amountStr.substring(amountStr.length()-2, amountStr.length());
		StringBuffer sb = new StringBuffer();
		sb.append(amount_1);
		sb.append(".");
		sb.append(amount_2);
		String amount = sb.toString();
		return amount;
	}
	
}

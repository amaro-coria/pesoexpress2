package com.peycash.common;

import org.apache.commons.codec.digest.DigestUtils;

public class PinUtils {

	public static String hashPin(String pin){
		String md5 = DigestUtils.md5Hex(pin);
		return md5;
	}
	
}

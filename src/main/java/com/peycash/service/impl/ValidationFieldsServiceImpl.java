/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.service.impl;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.peycash.common.ConfigRegister;
import com.peycash.common.util.Constants;
import com.peycash.common.util.DateUtils;
import com.peycash.persistence.domain.Fields;
import com.peycash.service.ValidationFieldsService;

/**
 * Implementation of the service interface
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
@Service
public class ValidationFieldsServiceImpl implements ValidationFieldsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ValidationFieldsServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.ValidationFieldsService#validateFields(org.jpos.iso
	 * .ISOMsg, java.lang.String)
	 */
	@Override
	public int validateFields(ISOMsg msg, String mti) {
		List<Fields> fieldList = ConfigRegister.getInstance().getFieldMap()
				.get(mti);
		List<Integer> list = fieldList.stream()
				.map(f -> validateFields(f, msg)).collect(Collectors.toList());
		IntSummaryStatistics stats = list.stream().mapToInt((x) -> x)
				.summaryStatistics();
		int max = stats.getMax();
		if(max > 0){
			list.forEach(e -> logMissingFields(e));
		}
		return max;
	}
	
	/**
	 * Logs the missing fields in the message
	 * @param i
	 */
	private void logMissingFields(int i){
		LOGGER.debug("Missing field [{}]", i);
	}

	/**
	 * Validates a single Field against the ISOMsg
	 * @param f
	 * @param msg
	 * @return
	 */
	private int validateFields(Fields f, ISOMsg msg) {
		if (msg.hasField(f.getField())) {
			Object objValue = null;
			try {
				objValue = msg.getValue(f.getField());
			} catch (ISOException e) {
				return f.getField();
			}
			String value = objValue.toString();
			boolean b = checkField(value, f);
			if (!b) {
				return f.getField();
			}
		} else if(f.getRequired() == 1){
			return f.getField();
		}
		return 0;
	}

	/**
	 * Validate a specific field based on its configuration
	 * 
	 * @param value
	 * @param f
	 * @return
	 */
	private boolean checkField(String value, Fields f) {
		boolean b = checkEmptyField(value);
		if (b) {
			return false;
		}
		switch (f.getTypeData()) {
		case Constants.FIELD_TYPE_NUMERIC:
			if (checkForNumericValue(value)
					&& checkForLength(value, f.getLength())
					&& checkForFixedValue(value, f.getFixed())) {
				return true;
			}
			break;
		case Constants.FIELD_TYPE_ALPHANUMERIC:
			if (checkForAlphaNumericValue(value)
					&& checkForLength(value, f.getLength())
					&& checkForFixedValue(value, f.getFixed())) {
				return true;
			}
			break;
		case Constants.FIELD_TYPE_DATE:
			if (checkForLength(value, f.getLength())
					&& checkForDateField(value, f.getFormat())) {
				return true;
			}
			break;
		case Constants.FIELD_NUMERIC_SPECIAL:
			if(checkForSpecialNumeric(value, f.getLength(), f.getFixed())){
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * Checks for special numeric entries. Validates numeric and empty strings with length of 1
	 * @param field
	 * @param length
	 * @param fixed
	 * @return
	 */
	private boolean checkForSpecialNumeric(String field, int length, String fixed){
		if(checkForNumericValue(field)){
			boolean checkL = checkForLength(field, length);
			boolean checkF = checkForFixedValue(field, fixed);
			boolean result = checkL && checkF;
			return result;
		}else{
			int lenght = field.length();
			if(lenght == 0){
				return true;
			}
			return false;
		}
	}

	/**
	 * Checks if the given value complains with Date specific format
	 * 
	 * @param value
	 * @param format
	 * @return
	 */
	private boolean checkForDateField(String value, String format) {
		boolean b = DateUtils.validateDateFormat(value, format);
		return b;
	}

	/**
	 * Checks if the value has printable ascii characters. Check first if the
	 * string is blank or null
	 * 
	 * @param value
	 * @return
	 */
	private boolean checkForAlphaNumericValue(String value) {
		boolean b = StringUtils.isAsciiPrintable(value);
		return b;
	}

	/**
	 * Checks if the value is null, empty or filled with blank spaces
	 * 
	 * @param value
	 * @return
	 */
	private boolean checkEmptyField(String value) {
		boolean empty = StringUtils.isBlank(value);
		return empty;
	}

	/**
	 * Check if the field is actually a number
	 * 
	 * @param value
	 * @return
	 */
	private boolean checkForNumericValue(String value) {
		String _value = value.trim();
		boolean b = StringUtils.isNumeric(_value);
		return b;
	}

	/**
	 * Check if the length of the field is actually the length expected. If the
	 * length is undetermined via Zero <code>0</code> in the configuration,
	 * returns true
	 * 
	 * @param value
	 * @param length
	 * @return
	 */
	private boolean checkForLength(String value, int length) {
		if (length == 0) {
			return true;
		}
		int len = value.length();
		boolean b = len == length;
		return b;
	}

	/**
	 * Check if the field has fixed value and if the value corresponds to the
	 * fixed one
	 * 
	 * @param value
	 * @param fixed
	 * @return
	 */
	private boolean checkForFixedValue(String value, String fixed) {
		if (fixed == null) {
			return true;
		}
		boolean b = value.equals(fixed);
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.peycash.service.ValidationFieldsService#isValidMTI(java.lang.String)
	 */
	@Override
	public int isValidMTI(String mti) {
		if (!isValidMTI_(mti)) {
			return 0;
		}
		String s = mti.substring(1, 2);
		int i = Integer.parseInt(s);
		return i;
	}

	/**
	 * Checks if the MTI provided is actually in the message types supported
	 * 
	 * @param mti
	 * @return
	 */
	private boolean isValidMTI_(String mti) {
		if (ConfigRegister.getInstance().getMessageTypes().contains(mti)) {
			return true;
		}
		return false;
	}
}

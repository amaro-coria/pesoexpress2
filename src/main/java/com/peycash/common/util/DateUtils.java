package com.peycash.common.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 * Utility class for routine operations with Date and Time instances
 *
 */
public class DateUtils {

	/**
	 * Validates only if a String is compliant for a date pattern
	 * @param value
	 * @param format
	 * @return
	 */
	public static boolean validateDateFormat(String value, String format){
		try{
			DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern(format);
			dateStringFormat.parseDateTime(value);
			return true;
		}catch(Exception e){
			return false;
		}
		
	}
	
	/**
	 * Checks if the given date is between the acceptable limits configured for time zones
	 * @param javaDate
	 * @return
	 */
	public static boolean validateDateTimeRange(Date javaDate){
		/* 1- minus 2:15 */
		/* 2 - plus 15min */
		DateTime given = new DateTime(javaDate);
		DateTime minus215 = getCurrentDatePlusMinusHoursMinutesDatTime(false, false, 2, 15);
		DateTime plus15 = getCurrentDatePlusMinusHoursMinutesDatTime(true, true, 0, 15);
		boolean isBefore = minus215.isBefore(given);
		boolean isAfter = plus15.isAfter(given);
		boolean result = isBefore && isAfter;
		return result;
		/*
		DateTime time = new DateTime(javaDate);
		DateTime time2 = time.plusMinutes(15);
		DateTime time3 = time.minusHours(2);
		DateTime time4 = time3.minusMinutes(15);
		DateTime timeNow = DateTime.now();
		//if the time given is MORE than 15 minutes difference
		boolean isAfter = timeNow.isAfter(time2);
		boolean isBefore = timeNow.isBefore(time4);
		boolean isAcceptable = !(isAfter && isBefore);
		return isAcceptable;*/
	}
	
	/**
	 * Checks if the given date is between the acceptable limits configured for time zones
	 * @param value
	 * @param format
	 * @return
	 */
	public static boolean validateDateTimeRange(String value, String format){
		try{
			DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern(format);
			DateTime time = dateStringFormat.parseDateTime(value);
			DateTime time2 = time.plusMinutes(15);
			DateTime time3 = time.minusHours(2);
			DateTime time4 = time3.minusMinutes(15);
			DateTime timeNow = DateTime.now();
			//if the time given is MORE than 15 minutes difference
			boolean isAfter = timeNow.isAfter(time2);
			boolean isBefore = timeNow.isBefore(time4);
			boolean isAcceptable = !(isAfter && isBefore);
			return isAcceptable;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Gets the current date in java format
	 * @return the current date
	 */
	public static Date getCurrentlDate(){
		Date date = DateTime.now().toDate();
		return date;
	}
	
	/**
	 * Gets the hour modified by specific parameters
	 * @param hours - true if the operations should add specific hours, false if the operation should subtract hours
	 * @param minutes - true if the operations should add specific minutes, false if the operation should subtract minutes
	 * @param hoursNumber - the hours to be added / subtracted
	 * @param minutesNumber - the minutes to be added / subtracted
	 * @return
	 */
	public static Date getCurrentDatePlusMinusHoursMinutes(boolean hours, boolean minutes, int hoursNumber, int minutesNumber){
		DateTime time = DateTime.now();
		if(hours){
			time = time.plusHours(hoursNumber);
		}else{
			time = time.minusHours(hoursNumber);
		}
		if(minutes){
			time = time.plusMinutes(minutesNumber);
		}else{
			time = time.minusMinutes(minutesNumber);
		}
		Date d = time.toDate();
		return d;
	}
	
	/**
	 * Gets the hour modified by specific parameters
	 * @param hours - true if the operations should add specific hours, false if the operation should subtract hours
	 * @param minutes - true if the operations should add specific minutes, false if the operation should subtract minutes
	 * @param hoursNumber - the hours to be added / subtracted
	 * @param minutesNumber - the minutes to be added / subtracted
	 * @return
	 */
	public static DateTime getCurrentDatePlusMinusHoursMinutesDatTime(boolean hours, boolean minutes, int hoursNumber, int minutesNumber){
		DateTime time = DateTime.now();
		if(hours){
			time = time.plusHours(hoursNumber);
		}else{
			time = time.minusHours(hoursNumber);
		}
		if(minutes){
			time = time.plusMinutes(minutesNumber);
		}else{
			time = time.minusMinutes(minutesNumber);
		}
		return time;
	}
}

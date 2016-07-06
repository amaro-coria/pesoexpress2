/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.common.dto;

import java.io.Serializable;

/**
 * Represents the limits of the transfers in the system
 * @author Jorge Amaro
 * @since 1.0
 * @version 1.0
 *
 */
public class LimitsDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sendDay;
	private int receiveDay;
	private int sendMonth;
	private int receiveMonth;
	private float minTrax;
	private float maxTrax;
	private int withdrawDay;
	private int withdrawMonth;
	/**
	 * @return the sendDay
	 */
	public int getSendDay() {
		return sendDay;
	}
	/**
	 * @param sendDay the sendDay to set
	 */
	public void setSendDay(int sendDay) {
		this.sendDay = sendDay;
	}
	/**
	 * @return the receiveDay
	 */
	public int getReceiveDay() {
		return receiveDay;
	}
	/**
	 * @param receiveDay the receiveDay to set
	 */
	public void setReceiveDay(int receiveDay) {
		this.receiveDay = receiveDay;
	}
	/**
	 * @return the sendMonth
	 */
	public int getSendMonth() {
		return sendMonth;
	}
	/**
	 * @param sendMonth the sendMonth to set
	 */
	public void setSendMonth(int sendMonth) {
		this.sendMonth = sendMonth;
	}
	/**
	 * @return the receiveMonth
	 */
	public int getReceiveMonth() {
		return receiveMonth;
	}
	/**
	 * @param receiveMonth the receiveMonth to set
	 */
	public void setReceiveMonth(int receiveMonth) {
		this.receiveMonth = receiveMonth;
	}
	/**
	 * @return the minTrax
	 */
	public float getMinTrax() {
		return minTrax;
	}
	/**
	 * @param minTrax the minTrax to set
	 */
	public void setMinTrax(float minTrax) {
		this.minTrax = minTrax;
	}
	/**
	 * @return the maxTrax
	 */
	public float getMaxTrax() {
		return maxTrax;
	}
	/**
	 * @param maxTrax the maxTrax to set
	 */
	public void setMaxTrax(float maxTrax) {
		this.maxTrax = maxTrax;
	}
	/**
	 * @return the withdrawDay
	 */
	public int getWithdrawDay() {
		return withdrawDay;
	}
	/**
	 * @param withdrawDay the withdrawDay to set
	 */
	public void setWithdrawDay(int withdrawDay) {
		this.withdrawDay = withdrawDay;
	}
	/**
	 * @return the withdrawMonth
	 */
	public int getWithdrawMonth() {
		return withdrawMonth;
	}
	/**
	 * @param withdrawMonth the withdrawMonth to set
	 */
	public void setWithdrawMonth(int withdrawMonth) {
		this.withdrawMonth = withdrawMonth;
	}
}

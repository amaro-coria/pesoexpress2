/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.common.util;

import com.peycash.persistence.domain.Transferstate;

/**
 * Factory for transferstate
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public class TransferStateFactory {
	
	/**
	 * Builds a transfer state representing SEND
	 * @return the transfer state
	 */
	public static Transferstate getTransferStateSENT(){
		Transferstate sent = new Transferstate();
		sent.setIdtransferstate(1);
		return sent;
	}
	
	/**
	 * Builds a transfer state representing RECEIVE
	 * @return the transfer state
	 */
	public static Transferstate getTransferStateRECEIVED(){
		Transferstate received = new Transferstate();
		received.setIdtransferstate(2);
		return received;
	}
	
	/**
	 * Builds a transfer state representing REVERSE
	 * @return the transfer state
	 */
	public static Transferstate getTransferStateREVERSED(){
		Transferstate reversed = new Transferstate();
		reversed.setIdtransferstate(3);
		return reversed;
	}
	
	public static Transferstate getTransferStateNON_REVERSABLE(){
		Transferstate reversed = new Transferstate();
		reversed.setIdtransferstate(4);
		return reversed;
	}
	
}

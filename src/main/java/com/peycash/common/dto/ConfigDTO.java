/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.common.dto;

import java.io.Serializable;

/**
 * DTO class for store main configuration values
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public class ConfigDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	private int tcpPort;
	private String tcpIp;
	/**
	 * @return the tcpPort
	 */
	public int getTcpPort() {
		return tcpPort;
	}
	/**
	 * @param tcpPort the tcpPort to set
	 */
	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}
	/**
	 * @return the tcpIp
	 */
	public String getTcpIp() {
		return tcpIp;
	}
	/**
	 * @param tcpIp the tcpIp to set
	 */
	public void setTcpIp(String tcpIp) {
		this.tcpIp = tcpIp;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConfigDTO [tcpPort=" + tcpPort + ", tcpIp=" + tcpIp + "]";
	}
	
	
	
}

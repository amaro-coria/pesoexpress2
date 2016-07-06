package com.peycash.service;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.peycash.common.StatusAmounts;

public interface ResponseBuilder {

	/**
	 * Builds a response for general failure scenarios
	 * @param m the iso message
	 * @return an iso message 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_generalFaiulre(ISOMsg m) throws ISOException;

	/**
	 * Builds a response for validation fields scenario
	 * @param m the iso message
	 * @return an iso message 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_validationFieldsFailure(ISOMsg m)
			throws ISOException;

	/**
	 * Builds a response for successful scenarios
	 * 
	 * @param m
	 *            the ISOMsg request
	 * @param autCode the authorization code to send
	 * @return ISOMsg response 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_OK(ISOMsg m, String autCode) throws ISOException;

	/**
	 * Builds an iso message for the required scenario
	 * @param m the iso message
	 * @return an iso message 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_fail_customerStatus_highRisk_notRegistered(ISOMsg m)
			throws ISOException;

	/**
	 * Builds an iso message for the required scenario
	 * @param m the iso message
	 * @return an iso message 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_fail_smsProviderNotAvailable(ISOMsg m)
			throws ISOException;

	/**
	 * Builds an iso message for the required scenario
	 * @param m the iso message
	 * @return an iso message 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_fail_notInTransactionLimits(ISOMsg m)
			throws ISOException;

	/**
	 * Builds an iso message for the required scenario
	 * @param m the iso message
	 * @return an iso message 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_fail_customerStatus_blackListed(ISOMsg m)
			throws ISOException;

	/**
	 * Builds a response for scenarios when a transaction with the same
	 * attributes is found in the system
	 * 
	 * @param m
	 *            the ISOMsg request
	 * @return the ISOMsg response
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_fail_duplicity(ISOMsg m) throws ISOException;

	/**
	 * Builds an iso message for the required scenario
	 * @param m the iso message
	 * @status the status of the exceed
	 * @return an iso message 0210
	 * @see {@code StatusAmounts}
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_fail_limitExceeded(ISOMsg m, StatusAmounts status)
			throws ISOException;

	/**
	 * Builds a response for scenarios when transactions is received out of time
	 * 
	 * @param m
	 *            the ISOMsg request
	 * @return the ISOMsg response
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_fail_dateTimeRange(ISOMsg m) throws ISOException;

	/**
	 * Builds a response for affiliation partner fail scenario
	 * 
	 * @param m
	 *            the ISOMsg request
	 * @return the ISOMsg response
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_fail_affiliationPartner(ISOMsg m,
			int affiliationFail) throws ISOException;

	/**
	 * Builds a generic response for events
	 * 
	 * @param m
	 *            ISOMsg request
	 * @param field39Value
	 *            the message to be set in field no 39
	 * @return an instance of the response ISOMsg
	 * @throws ISOException
	 */
	ISOMsg buildGenericResponse_0200(ISOMsg m, String field39Value)
			throws ISOException;

	/**
	 * Creates a generic response for 0200 messages that removes all innecesary
	 * fields
	 * 
	 * @param m
	 *            The ISOMsg
	 * @return an instance of the message without innecesary fields
	 * @throws ISOException
	 */
	ISOMsg buildGenericResponse_0200(ISOMsg m) throws ISOException;

	/**
	 * Builds an iso message for the required scenario
	 * @param m the iso message
	 * @return an iso message 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_processingCodeNotSupported(ISOMsg m)
			throws ISOException;

	/**
	 * Builds an iso message for the required scenario
	 * @param m2 the iso message
	 * @return an iso message 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_withdrawError(ISOMsg m2) throws ISOException;

	/**
	 * Builds an iso message for the required scenario
	 * @param m2 the iso message
	 * @return an iso message 0210
	 * @throws ISOException
	 */
	ISOMsg buildResponse0200_withdrawErrorNoPendings(ISOMsg m2)
			throws ISOException;

	
	/**
	 * Builds an iso message for the required scenario
	 * @param m2 the iso message
	 * @return an iso message 0430
	 * @throws ISOException
	 */
	ISOMsg buildResponse0420_previousRequestNotFound(ISOMsg m2)
			throws ISOException;

	/**
	 * Builds a generic response for 0420 scenarios
	 * @param m2
	 * @return
	 * @throws ISOException
	 */
	ISOMsg buildGenericResponse_0420(ISOMsg m2) throws ISOException;

	/**
	 * Builds a generic response for 0420 scenarios
	 * @param m2
	 * @return
	 * @throws ISOException
	 */
	ISOMsg buildGenericResponse_0420(ISOMsg m2, String field39Value)
			throws ISOException;

	/**
	 * Builds a response for successful scenarios
	 * @param m2
	 * @return
	 * @throws ISOException
	 */
	ISOMsg buildResponse0420_OK(ISOMsg m2) throws ISOException;

	/**
	 * Builds a response for successful scenarios
	 * @param m2
	 * @param autCode
	 * @return
	 * @throws ISOException
	 */
	ISOMsg buildResponse0420_responseCodeNotSupported(ISOMsg m2)
			throws ISOException;

}

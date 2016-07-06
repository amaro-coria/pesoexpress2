package com.peycash.service.impl;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.stereotype.Service;

import com.peycash.common.ConfigRegister;
import com.peycash.common.StatusAmounts;
import com.peycash.common.util.Constants;
import com.peycash.service.ResponseBuilder;

@Service
public class ResponseBuilderImpl implements ResponseBuilder{

	
	@Override
	public ISOMsg buildResponse0200_generalFaiulre(ISOMsg m2)
			throws ISOException {
		ISOMsg m = buildGenericResponse_0200(m2,
				Constants.RESPONSE_CODE_FAILS_GENERAL_ERROR);
		return m;
	}

	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_validationFieldsFailure(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_validationFieldsFailure(ISOMsg m2)
			throws ISOException {
		ISOMsg m =  buildGenericResponse_0200(m2,
				Constants.RESPONSE_CODE_FAILS_VALIDATION_FIELDS);
		m.recalcBitMap();
		return m;
	}

	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_OK(org.jpos.iso.ISOMsg, java.lang.String)
	 */
	@Override
	public ISOMsg buildResponse0200_OK(ISOMsg m2, String autCode) throws ISOException {
		ISOMsg m = buildGenericResponse_0200(m2, Constants.RESPONSE_CODE_SUCCESS);
		m.set(38, autCode);
		String ticketText = ConfigRegister.getInstance().getTicketText();
		if(ticketText == null || ticketText.isEmpty()){
			ticketText = Constants.TEMPLATE_TICKET_TEXT_DEFAULT;
		}
		m.set(127, ticketText);
		m.recalcBitMap();
		return m;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0420_OK(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0420_OK(ISOMsg m2) throws ISOException {
		ISOMsg m = buildGenericResponse_0420(m2, Constants.RESPONSE_CODE_SUCCESS);
		m.recalcBitMap();
		return m;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_withdrawError(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_withdrawError(ISOMsg m2) throws ISOException{
		ISOMsg m = buildGenericResponse_0200(
				m2,
				Constants.RESPONSE_CODE_FAILS_WITHDRAW_FAIL);
		return m;
	}
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_withdrawErrorNoPendings(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_withdrawErrorNoPendings(ISOMsg m2) throws ISOException{
		ISOMsg m = buildGenericResponse_0200(
				m2,
				Constants.RESPONSE_CODE_FAILS_WITHDRAW_NO_PENDINGS);
		return m;
	}

	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_fail_customerStatus_highRisk_notRegistered(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_fail_customerStatus_highRisk_notRegistered(
			ISOMsg m2) throws ISOException {
		ISOMsg m = buildGenericResponse_0200(
				m2,
				Constants.RESPONSE_CODE_FAILS_CUSTOMER_HIGH_RISK_OR_NOT_REGISTERED);
		return m;
	}

	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_fail_smsProviderNotAvailable(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_fail_smsProviderNotAvailable(
			ISOMsg m2) throws ISOException {
		ISOMsg m = buildGenericResponse_0200(
				m2,
				Constants.RESPONSE_CODE_FAILS_SMS_NOT_AVAILABLE);
		return m;
	}

	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_fail_notInTransactionLimits(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_fail_notInTransactionLimits(
			ISOMsg m2) throws ISOException {
		ISOMsg m =buildGenericResponse_0200(
				m2,
				Constants.RESPONSE_CODE_FAILS_TRANSACTIONS_AMOUNT_NOT_IN_LIMITS);
		return m;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_fail_customerStatus_blackListed(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_fail_customerStatus_blackListed(ISOMsg m2)
			throws ISOException {
		ISOMsg m = buildGenericResponse_0200(m2,
				Constants.RESPONSE_CODE_FAILS_CUSTOMER_BLACK_LISTED);
		return m;
	}

	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_fail_duplicity(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_fail_duplicity(ISOMsg m2)
			throws ISOException {
		ISOMsg m =buildGenericResponse_0200(m2,
				Constants.RESPONSE_CODE_FAILS_DUPLICITY);
		return m;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_fail_limitExceeded(org.jpos.iso.ISOMsg, com.peycash.common.StatusAmounts)
	 */
	@Override
	public ISOMsg buildResponse0200_fail_limitExceeded(ISOMsg m2, StatusAmounts status)
			throws ISOException {
		ISOMsg m = null;
		switch (status) {
		case DAYLI_LIMIT_EXCEED_BY_RECEIVER:
			m = buildGenericResponse_0200(m2,
					Constants.RESPONSE_CODE_FAILS_AMOUNT_DAYLI_RECEIVED_EXCEEDED);
			break;
		case DAYLI_LIMIT_EXCEED_BY_SENDER:
			m = buildGenericResponse_0200(m2,
					Constants.RESPONSE_CODE_FAILS_AMOUNT_DAYLI_SENT_EXCEEDED);
			break;
		case MONTHLY_LIMIT_EXCEED_BY_RECEIVER:
			m = buildGenericResponse_0200(m2,
					Constants.RESPONSE_CODE_FAILS_AMOUNT_MONTHLY_RECEIVED_EXCEEDED);
			break;
		case MONTHLY_LIMIT_EXCEED_BY_SENDER:
			m = buildGenericResponse_0200(m2,
					Constants.RESPONSE_CODE_FAILS_AMOUNT_MONTHLY_SENT_EXCEEDED);
			break;
		case STATUS_OK:
			break;
		default:
			break;
		}
		return m;
	}

	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_fail_dateTimeRange(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_fail_dateTimeRange(ISOMsg m2)
			throws ISOException {
		ISOMsg m  = buildGenericResponse_0200(m2, Constants.RESPONSE_CODE_FAILS_DATETIME);
		return m;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0420_previousRequestNotFound(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0420_previousRequestNotFound(ISOMsg m2) throws ISOException{
		ISOMsg m  = buildGenericResponse_0420(m2, Constants.RESPONSE_CODE_FAILS_NO_PREVIOUS_RECORD);
		return m;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0420_responseCodeNotSupported(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0420_responseCodeNotSupported(ISOMsg m2) throws ISOException{
		ISOMsg m  = buildGenericResponse_0420(m2, Constants.RESPONSE_CODE_FAILS_PROCESSING_CODE_NOT_SUPPORTED);
		return m;
	}

	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_fail_affiliationPartner(org.jpos.iso.ISOMsg, int)
	 */
	@Override
	public ISOMsg buildResponse0200_fail_affiliationPartner(ISOMsg m2,
			int affiliationFail) throws ISOException {
		ISOMsg m = null;
		switch (affiliationFail) {
		case 1:
			m = buildGenericResponse_0200(m2,
					Constants.RESPONSE_CODE_FAILS_AFFILIATION_PARTNER);
			break;
		case 2:
			m = buildGenericResponse_0200(m2,
					Constants.RESPONSE_CODE_FAILS_AFFILIATION_PARTNERSHOP);
			break;
		default:
			m = buildGenericResponse_0200(m2,
					Constants.RESPONSE_CODE_FAILS_AFFILIATION_PARTNER);
			break;
		}
		return m;
	}

	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildGenericResponse_0200(org.jpos.iso.ISOMsg, java.lang.String)
	 */
	@Override
	public ISOMsg buildGenericResponse_0200(ISOMsg m2, String field39Value)
			throws ISOException {
		ISOMsg m = buildGenericResponse_0200(m2);
		m.set(39, field39Value);
		m.recalcBitMap();
		return m;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildGenericResponse_0420(org.jpos.iso.ISOMsg, java.lang.String)
	 */
	@Override
	public ISOMsg buildGenericResponse_0420(ISOMsg m2, String field39Value)
			throws ISOException {
		ISOMsg m = buildGenericResponse_0420(m2);
		m.set(39, field39Value);
		m.recalcBitMap();
		return m;
	}

	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildGenericResponse_0200(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildGenericResponse_0200(ISOMsg m2) throws ISOException {
		ISOMsg m = (ISOMsg) m2.clone();
		m.setResponseMTI();
		int[] fieldsNotRequiredByResponse = { 76, 78, 22, 43, 52 };
		m.unset(fieldsNotRequiredByResponse);
		return m;
	}
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildGenericResponse_0420(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildGenericResponse_0420(ISOMsg m2) throws ISOException{
		ISOMsg m = (ISOMsg) m2.clone();
		m.setResponseMTI();
		int[] fieldsNotRequiredByResponse = { 22, 38, 41, 42, 43 };
		m.unset(fieldsNotRequiredByResponse);
		return m;
		
	} 
	
	/* (non-Javadoc)
	 * @see com.peycash.service.ResponseBuilder#buildResponse0200_processingCodeNotSupported(org.jpos.iso.ISOMsg)
	 */
	@Override
	public ISOMsg buildResponse0200_processingCodeNotSupported(ISOMsg m2)
			throws ISOException {
		ISOMsg m = buildGenericResponse_0200(m2,
				Constants.RESPONSE_CODE_FAILS_PROCESSING_CODE_NOT_SUPPORTED);
		return m;
	}

	
	
}

/*
 * Peycash 2014 - All rights reserved
 */
package com.peycash.common.util;

/**
 * Class with utility constants
 * @author Jorge Amaro
 * @version 1.0
 * @since 1.0
 *
 */
public class Constants {
	
	public static final String FIELD_TYPE_NUMERIC="N";
	public static final String FIELD_TYPE_DATE="F";
	public static final String FIELD_TYPE_ALPHANUMERIC="A";
	public static final String FIELD_NUMERIC_SPECIAL="NS";
	
	//Constatns for response codes
	public static final String RESPONSE_CODE_SUCCESS="00";
	public static final String RESPONSE_CODE_FAILS_AFFILIATION_PARTNER="87";
	public static final String RESPONSE_CODE_FAILS_AFFILIATION_PARTNERSHOP="88";
	public static final String RESPONSE_CODE_FAILS_MISSING_REQUIRED_FIELDS="89";
	public static final String RESPONSE_CODE_FAILS_DATETIME="41";
	public static final String RESPONSE_CODE_FAILS_NO_PREVIOUS_RECORD="63";
	public static final String RESPONSE_CODE_FAILS_DUPLICITY="31";
	public static final String RESPONSE_CODE_FAILS_GENERAL_ERROR="69";
	public static final String RESPONSE_CODE_FAILS_PROCESSING_CODE_NOT_SUPPORTED="21";
	public static final String RESPONSE_CODE_FAILS_VALIDATION_FIELDS="78";
	public static final String RESPONSE_CODE_FAILS_CUSTOMER_HIGH_RISK_OR_NOT_REGISTERED="60";
	public static final String RESPONSE_CODE_FAILS_CUSTOMER_BLACK_LISTED="59";
	public static final String RESPONSE_CODE_FAILS_SMS_NOT_AVAILABLE="91";
	public static final String RESPONSE_CODE_FAILS_TRANSACTIONS_AMOUNT_NOT_IN_LIMITS="80";
	public static final String RESPONSE_CODE_FAILS_AMOUNT_DAYLI_SENT_EXCEEDED="81";
	public static final String RESPONSE_CODE_FAILS_AMOUNT_DAYLI_RECEIVED_EXCEEDED="82";
	public static final String RESPONSE_CODE_FAILS_AMOUNT_MONTHLY_SENT_EXCEEDED="83";
	public static final String RESPONSE_CODE_FAILS_AMOUNT_MONTHLY_RECEIVED_EXCEEDED="84";
	public static final String RESPONSE_CODE_FAILS_WITHDRAW_FAIL="59";
	public static final String RESPONSE_CODE_FAILS_WITHDRAW_NO_PENDINGS="58";
	public static final int CUSTOMER_STATUS_HIGH_RISK_EVALUATION=60;
	public static final int CUSTOMER_STATUS_NOT_REGISTERED=60;
	public static final int CUSTOMER_STATUS_BLACK_LISTED=59;
	public static final int CUSTOMER_STATUS_OK=0;
	
	//Errros
	public static final String ERROR_SMS_NO_SMS_PROVIDER="There is no SMS Provider for replacement";
	public static final String ERROR_CATEGORY_SMS="SMS Provider";
	
	//Templates
	public static final String TEMPLATE_TICKET_TEXT_DEFAULT="Texto ticket de prueba para enviar a comercio. Hasta 160 caracteres";
	
	//Transfer state
	public static final int TRANSFER_STATE_SENT_0200=1;
	public static final int TRANSFER_STATE_WITHDRAW_0210=2;
	public static final int TRANSFER_STATE_REVERSE_0420=3;
	
	//Message types
	public static final int MESSAGE_TYPE_0200=1;
	public static final int MESSAGE_TYPE_0210=2;
	
	//Processing codes
	public static final int PROCESSING_CODE_000809=1;
	public static final int PROCESSING_CODE_010908=2;
	public static final int PROCESSING_CODE_000808=3;
	public static final String PROCESSING_CODE_STRING_000809="000809";
	public static final String PROCESSING_CODE_STRING_010908="010908";
	public static final String PROCESSING_CODE_STRING_000808="000808";
	
	//SMS Status send
	public static final int SMS_STATUS_SEND_READY_FOR_SENT=1;
	//SMS template keywords
	public static final String SMS_TEMPLATE_TEXT_KEYWORD_QUANTITY="M_QUANTITY";
	public static final String SMS_TEMPLATE_TEXT_KEYWORD_PADDED_NUMBER="P_NUMBER";
	public static final String SMS_TEMPLATE_TEXT_KEYWORD_PIN="PIN_NUMBER";
}

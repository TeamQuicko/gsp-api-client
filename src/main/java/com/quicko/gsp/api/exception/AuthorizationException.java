package com.quicko.gsp.api.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicko.gsp.api.client.GSPHTTPClient;

import in.gov.gst.beans.ApiResponse;

public class AuthorizationException extends Throwable
{

	private static final long serialVersionUID = 4777398988267708757L;

	@JsonProperty("transaction_id")
	String transactionId;

	@JsonProperty("data")
	private ApiResponse apiResponse;

	@JsonProperty("message")
	private String message;

	public AuthorizationException(final String transactionId, final String message, final Throwable casue)
	{
		super(casue);
		this.transactionId = transactionId;
		this.message = message;
	}

	public AuthorizationException(final String transactionId, final String message)
	{
		this.transactionId = transactionId;
		this.message = message;
	}

	public AuthorizationException(final String message, final Throwable casue)
	{
		super(casue);
		this.message = message;
	}

	public AuthorizationException(final ApiResponse apiResponse)
	{
		this.apiResponse = apiResponse;
		this.transactionId =
		        (String) apiResponse.headers().get(GSPHTTPClient.QUICKO_GSP_HEADER_ATTRIBUTE_TRANSACTION_ID);
		this.message = "code: ".concat(this.getErrorCode()).concat(", msg: ").concat(this.getErrorMessage());
	}

	public String getErrorMessage()
	{
		return apiResponse.getErrorMessage();
	}

	public String getErrorCode()
	{
		return apiResponse.getErrorCode();
	}

	@Override
	public String getMessage()
	{
		return message;
	}

	public ApiResponse getApiResponse()
	{
		return apiResponse;
	}

}

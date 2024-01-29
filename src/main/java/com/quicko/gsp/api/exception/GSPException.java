package com.quicko.gsp.api.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicko.gsp.api.client.GSPHTTPClient;

import in.gov.gst.beans.ApiResponse;

public class GSPException extends Throwable
{

	/** TODO Auto-generated JavaDoc */
	private static final long serialVersionUID = -8149130515037645511L;

	@JsonProperty("transaction_id")
	String transactionId;

	@JsonProperty("data")
	private ApiResponse apiResponse;

	public GSPException(final ApiResponse apiResponse)
	{
		this.apiResponse = apiResponse;
		this.transactionId =
		        (String) apiResponse.headers().get(GSPHTTPClient.QUICKO_GSP_HEADER_ATTRIBUTE_TRANSACTION_ID);
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
		return "code: ".concat(this.getErrorCode()).concat(", msg: ").concat(this.getErrorMessage());
	}

	public ApiResponse getApiResponse()
	{
		return apiResponse;
	}
}

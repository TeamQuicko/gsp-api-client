package com.quicko.gsp.api.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorizationException extends Throwable
{

	private static final long serialVersionUID = 4777398988267708757L;

	@JsonProperty("transaction_id")
	String transactionId;

	@JsonProperty("data")
	private String msg;

	public AuthorizationException(final String msg, final String transactionId)
	{
		this.msg = msg;
		this.transactionId = transactionId;
	}

	public AuthorizationException(final String msg, Throwable e)
	{
		this.initCause(e);
		this.msg = msg;
	}

	@Override
	public String getMessage()
	{
		return msg;

	}
}

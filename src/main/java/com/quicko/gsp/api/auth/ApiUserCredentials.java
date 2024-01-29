package com.quicko.gsp.api.auth;

public class ApiUserCredentials
{

	protected String apiKey;

	protected String apiSecret;

	public ApiUserCredentials(final String apiKey, final String apiSecret)
	{
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
	}

	public String getApiKey()
	{
		return this.apiKey;
	}

	public String getApiSecret()
	{
		return this.apiSecret;
	}

}

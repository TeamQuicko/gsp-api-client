package com.quicko.gsp.api.auth;

public class ApiSession
{

	protected String apiKey;

	protected String accessToken;

	public ApiSession(final String apiKey, final String accessToken)
	{
		this.apiKey = apiKey;

		this.accessToken = accessToken;
	}

	public String getApiKey()
	{
		return this.apiKey;
	}

	public String getAccessToken()
	{
		return this.accessToken;
	}

	public void setAccessToken(final String accessToken)
	{
		this.accessToken = accessToken;
	}
}

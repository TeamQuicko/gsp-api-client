package com.quicko.gsp.api.type;

public enum Environment
{

	PROD("https://api.gsp.quicko.com"),

	TEST("https://test-api.gsp.quicko.com");

	private final String host;

	Environment(final String host)
	{
		this.host = host;
	}

	public String getHost()
	{
		return this.host;
	}
}

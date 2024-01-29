package com.quicko.gsp.api.type;

public enum Environment implements in.gov.gst.type.Environment
{

	PROD("https://api.gsp.quicko.com"),

	TEST("https://test-api.gsp.quicko.com");

	public static Environment get(String apiKey)
	{

		switch (apiKey.split("_")[1])
		{
			case "live":
				return Environment.PROD;
			case "test":
				return Environment.TEST;
			default:
				return Environment.PROD;
		}
	}

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

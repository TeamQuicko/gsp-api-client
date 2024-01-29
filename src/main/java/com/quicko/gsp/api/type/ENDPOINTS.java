package com.quicko.gsp.api.type;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.gov.gst.util.FilterUtils;

public class ENDPOINTS
{

	public enum URLPath
	{

		QUICKO_GSP_AUTHENTICATE("/auth/authenticate"),

		QUICKO_GSP_AUTHORIZE("/auth/refresh");

		private final String value;

		URLPath(final String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}
	}

	public static String build(Environment env, URLPath urlPath, Object... args)
	{
		String url = new StringBuilder().append(env.getHost()).append(urlPath.getValue()).toString();
		String regex = "\\{(\\w*)\\}";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher;

		int i = 0;

		do
		{
			matcher = pattern.matcher(url);

			if (matcher.find())
			{
				url = url.replace(matcher.group(0),
				        args != null && args.length > i && args[i] != null && !args[i].toString().isEmpty()
				                ? (args[i] instanceof List || args[i] instanceof String[]
				                        ? FilterUtils.listToCSV((List<String>) args[i])
				                        : args[i].toString())
				                : "");
			}

			i++;

		}
		while (matcher.find());

		return url.toString();
	}
}

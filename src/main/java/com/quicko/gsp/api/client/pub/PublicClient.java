package com.quicko.gsp.api.client.pub;

import com.quicko.gsp.api.auth.ApiSessionProvider;
import com.quicko.gsp.api.exception.AuthorizationException;
import com.quicko.gsp.api.exception.QuickoGSPAuthorizationException;
import com.quicko.gsp.api.exception.QuickoGSPException;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;

import in.gov.gst.beans.PublicApiResponse;
import in.gov.gst.exception.GSTNException;
import in.gov.gst.type.ENDPOINTS.URLPath;
import okhttp3.OkHttpClient;

public class PublicClient
{

	private in.gov.gst.proxy.client.pub.ProxyClient proxyClient;

	public PublicClient(final OkHttpClient client, ApiSessionProvider apiSessionCredentialProvider, String appKey,
	        String encryptedAppKey, String whiteListedIpAddress) throws AuthorizationException
	{
		this.proxyClient = new in.gov.gst.proxy.client.pub.ProxyClient(client, appKey, encryptedAppKey,
		        com.quicko.gsp.api.type.Environment.get(apiSessionCredentialProvider.getApiKey()),
		        whiteListedIpAddress);

	}

	public PublicApiResponse get(URLPath urlPath, String version, Map<String, String> requestParams,
	        Map<String, String> requestHeaders)
	        throws IOException, JSONException, GSTNException, QuickoGSPException, QuickoGSPAuthorizationException
	{
		try
		{
			return this.proxyClient.get(urlPath, version, requestParams, requestHeaders);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new QuickoGSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new QuickoGSPAuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}
}

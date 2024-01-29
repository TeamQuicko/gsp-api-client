package com.quicko.gsp.api.client.taxpayer;

import com.quicko.gsp.api.auth.ApiSessionProvider;
import com.quicko.gsp.api.exception.AuthorizationException;
import com.quicko.gsp.api.exception.GSPException;
import com.quicko.gsp.api.type.Environment;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import in.gov.gst.auth.beans.TaxpayerSession;
import in.gov.gst.beans.TaxpayerApiResponse;
import in.gov.gst.exception.CryptographyException;
import in.gov.gst.exception.GSTNException;
import in.gov.gst.type.ENDPOINTS.URLPath;
import in.gov.gst.type.GoodsAndServicesTaxReturnType;
import okhttp3.OkHttpClient;

public class TaxpayerClient
{

	private in.gov.gst.proxy.client.taxpayer.ProxyClient proxyClient;

	public TaxpayerClient(final OkHttpClient client, ApiSessionProvider apiSessionCredentialProvider, String appKey,
	        String encryptedAppKey, String whiteListedIpAddress, Environment environment) throws AuthorizationException
	{
		this.proxyClient = new in.gov.gst.proxy.client.taxpayer.ProxyClient(client, appKey, encryptedAppKey,
		        environment.getHost(), whiteListedIpAddress);
	}

	public TaxpayerApiResponse generateOtp(final String version, final String userName, final String gstin)
	        throws IOException, JSONException, GSTNException, GSPException, AuthorizationException,
	        CryptographyException
	{
		try
		{
			return this.proxyClient.generateOtp(version, userName, gstin);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}

	public TaxpayerApiResponse verifyOtp(final String version, final String userName, final String gstin,
	        final String otp) throws IOException, JSONException, GSTNException, GSPException, AuthorizationException,
	        CryptographyException
	{
		try
		{
			return this.proxyClient.verifyOtp(version, userName, gstin, otp);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}

	public TaxpayerApiResponse refreshSession(final TaxpayerSession taxpayerSession, final String version)
	        throws IOException, JSONException, GSTNException, GSPException, AuthorizationException,
	        CryptographyException
	{
		try
		{
			return this.proxyClient.refreshSession(taxpayerSession, version);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}

	public TaxpayerApiResponse logout(final TaxpayerSession taxpayerSession, final String version) throws IOException,
	        JSONException, GSTNException, GSPException, AuthorizationException, CryptographyException
	{
		try
		{
			return this.proxyClient.logout(taxpayerSession, version);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}

	public TaxpayerApiResponse get(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final Map<String, String> requestParams, final Map<String, String> requestHeaders) throws IOException,
	        JSONException, GSTNException, GSPException, AuthorizationException, CryptographyException
	{
		try
		{
			return this.proxyClient.get(taxpayerSession, urlPath, version, requestParams, requestHeaders);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}

	public TaxpayerApiResponse post(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final String action, final Map<String, String> requestParams, final Map<String, String> requestHeaders,
	        final JSONObject json) throws IOException, JSONException, GSTNException, GSPException,
	        AuthorizationException, CryptographyException
	{
		try
		{
			return this.proxyClient.post(taxpayerSession, urlPath, version, action, requestParams, requestHeaders,
			        json);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}

	public TaxpayerApiResponse put(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final String action, final Map<String, String> requestParams, final Map<String, String> requestHeaders,
	        final JSONObject json) throws IOException, JSONException, GSTNException, GSPException,
	        AuthorizationException, CryptographyException
	{
		try
		{
			return this.proxyClient.put(taxpayerSession, urlPath, version, action, requestParams, requestHeaders, json);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}

	public TaxpayerApiResponse post(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final String action, final GoodsAndServicesTaxReturnType rtnType, final Map<String, String> requestParams,
	        final Map<String, String> requestHeaders, final JSONObject json) throws IOException, JSONException,
	        GSTNException, GSPException, AuthorizationException, CryptographyException
	{
		try
		{
			return this.proxyClient.post(taxpayerSession, urlPath, version, action, rtnType, requestParams,
			        requestHeaders, json);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}

	public TaxpayerApiResponse put(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final String action, final GoodsAndServicesTaxReturnType rtnType, final Map<String, String> requestParams,
	        final Map<String, String> requestHeaders, final JSONObject json) throws IOException, JSONException,
	        GSTNException, GSPException, AuthorizationException, CryptographyException
	{
		try
		{
			return this.proxyClient.put(taxpayerSession, urlPath, version, action, rtnType, requestParams,
			        requestHeaders, json);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}

	public TaxpayerApiResponse postWithEVC(final TaxpayerSession taxpayerSession, final URLPath urlPath,
	        final String version, final String action, final GoodsAndServicesTaxReturnType rtnType, final String pan,
	        final String otp, final Map<String, String> requestParams, final Map<String, String> requestHeaders,
	        final JSONObject json) throws IOException, JSONException, GSTNException, GSPException,
	        AuthorizationException, CryptographyException
	{
		try
		{
			return this.proxyClient.postWithEVC(taxpayerSession, urlPath, version, action, rtnType, pan, otp,
			        requestParams, requestHeaders, json);
		}
		catch (GSTNException e)
		{
			if (e.getApiResponse().httpStatusCode() == 500 && e.getApiResponse().getErrorCode() == "GEN5008")
			{
				throw new GSPException(e.getApiResponse());
			}
			else if (e.getApiResponse().httpStatusCode() == 453 && e.getApiResponse().getErrorCode() == "AUTH4035")
			{

				throw new AuthorizationException(e.getApiResponse());
			}
			throw e;
		}
	}
}

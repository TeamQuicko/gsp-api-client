package in.gov.gst.proxy.client.pub;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import in.gov.gst.auth.beans.PublicSession;
import in.gov.gst.beans.PublicApiResponse;
import in.gov.gst.exception.GSTNException;
import in.gov.gst.proxy.client.ProxyClientTest;
import in.gov.gst.type.ENDPOINTS.URLPath;

@TestInstance(Lifecycle.PER_CLASS)
public class PubProxyClientTest extends ProxyClientTest
{

	protected ProxyClient publicClient;

	protected PublicSession publicSession;

	@BeforeAll
	public void publicProxyClientSetup()
	{
		this.publicClient =
		        new ProxyClient(appKey, encryptedAppKey, BASE_URL, WHITE_LISTED_IP, CLIENT_ID, CLIENT_SECRET);
		commonAuthorise();
	}

	public void commonAuthorise()
	{
		Map<String, String> requestParams = new HashMap<>();

		Map<String, String> requestHeaders = new HashMap<>();

		JSONObject json = new JSONObject();
		json.put("app_key", encryptedAppKey);
		json.put("username", "commonapiuser");
		json.put("password", encryptedCommonPassword);
		json.put("action", "ACCESSTOKEN");
		try
		{

			final PublicApiResponse apiResponse = this.publicClient.auhtenticate(requestParams, requestHeaders, json);
			System.out.println(apiResponse.body());
			this.publicSession = new PublicSession(encryptedAppKey, appKey, apiResponse.body().getString("auth_token"),
			        apiResponse.body().getString("sek"), commonUsername);

		}
		catch (GSTNException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void ShouldReturnTP() throws IOException

	{

		Map<String, String> requestParams = new HashMap<>();
		requestParams.put(ProxyClient.QUERY_ATTRIBUTE_GSTIN, GSTIN);
		requestParams.put(ProxyClient.QUERY_ATTRIBUTE_ACTION,
		        in.gov.gst.proxy.client.pub.ProxyClient.QUERY_ACTION_SEARCH_TP);

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put(in.gov.gst.proxy.client.pub.ProxyClient.HEADER_ATTRIBUTE_STATE_CD, GSTIN.substring(0, 2));
		requestHeaders.put(ProxyClient.QUERY_ATTRIBUTE_GSTIN, GSTIN);

		try
		{

			final PublicApiResponse apiResponse = this.publicClient.get(this.publicSession,
			        URLPath.GSTN_COMMONS_SEARCH_TAX_PAYER, "v0.3", requestParams, requestHeaders);
			System.out.println(apiResponse.body());
		}
		catch (GSTNException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void ShouldTrackReturn() throws JSONException, GSTNException
	{

		Map<String, String> requestParams = new HashMap<>();
		requestParams.put(ProxyClient.QUERY_ATTRIBUTE_GSTIN, GSTIN);
		requestParams.put(ProxyClient.QUERY_ATTRIBUTE_ACTION,
		        in.gov.gst.proxy.client.pub.ProxyClient.QUERY_ACTION_TRACK_RETURNS);
		requestParams.put(in.gov.gst.proxy.client.pub.ProxyClient.QUERY_ATTRUBUTE_FINANCIAL_YEAR, "2023-24");

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put(in.gov.gst.proxy.client.pub.ProxyClient.HEADER_ATTRIBUTE_STATE_CD, GSTIN.substring(0, 2));
		requestHeaders.put(ProxyClient.QUERY_ATTRIBUTE_GSTIN, GSTIN);

		try
		{
			final PublicApiResponse apiResponse = this.publicClient.get(this.publicSession,
			        URLPath.GSTN_COMMONS_TRACK_RETURNS, "v1.0", requestParams, requestHeaders);
			System.out.println(apiResponse.body());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

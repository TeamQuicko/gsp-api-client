package in.gov.gst.proxy.client.pub;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import in.gov.gst.auth.beans.PublicSession;
import in.gov.gst.beans.PublicApiResponse;
import in.gov.gst.exception.GSTNException;
import in.gov.gst.type.ENDPOINTS;
import in.gov.gst.type.ENDPOINTS.URLPath;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProxyClient extends in.gov.gst.proxy.client.ProxyClient
{

	public ProxyClient(final String appKey, final String encryptedAppKey, final String baseUrl,
	        final String whiteListedIpAddress, final String clientId, final String clientSecret)
	{
		super(appKey, encryptedAppKey, baseUrl, whiteListedIpAddress, clientId, clientSecret);
	}

	public static String QUERY_ACTION_SEARCH_TP = "TP";

	public static String QUERY_ACTION_TRACK_RETURNS = "RETTRACK";

	public static String PAYLOAD_ACTION_COMMON_AUTHENTICATE = "ACCESSTOKEN";

	public PublicApiResponse get(final PublicSession publicSession, final URLPath urlPath, final String version,
	        final Map<String, String> requestParams, final Map<String, String> requestHeaders)
	        throws IOException, JSONException, GSTNException
	{

		HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINTS.build(this.baseUrl, urlPath, version)).newBuilder();
		if (requestParams != null)
		{
			for (Entry<String, String> queryParam : requestParams.entrySet())
			{
				urlBuilder.addQueryParameter(queryParam.getKey(), queryParam.getValue());
			}
		}

		Headers.Builder headers = new Headers.Builder();
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		if (requestHeaders != null)
		{
			for (Entry<String, String> header : requestHeaders.entrySet())
			{
				headers.add(header.getKey(), header.getValue());
			}
		}
		headers.add("clientid", this.clientId);
		headers.add("client-secret", this.clientSecret);
		headers.add("auth-token", publicSession.getAuthToken());
		headers.add("username", publicSession.getUserName());
		headers.add("txn", "asadacaaqw4323425234v23542412vq");

		Request request = new Request.Builder().url(urlBuilder.build()).headers(headers.build()).build();

		Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);

	}

	public PublicApiResponse auhtenticate(final Map<String, String> requestParams,
	        final Map<String, String> requestHeaders, final JSONObject json)
	        throws IOException, JSONException, GSTNException
	{
		HttpUrl.Builder urlBuilder =
		        HttpUrl.parse(ENDPOINTS.build(this.baseUrl, URLPath.GSTN_COMMONS_AUTHENTICATE)).newBuilder();
		if (requestParams != null)
		{
			for (Entry<String, String> queryParam : requestParams.entrySet())
			{
				urlBuilder.addQueryParameter(queryParam.getKey(), queryParam.getValue());
			}
		}

		Headers.Builder headers = new Headers.Builder();
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		headers.add("clientid", this.clientId);
		headers.add("client-secret", this.clientSecret);

		if (requestHeaders != null)
		{
			for (Entry<String, String> header : requestHeaders.entrySet())
			{
				headers.add(header.getKey(), header.getValue());
			}
		}

		final RequestBody body = RequestBody.create(this.mapper.writeValueAsString(json), ProxyClient.JSON);

		Request request = new Request.Builder().url(urlBuilder.build()).headers(headers.build()).post(body).build();

		Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);

	}

	protected static PublicApiResponse validateAndParse(Response response)
	        throws JSONException, IOException, GSTNException
	{
		in.gov.gst.beans.ApiResponse gspApiResponse = in.gov.gst.proxy.client.ProxyClient.validateAndParse(response);
		PublicApiResponse publicApiResponse =
		        new PublicApiResponse(gspApiResponse.httpStatusCode(), gspApiResponse.headers(), gspApiResponse.body());
		return publicApiResponse;
	}
}

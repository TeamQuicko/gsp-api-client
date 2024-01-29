package in.gov.gst.proxy.client.taxpayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import in.gov.gst.auth.beans.TaxpayerSession;
import in.gov.gst.beans.TaxpayerApiResponse;
import in.gov.gst.exception.CryptoException;
import in.gov.gst.exception.GSTNException;
import in.gov.gst.type.ENDPOINTS;
import in.gov.gst.type.ENDPOINTS.URLPath;
import in.gov.gst.type.Environment;
import in.gov.gst.type.GoodsAndServicesTaxReturnType;
import in.gov.gst.util.AppKeyUtil;
import in.gov.gst.util.OtpUtil;
import in.gov.gst.util.RequestUtil;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProxyClient extends in.gov.gst.proxy.client.ProxyClient
{

	public static String QUERY_ATTRIBUTE_TAXPAYER_RETURN_TYPE = "ret_typ";

	public static String QUERY_ATTRIBUTE_TAXPAYER_RETURN_PERIOD = "ret_period";

	public static String QUERY_ATTRIBUTE_TAXPAYER_PERIOD_FROM = "fr_dt";

	public static String QUERY_ATTRIBUTE_TAXPAYER_PERIOD_TO = "to_dt";

	public static String QUERY_ATTRIBUTE_OTP = "otp";

	public static String QUERY_ATTRIBUTE_PAN = "pan";

	public static String QUERY_ACTION_EVCOTP = "EVCOTP";

	public static String QUERY_EVC_OTP_ATTRIBUTE_FORM_TYPE = "form_type";

	public static String HEADER_ATTRIBUTE_AUTH_TOKEN = "auth-token";

	public static String HEADER_ATTRIBUTE_GSTIN = "gstin";

	public static String HEADER_ATTRIBUTE_TAXPAYER_RETURN_TYPE = "rtn_typ";

	public static String HEADER_ATTRIBUTE_TAXPAYER_RETURN_USER_ROLE = "userrole";

	public static String HEADER_ATTRIBUTE_TAXPAYER_RETURN_API_VERSION = "api_version";

	public static String HEADER_ATTRIBUTE_TAXPAYER_RETURN_PERIOD = "ret_period";

	public static String HEADER_ATTRIBUTE_USER_NAME = "username";

	public static String PAYLOAD_ACTION_REFRESHTOKEN = "REFRESHTOKEN";

	public static String PAYLOAD_ACTION_OTPREQUEST = "OTPREQUEST";

	public static String PAYLOAD_ACTION_LOGOUT = "LOGOUT";

	public static String PAYLOAD_ACTION_AUTHTOKEN = "AUTHTOKEN";

	public static String PAYLOAD_ATTRIBUTE_AUTH_TOKEN = "auth_token";

	public static String RESPONSE_PAYLOAD_ATTRUBUTE_AUTH_TOKEN = "auth_token";

	public static String RESPONSE_PAYLOAD_ATTRUBUTE_SEK = "sek";

	public static String RESPONSE_PAYLOAD_ATTRUBUTE_REK = "rek";

	public ProxyClient(final OkHttpClient client, String appKey, String encryptedAppKey, Environment environment,
	        String whiteListedIpAddress)
	{
		super(client, appKey, encryptedAppKey, environment, whiteListedIpAddress);
	}

	public TaxpayerApiResponse generateOtp(final String version, final String userName, final String gstin)
	        throws CryptoException, JSONException, IOException, GSTNException
	{

		final Map<String, String> body = new HashMap<String, String>();

		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_ACTION, PAYLOAD_ACTION_OTPREQUEST);
		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_APP_KEY, this.encryptedAppKey);
		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_USERNAME, userName);

		RequestBody requestBody = null;

		requestBody = RequestBody.create(this.mapper.writeValueAsString(body), ProxyClient.JSON);

		Headers.Builder headers = new Headers.Builder();
		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, gstin.substring(0, 2));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);

		final Request request = new Request.Builder().url(ENDPOINTS.build(environment, URLPath.GSTN_TAX_PAYER, version))
		        .headers(headers.build()).post(requestBody).build();

		final Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);

	}

	public TaxpayerApiResponse verifyOtp(final String version, final String userName, final String gstin,
	        final String otp) throws CryptoException, IOException, JSONException, GSTNException
	{
		final String encryptedOtp = OtpUtil.encrypt(this.appKey, otp);

		final Map<String, String> body = new HashMap<String, String>();

		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_ACTION, PAYLOAD_ACTION_AUTHTOKEN);
		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_APP_KEY, this.encryptedAppKey);
		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_USERNAME, userName);
		body.put(QUERY_ATTRIBUTE_OTP, encryptedOtp);

		RequestBody requestBody = null;

		requestBody = RequestBody.create(this.mapper.writeValueAsString(body), ProxyClient.JSON);

		Headers.Builder headers = new Headers.Builder();

		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, gstin.substring(0, 2));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);

		final Request request = new Request.Builder().url(ENDPOINTS.build(environment, URLPath.GSTN_TAX_PAYER, version))
		        .headers(headers.build()).post(requestBody).build();

		final Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);

	}

	public TaxpayerApiResponse refreshSession(final TaxpayerSession taxpayerSession, final String version)
	        throws CryptoException, JSONException, IOException, GSTNException
	{
		final String newAppKey = AppKeyUtil.generateSecureKey();

		final String newEncryptedAppKey =
		        RequestUtil.encryptAppKey(newAppKey, taxpayerSession.getAppKey(), taxpayerSession.getSek());

		final Map<String, String> body = new HashMap<String, String>();

		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_ACTION, PAYLOAD_ACTION_REFRESHTOKEN);
		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_APP_KEY, newEncryptedAppKey);
		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_USERNAME, taxpayerSession.getUserName());
		body.put(PAYLOAD_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());

		RequestBody requestBody = RequestBody.create(this.mapper.writeValueAsString(body), ProxyClient.JSON);

		Headers.Builder headers = new Headers.Builder();

		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		headers.add(HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		headers.add(HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		headers.add(HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);

		final Request request = new Request.Builder().url(ENDPOINTS.build(environment, URLPath.GSTN_TAX_PAYER, version))
		        .headers(headers.build()).post(requestBody).build();

		final Response response = this.getClient().newCall(request).execute();

		TaxpayerApiResponse apiResponse = ProxyClient.validateAndParse(response);

		apiResponse.body().put("app_key", newAppKey);
		apiResponse.body().put("encrypted_app_key", newEncryptedAppKey);

		return apiResponse;

	}

	public TaxpayerApiResponse logout(final TaxpayerSession taxpayerSession, final String version)
	        throws JSONException, IOException, CryptoException, GSTNException
	{

		final Map<String, String> body = new HashMap<String, String>();

		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_ACTION, PAYLOAD_ACTION_LOGOUT);
		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_APP_KEY, taxpayerSession.getEncryptedAppKey());
		body.put(ProxyClient.PAYLOAD_ATTRIBUTE_USERNAME, taxpayerSession.getUserName());
		body.put(PAYLOAD_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());

		RequestBody requestBody = null;

		requestBody = RequestBody.create(this.mapper.writeValueAsString(body), ProxyClient.JSON);

		Headers.Builder headers = new Headers.Builder();

		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		headers.add(HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		headers.add(HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		headers.add(HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);

		final Request request = new Request.Builder().url(ENDPOINTS.build(environment, URLPath.GSTN_TAX_PAYER, version))
		        .headers(headers.build()).post(requestBody).build();

		final Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);

	}

	public TaxpayerApiResponse get(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final Map<String, String> requestParams, final Map<String, String> requestHeaders)
	        throws CryptoException, JSONException, IOException, GSTNException
	{

		HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINTS.build(environment, urlPath, version)).newBuilder();
		urlBuilder.addQueryParameter(ProxyClient.QUERY_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		if (requestParams != null)
		{
			for (Entry<String, String> queryParam : requestParams.entrySet())
			{
				urlBuilder.addQueryParameter(queryParam.getKey(), queryParam.getValue());
			}
		}

		Headers.Builder headers = new Headers.Builder();
		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		if (requestHeaders != null)
		{
			for (Entry<String, String> requestHeader : requestHeaders.entrySet())
			{
				headers.add(requestHeader.getKey(), requestHeader.getValue());
			}
		}

		final Request request = new Request.Builder().url(urlBuilder.build()).headers(headers.build()).build();

		final Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);
	}

	public TaxpayerApiResponse post(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final String action, final Map<String, String> requestParams, final Map<String, String> requestHeaders,
	        final JSONObject json) throws CryptoException, JSONException, IOException, GSTNException
	{
		HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINTS.build(environment, urlPath, version)).newBuilder();
		if (requestParams != null)
		{
			for (Entry<String, String> queryParam : requestParams.entrySet())
			{
				urlBuilder.addQueryParameter(queryParam.getKey(), queryParam.getValue());
			}
		}

		Headers.Builder headers = new Headers.Builder();
		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		if (requestHeaders != null)
		{
			for (Entry<String, String> requestHeader : requestHeaders.entrySet())
			{
				headers.add(requestHeader.getKey(), requestHeader.getValue());
			}
		}

		final RequestBody body = RequestBody.create(this.mapper.writeValueAsString(
		        RequestUtil.encrypt(taxpayerSession.getSek(), taxpayerSession.getAppKey(), json.toString(), action)),
		        ProxyClient.JSON);

		final Request request =
		        new Request.Builder().url(urlBuilder.build()).headers(headers.build()).post(body).build();

		final Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);
	}

	public TaxpayerApiResponse put(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final String action, final Map<String, String> requestParams, final Map<String, String> requestHeaders,
	        final JSONObject json) throws CryptoException, IOException, JSONException, GSTNException
	{

		HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINTS.build(environment, urlPath, version)).newBuilder();
		if (requestParams != null)
		{
			for (Entry<String, String> queryParam : requestParams.entrySet())
			{
				urlBuilder.addQueryParameter(queryParam.getKey(), queryParam.getValue());
			}
		}

		Headers.Builder headers = new Headers.Builder();
		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		if (requestHeaders != null)
		{
			for (Entry<String, String> requestHeader : requestHeaders.entrySet())
			{
				headers.add(requestHeader.getKey(), requestHeader.getValue());
			}
		}

		final RequestBody body = RequestBody.create(this.mapper.writeValueAsString(
		        RequestUtil.encrypt(taxpayerSession.getSek(), taxpayerSession.getAppKey(), json.toString(), action)),
		        ProxyClient.JSON);

		final Request request =
		        new Request.Builder().url(urlBuilder.build()).headers(headers.build()).put(body).build();

		final Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);
	}

	// Method overloading for [save] apis
	public TaxpayerApiResponse post(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final String action, final GoodsAndServicesTaxReturnType rtnType, final Map<String, String> requestParams,
	        final Map<String, String> requestHeaders, final JSONObject json)
	        throws CryptoException, JSONException, IOException, GSTNException
	{
		HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINTS.build(environment, urlPath, version)).newBuilder();
		if (requestParams != null)
		{
			for (Entry<String, String> queryParam : requestParams.entrySet())
			{
				urlBuilder.addQueryParameter(queryParam.getKey(), queryParam.getValue());
			}
		}

		Headers.Builder headers = new Headers.Builder();
		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		headers.add(ProxyClient.HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_USER_ROLE, rtnType.value().replace("-", ""));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_TYPE, rtnType.value().replace("-", ""));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_API_VERSION, version.replace("v", ""));
		if (requestHeaders != null)
		{
			for (Entry<String, String> requestHeader : requestHeaders.entrySet())
			{
				headers.add(requestHeader.getKey(), requestHeader.getValue());
			}
		}

		JSONObject hdr = new JSONObject();
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_USER_ROLE, rtnType.value().replace("-", ""));
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_TYPE, rtnType.value().replace("-", ""));
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_API_VERSION, version.replace("v", ""));

		JSONObject requestBody =
		        RequestUtil.encrypt(taxpayerSession.getSek(), taxpayerSession.getAppKey(), json.toString(), action);
		requestBody.put("hdr", hdr);

		final Request request = new Request.Builder().url(urlBuilder.build()).headers(headers.build())
		        .post(RequestBody.create(requestBody.toString(), JSON)).build();

		final Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);
	}

	// Method overloading for [save] apis
	public TaxpayerApiResponse put(final TaxpayerSession taxpayerSession, final URLPath urlPath, final String version,
	        final String action, final GoodsAndServicesTaxReturnType rtnType, final Map<String, String> requestParams,
	        final Map<String, String> requestHeaders, final JSONObject json)
	        throws CryptoException, JSONException, IOException, GSTNException
	{
		HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINTS.build(environment, urlPath, version)).newBuilder();
		if (requestParams != null)
		{
			for (Entry<String, String> queryParam : requestParams.entrySet())
			{
				urlBuilder.addQueryParameter(queryParam.getKey(), queryParam.getValue());
			}
		}

		Headers.Builder headers = new Headers.Builder();
		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		headers.add(ProxyClient.HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_USER_ROLE, rtnType.value().replace("-", ""));
		if ("RETNEWPTF".equalsIgnoreCase(action) || "PROCEEDFILE".equalsIgnoreCase(action))
		{
			headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_TYPE, rtnType.type());
		}
		else
		{
			headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_TYPE, rtnType.value().replace("-", ""));
		}

		headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_API_VERSION, version.replace("v", ""));
		if (requestHeaders != null)
		{
			for (Entry<String, String> requestHeader : requestHeaders.entrySet())
			{
				headers.add(requestHeader.getKey(), requestHeader.getValue());
			}
		}

		JSONObject hdr = new JSONObject();
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_USER_ROLE, rtnType.value().replace("-", ""));
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_API_VERSION, version.replace("v", ""));

		// sets type attribute of enum in returnType header for of proceed to file apis else sets value attribute of
		// enum for other apis
		if ("RETNEWPTF".equalsIgnoreCase(action) || "PROCEEDFILE".equalsIgnoreCase(action))
		{
			hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_TYPE, rtnType.type());
		}
		else
		{
			hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_TYPE, rtnType.value().replace("-", ""));
		}

		JSONObject requestBody =
		        RequestUtil.encrypt(taxpayerSession.getSek(), taxpayerSession.getAppKey(), json.toString(), action);
		requestBody.put("hdr", hdr);

		final Request request = new Request.Builder().url(urlBuilder.build()).headers(headers.build())
		        .put(RequestBody.create(requestBody.toString(), JSON)).build();

		final Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);
	}

	public TaxpayerApiResponse postWithEVC(final TaxpayerSession taxpayerSession, final URLPath urlPath,
	        final String version, final String action, final GoodsAndServicesTaxReturnType rtnType, final String pan,
	        final String otp, final Map<String, String> requestParams, final Map<String, String> requestHeaders,
	        final JSONObject json) throws CryptoException, JSONException, IOException, GSTNException
	{
		HttpUrl.Builder urlBuilder = HttpUrl.parse(ENDPOINTS.build(environment, urlPath, version)).newBuilder();
		if (requestParams != null)
		{
			for (Entry<String, String> queryParam : requestParams.entrySet())
			{
				urlBuilder.addQueryParameter(queryParam.getKey(), queryParam.getValue());
			}
		}

		Headers.Builder headers = new Headers.Builder();
		headers.add(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		headers.add(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_USER_ROLE, rtnType.value().replace("-", ""));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_TYPE, rtnType.value().replace("-", ""));
		headers.add(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_API_VERSION, version.replace("v", ""));
		if (requestHeaders != null)
		{
			for (Entry<String, String> requestHeader : requestHeaders.entrySet())
			{
				headers.add(requestHeader.getKey(), requestHeader.getValue());
			}
		}

		JSONObject hdr = new JSONObject();
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_STATE_CD, taxpayerSession.getGstin().substring(0, 2));
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_GSTIN, taxpayerSession.getGstin());
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_USER_NAME, taxpayerSession.getUserName());
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_AUTH_TOKEN, taxpayerSession.getAuthToken());
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_IP, this.whiteListedIpAddress);
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_USER_ROLE, rtnType.value().replace("-", ""));
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_TYPE, rtnType.value().replace("-", ""));
		hdr.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_API_VERSION, version.replace("v", ""));

		JSONObject requestBody = RequestUtil.encrypt(taxpayerSession.getSek(), taxpayerSession.getAppKey(),
		        json.toString(), action, pan, otp);
		requestBody.put("hdr", hdr);

		final Request request = new Request.Builder().url(urlBuilder.build()).headers(headers.build())
		        .post(RequestBody.create(requestBody.toString(), JSON)).build();

		final Response response = this.getClient().newCall(request).execute();

		return ProxyClient.validateAndParse(response);
	}

	public static TaxpayerApiResponse validateAndParse(Response response)
	        throws JSONException, IOException, GSTNException
	{
		in.gov.gst.beans.ApiResponse gspApiResponse = in.gov.gst.proxy.client.ProxyClient.validateAndParse(response);
		TaxpayerApiResponse taxpayerApiResponse = new TaxpayerApiResponse(gspApiResponse.httpStatusCode(),
		        gspApiResponse.headers(), gspApiResponse.body());
		return taxpayerApiResponse;
	}
}

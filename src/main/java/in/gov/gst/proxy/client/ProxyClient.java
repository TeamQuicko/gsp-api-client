package in.gov.gst.proxy.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import in.gov.gst.beans.ApiResponse;
import in.gov.gst.exception.GSTNException;
import in.gov.gst.mapper.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public abstract class ProxyClient
{

	public static boolean ENABLE_LOGGING = true;

	public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static String HEADER_ATTRIBUTE_STATE_CD = "state-cd";

	public static String HEADER_ATTRIBUTE_TXN_ID = "txn";

	public static String HEADER_ATTRIBUTE_IP = "ip-usr";

	public static String QUERY_ATTRIBUTE_ACTION = "action";

	public static String QUERY_ATTRIBUTE_APP_KEY = "app_key";

	public static String QUERY_ATTRIBUTE_GSTIN = "gstin";

	public static String PAYLOAD_ATTRIBUTE_ACTION = "action";

	public static String PAYLOAD_ATTRIBUTE_APP_KEY = "app_key";

	public static String QUERY_ATTRUBUTE_FINANCIAL_YEAR = "fy";

	public static String PAYLOAD_ATTRIBUTE_USERNAME = "username";

	public static String RESPONSE_PAYLOAD_ATTRUBUTE_STATUS_CODE = "status_cd";

	public static String RESPONSE_PAYLOAD_ATTRUBUTE_ERROR = "error";

	public static String RESPONSE_PAYLOAD_ATTRUBUTE_ERROR_ERROR_CODE = "error_cd";

	public static String RESPONSE_PAYLOAD_ATTRUBUTE_ERROR_MESSAGE = "message";

	public static String RESPONSE_PAYLOAD_ATTRUBUTE_DATA = "data";

	public static String RESPONSE_PAYLOAD_ATTRUBUTE_HMAC = "hmac";

	protected String appKey;

	protected String baseUrl;

	protected String encryptedAppKey;

	protected ObjectMapper mapper;

	protected String whiteListedIpAddress;

	protected OkHttpClient client;

	protected OkHttpClient getClient()
	{
		return client;
	}

	public ProxyClient(final OkHttpClient client, final String appKey, final String encryptedAppKey,
	        final String baseUrl, final String whiteListedIpAddress)
	{
		this.client = client;
		this.appKey = appKey;
		this.encryptedAppKey = encryptedAppKey;
		this.baseUrl = baseUrl;
		this.whiteListedIpAddress = whiteListedIpAddress;
		this.mapper = new ObjectMapper();
	}

	protected static in.gov.gst.beans.ApiResponse validateAndParse(Response response)
	        throws JSONException, IOException, GSTNException
	{
		final int httpStatusCode = response.code();
		final Map<String, String> headers = new HashMap<String, String>();
		final JSONObject body = new JSONObject(response.body().string());
		response.headers().toMultimap().forEach((k, v) -> headers.put(k, v.get(0)));
		final ApiResponse apiResponse = new ApiResponse(httpStatusCode, headers, body);
		if (httpStatusCode != 200)
		{
			throw new GSTNException(apiResponse);
		}
		return apiResponse;
	}
}

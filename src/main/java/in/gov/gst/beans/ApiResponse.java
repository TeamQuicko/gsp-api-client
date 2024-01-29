package in.gov.gst.beans;

import java.io.Serializable;
import java.util.Map;

import org.json.JSONObject;

import in.gov.gst.proxy.client.ProxyClient;

public class ApiResponse implements Serializable
{

	private static final long serialVersionUID = 5327480854057572947L;

	protected int httpStatusCode;

	protected Map<String, String> headers;

	protected JSONObject response;

	public ApiResponse(final int httpStatusCode, final Map<String, String> headers, final JSONObject response)
	{
		this.httpStatusCode = httpStatusCode;
		this.headers = headers;
		this.response = response;
	}

	public String getData()
	{
		if (response.has(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_DATA))
		{
			return response.getString(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_DATA);
		}
		return null;
	}

	public Integer getGspStatusCode()
	{
		if (response.has(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_STATUS_CODE))
		{
			return Integer.valueOf(response.getString(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_STATUS_CODE));
		}
		return null;
	}

	public String getHMAC()
	{
		if (response.has(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_HMAC))
		{
			return response.getString(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_HMAC);
		}
		return null;
	}

	public String getErrorCode()
	{
		if (response.has(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR)
		        && response.getJSONObject(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR)
		                .has(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR_ERROR_CODE))
		{
			return response.getJSONObject(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR)
			        .getString(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR_ERROR_CODE);
		}
		return null;
	}

	public String getErrorMessage()
	{
		if (response.has(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR)
		        && response.getJSONObject(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR)
		                .has(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR_MESSAGE))
		{
			return response.getJSONObject(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR)
			        .getString(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_ERROR_MESSAGE);
		}
		return null;
	}

	public JSONObject body()
	{
		return response;
	}

	public Map<String, String> headers()
	{
		return headers;
	}

	public int httpStatusCode()
	{
		return httpStatusCode;
	}
}

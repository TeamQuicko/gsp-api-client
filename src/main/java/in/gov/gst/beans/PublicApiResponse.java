package in.gov.gst.beans;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import in.gov.gst.util.ResponseUtil;

public class PublicApiResponse extends ApiResponse
{

	private static final long serialVersionUID = -7545858626659395829L;

	public PublicApiResponse(final int httpStatusCode, final Map<String, String> headers, final JSONObject response)
	{
		super(httpStatusCode, headers, response);
	}

	public JSONObject getDecode() throws JSONException, UnsupportedEncodingException
	{
		return new JSONObject(ResponseUtil.decodeb64Payload(this.getData()));
	}
}

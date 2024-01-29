package in.gov.gst.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.gst.beans.ApiResponse;

public class GSTNException extends Throwable
{

	/** TODO Auto-generated JavaDoc */
	private static final long serialVersionUID = 5445356178632733913L;

	@JsonProperty("data")
	private ApiResponse apiResponse;

	public GSTNException(final ApiResponse apiResponse)
	{
		this.apiResponse = apiResponse;
	}

	public String getErrorMessage()
	{
		return apiResponse.getErrorMessage();
	}

	public String getErrorCode()
	{
		return apiResponse.getErrorCode();
	}

	@Override
	public String getMessage()
	{
		return "code: ".concat(this.getErrorCode()).concat(", msg: ").concat(this.getErrorMessage());
	}

	public ApiResponse getApiResponse()
	{
		return apiResponse;
	}

}

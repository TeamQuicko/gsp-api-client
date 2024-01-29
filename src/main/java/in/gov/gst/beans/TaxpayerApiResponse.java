package in.gov.gst.beans;

import java.util.Map;

import org.json.JSONObject;

import in.gov.gst.auth.beans.TaxpayerSession;
import in.gov.gst.exception.CryptoException;
import in.gov.gst.exception.VerificationException;
import in.gov.gst.proxy.client.taxpayer.ProxyClient;
import in.gov.gst.util.ResponseUtil;

public class TaxpayerApiResponse extends ApiResponse
{

	private static final long serialVersionUID = 3730256520534343341L;

	public TaxpayerApiResponse(final int httpStatusCode, final Map<String, String> headers, final JSONObject response)
	{
		super(httpStatusCode, headers, response);
	}

	public String getRek()
	{
		if (response.has(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_REK))
		{
			return response.getString(ProxyClient.RESPONSE_PAYLOAD_ATTRUBUTE_REK);
		}
		return null;
	}

	public static JSONObject decrypt(final TaxpayerSession taxpayerSession,
	        final TaxpayerApiResponse taxpayerApiResponse) throws CryptoException
	{

		final String decryptedData = ResponseUtil.decrypt(taxpayerSession.getAppKey(), taxpayerSession.getSek(),
		        taxpayerApiResponse.getRek(), taxpayerApiResponse.getData());

		return new JSONObject(decryptedData);

	}

	public JSONObject decryptwithHMACVerification(final TaxpayerSession taxpayerSession)
	        throws CryptoException, VerificationException
	{

		final String decryptedData = ResponseUtil.decryptwithHMACVerification(taxpayerSession.getAppKey(),
		        taxpayerSession.getSek(), this.getRek(), this.getData(), this.getHMAC());

		return new JSONObject(decryptedData);

	}

}

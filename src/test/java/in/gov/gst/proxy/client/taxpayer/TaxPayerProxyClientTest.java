package in.gov.gst.proxy.client.taxpayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import in.gov.gst.auth.beans.TaxpayerSession;
import in.gov.gst.beans.TaxpayerApiResponse;
import in.gov.gst.exception.CryptographyException;
import in.gov.gst.exception.GSTNException;
import in.gov.gst.proxy.client.ProxyClientTest;
import in.gov.gst.type.ENDPOINTS.URLPath;

@TestInstance(Lifecycle.PER_CLASS)
public class TaxPayerProxyClientTest extends ProxyClientTest
{

	protected String PAN = "DMOPD7318K";

	protected String USERNAME = "TN_NT2.2304";

	protected ProxyClient taxpayerClient;

	protected TaxpayerSession taxpayerSession;

	@BeforeAll
	public void taxpayerProxyClientSetup()
	{

		this.taxpayerClient =
		        new ProxyClient(appKey, encryptedAppKey, BASE_URL, WHITE_LISTED_IP, CLIENT_ID, CLIENT_SECRET);
		this.taxpayerAuthenticate();
	}

	void taxpayerAuthenticate()
	{
		try
		{
			this.taxpayerClient.generateOtp("v1.0", USERNAME, GSTIN);
			TaxpayerApiResponse apiResponse = this.taxpayerClient.verifyOtp("v1.0", USERNAME, GSTIN, "575757");
			this.taxpayerSession = new TaxpayerSession(this.encryptedAppKey, this.appKey, this.USERNAME,
			        apiResponse.body().getString("auth_token"), apiResponse.body().getString("sek"), this.GSTIN,
			        apiResponse.body().getLong("expiry"));

		}
		catch (IOException | JSONException | CryptographyException | GSTNException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void ShouldGetGST1B2B() throws JSONException, GSTNException, CryptographyException, IOException
	{
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put(ProxyClient.QUERY_ATTRIBUTE_TAXPAYER_RETURN_PERIOD, "022023");
		requestParams.put(ProxyClient.QUERY_ATTRIBUTE_ACTION, "AT");

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put(ProxyClient.HEADER_ATTRIBUTE_TAXPAYER_RETURN_PERIOD, "022023");

		TaxpayerApiResponse apiResponse = this.taxpayerClient.get(taxpayerSession, URLPath.GSTN_TAX_PAYER_RETURNS_GSTR1,
		        "v3.1", requestParams, requestHeaders);

		System.out.println(apiResponse.body().toString());
	}
}

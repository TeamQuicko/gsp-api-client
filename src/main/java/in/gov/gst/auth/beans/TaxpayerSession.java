package in.gov.gst.auth.beans;

public class TaxpayerSession
{

	private String authToken;

	private String sek;

	private String appKey;

	private String encryptedAppKey;

	private String userName;

	private String gstin;

	private Long expiry;

	public TaxpayerSession(final String encryptedAppKey, final String appKey, final String userName,
	        final String authToken, final String sek, final String gstin, final Long expiry)
	{
		this.encryptedAppKey = encryptedAppKey;

		this.authToken = authToken;

		this.sek = sek;

		this.appKey = appKey;

		this.userName = userName;

		this.gstin = gstin;

		this.expiry = expiry;
	}

	public String getAuthToken()
	{
		return authToken;
	}

	public void setAuthToken(String authToken)
	{
		this.authToken = authToken;
	}

	public String getSek()
	{
		return sek;
	}

	public void setSek(String sek)
	{
		this.sek = sek;
	}

	public String getEncryptedAppKey()
	{
		return encryptedAppKey;
	}

	public void setEncryptedAppKey(String encryptedAppKey)
	{
		this.encryptedAppKey = encryptedAppKey;
	}

	public String getAppKey()
	{
		return appKey;
	}

	public void setAppKey(String appKey)
	{
		this.appKey = appKey;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getGstin()
	{
		return gstin;
	}

	public void setGstin(String gstin)
	{
		this.gstin = gstin;
	}

	public Long getExpiry()
	{
		return expiry;
	}

	public void setExpiry(Long expiry)
	{
		this.expiry = expiry;
	}
}

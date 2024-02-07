package in.gov.gst.auth.beans;

public class PublicSession
{

	private String authToken;

	private String sek;

	private String appKey;

	private String encryptedAppKey;

	private String username;

	public PublicSession(final String encryptedAppKey, final String appKey, final String authToken, final String sek,
	        final String username)
	{
		this.encryptedAppKey = encryptedAppKey;

		this.authToken = authToken;

		this.sek = sek;

		this.appKey = appKey;

		this.username = username;
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
		return username;
	}

	public void setUserName(String username)
	{
		this.username = username;
	}
}

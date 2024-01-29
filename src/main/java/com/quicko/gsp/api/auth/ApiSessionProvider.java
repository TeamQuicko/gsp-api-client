package com.quicko.gsp.api.auth;

import com.auth0.jwt.JWT;
import com.quicko.gsp.api.client.GSPHTTPClient;
import com.quicko.gsp.api.exception.AuthorizationException;
import com.quicko.gsp.api.type.ENDPOINTS;
import com.quicko.gsp.api.type.ENDPOINTS.URLPath;
import com.quicko.gsp.api.type.Environment;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiSessionProvider
{

	public static String USER_AGENT = "User-Agent";

	public static String USER_AGENT_VALUE = "java/com.quicko.gsp/api-client/1.0.0";

	private ApiUserCredentials apiUserCredentials;

	private String accessToken;

	private String refreshToken;

	protected OkHttpClient client;

	private ApiSession apiSession;

	public ApiSessionProvider(final ApiUserCredentials apiUserCredentials) throws AuthorizationException
	{
		this.apiUserCredentials = apiUserCredentials;

		final OkHttpClient.Builder builder = new OkHttpClient.Builder();

		final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		this.client = builder.connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES)
		        .writeTimeout(1, TimeUnit.MINUTES).addInterceptor(logging).build();

		this.apiSession = new ApiSession(this.apiUserCredentials.getApiKey(), this.getAccessToken());

	}

	public ApiSession provide() throws AuthorizationException
	{
		this.apiSession.setAccessToken(this.getAccessToken());

		return this.apiSession;
	}

	public String getApiKey()
	{
		return this.apiUserCredentials.getApiKey();
	}

	private String getAccessToken() throws AuthorizationException
	{
		if (this.accessToken == null)
		{
			this.authenticate();
		}
		else if (JWT.decode(this.accessToken).getExpiresAt().before(new Date())
		        && JWT.decode(this.refreshToken).getExpiresAt().after(new Date()))
		{
			this.authorize();
		}
		else if (JWT.decode(this.refreshToken).getExpiresAt().before(new Date()))
		{
			this.authenticate();
		}

		return this.accessToken;
	}

	private void authenticate() throws AuthorizationException
	{
		try
		{

			final FormBody.Builder builder = new FormBody.Builder();

			final RequestBody requestBody = builder.build();

			final Request request = new Request.Builder()
			        .url(ENDPOINTS.build(Environment.get(apiUserCredentials.getApiKey()),
			                URLPath.QUICKO_GSP_AUTHENTICATE))
			        .post(requestBody).header(USER_AGENT, USER_AGENT_VALUE)
			        .header("x-api-key", apiUserCredentials.getApiKey())
			        .header("x-api-secret", apiUserCredentials.getApiSecret()).build();

			final Response response = client.newCall(request).execute();
			final String transactionId = response.header(GSPHTTPClient.QUICKO_GSP_HEADER_ATTRIBUTE_TRANSACTION_ID);
			if (response.header("Content-Type").contains("json"))
			{
				final JSONObject jsonObject = new JSONObject(response.body().string());

				if (response.code() != 200)
				{
					throw new AuthorizationException(jsonObject.get("error").toString(), transactionId);
				}

				this.accessToken = jsonObject.getString("access_token");
				this.refreshToken = jsonObject.getString("refresh_token");
			}
			else
			{
				throw new AuthorizationException("Unexpected content type received from server: "
				        + response.header("Content-Type") + " " + response.body().string(), transactionId);
			}
		}
		catch (final IOException | JSONException ioE)
		{
			throw new AuthorizationException("Failed to authenticate", ioE);
		}
	}

	private void authorize() throws AuthorizationException
	{
		try
		{
			final FormBody.Builder builder = new FormBody.Builder();

			final RequestBody requestBody = builder.build();

			final Request request = new Request.Builder()
			        .url(ENDPOINTS.build(Environment.get(apiUserCredentials.getApiKey()), URLPath.QUICKO_GSP_AUTHORIZE))
			        .post(requestBody).header(USER_AGENT, USER_AGENT_VALUE)
			        .header("x-api-key", apiUserCredentials.getApiKey()).header("Authorization", this.refreshToken)
			        .build();

			final Response response = client.newCall(request).execute();

			final String transactionId = response.header(GSPHTTPClient.QUICKO_GSP_HEADER_ATTRIBUTE_TRANSACTION_ID);
			if (response.header("Content-Type").contains("json"))
			{

				final JSONObject jsonObject = new JSONObject(response.body().string());

				if (response.code() != 200)
				{
					throw new AuthorizationException(jsonObject.get("error").toString(), transactionId);
				}

				this.accessToken = jsonObject.getString("access_token");
			}
			else
			{
				throw new AuthorizationException("Unexpected content type received from server: "
				        + response.header("Content-Type") + " " + response.body().string(), transactionId);
			}
		}
		catch (final IOException | JSONException ioE)
		{
			throw new AuthorizationException("Failed to authorize", ioE);
		}

	}
}

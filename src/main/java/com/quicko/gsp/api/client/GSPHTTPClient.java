package com.quicko.gsp.api.client;

import com.quicko.gsp.api.auth.ApiSession;
import com.quicko.gsp.api.auth.ApiSessionProvider;
import com.quicko.gsp.api.exception.AuthorizationException;

import java.io.IOException;

import org.json.JSONObject;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class GSPHTTPClient extends OkHttpClient
{

	private static OkHttpClient INSTANCE;

	public static String QUICKO_GSP_HEADER_USER_AGENT = "User-Agent";

	public static String QUICKO_GSP_HEADER_USER_AGENT_VALUE = "java/com.quicko.gsp/api-client/1.0.0";

	public static String QUICKO_GSP_HEADER_AUTHORIZATION = "Authorization";

	public static String QUICKO_GSP_HEADER_API_KEY = "x-api-key";

	public static String QUICKO_GSP_HEADER_ATTRIBUTE_TRANSACTION_ID = "x-transaction-id";

	private GSPHTTPClient()
	{
	}

	public static synchronized OkHttpClient getInstance()
	{
		return INSTANCE;
	}

	public static synchronized OkHttpClient build(final ApiSessionProvider sessionProvider,
	        HttpLoggingInterceptor.Level level)
	{

		final ResponseInterceptor responseInterceptor = new ResponseInterceptor(sessionProvider);
		final RequestInterceptor requestInterceptor = new RequestInterceptor(sessionProvider);

		final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

		logging.setLevel(HttpLoggingInterceptor.Level.BODY);

		final OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(logging)
		        .addInterceptor(requestInterceptor).addInterceptor(responseInterceptor).build();

		INSTANCE = httpClient;
		return httpClient;
	}

	static class ResponseInterceptor implements Interceptor
	{

		protected ApiSessionProvider sessionProvider;

		public ResponseInterceptor(ApiSessionProvider sessionProvider)
		{
			this.sessionProvider = sessionProvider;
		}

		@Override
		public Response intercept(final Interceptor.Chain chain) throws IOException
		{
			final Request request = chain.request();

			final Response response = chain.proceed(request);

			switch (response.code())
			{
				case 403:

					final JSONObject responseJSON = new JSONObject(response.body().string());
					final String errorMessage;
					if (responseJSON.has("error"))
					{
						errorMessage = responseJSON.getString("error");
					}
					else
					{
						ResponseBody body =
						        ResponseBody.create(responseJSON.toString(), MediaType.parse("application/json"));

						Response responseObject = response.newBuilder().body(body).build();

						return responseObject;
					}

					switch (errorMessage)
					{

						case "Authorization token missing":
						case "Access token has expired":
							// Retry
							try
							{
								final Response retryResponse =
								        chain.proceed(request.newBuilder().header(QUICKO_GSP_HEADER_AUTHORIZATION,
								                sessionProvider.provide().getAccessToken()).build());

								return retryResponse;
							}
							catch (AuthorizationException e)
							{
								throw new IOException(e.getMessage(), e);
							}
						case "API key not active":
						case "Insufficient privilege":
						case "Invalid access token":
						default:

							ResponseBody body =
							        ResponseBody.create(responseJSON.toString(), MediaType.parse("application/json"));

							Response responseObject = response.newBuilder().body(body).build();

							return responseObject;

					}

				default:
					break;

			}

			return response;
		}

	}

	static class RequestInterceptor implements Interceptor
	{

		protected ApiSessionProvider sessionProvider;

		public RequestInterceptor(ApiSessionProvider sessionProvider)
		{
			this.sessionProvider = sessionProvider;
		}

		@Override
		public Response intercept(final Interceptor.Chain chain) throws IOException
		{
			final Request request = chain.request();
			try
			{
				final ApiSession apiSession = sessionProvider.provide();

				final Response response = chain.proceed(
				        request.newBuilder().header(QUICKO_GSP_HEADER_AUTHORIZATION, apiSession.getAccessToken())
				                .header(QUICKO_GSP_HEADER_USER_AGENT, QUICKO_GSP_HEADER_USER_AGENT_VALUE)
				                .header(QUICKO_GSP_HEADER_API_KEY, apiSession.getApiKey()).build());

				return response;
			}
			catch (AuthorizationException e)
			{
				throw new IOException(e.getMessage(), e);
			}
		}
	}
}

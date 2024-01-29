package in.gov.gst.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.json.JSONException;
import org.json.JSONObject;

import in.gov.gst.exception.CryptographyException;

public class RequestUtil
{

	private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";

	private static final String AES_ALGORITHM = "AES";

	private static final int ENC_BITS = 256;

	private static final String CHARACTER_ENCODING = "UTF-8";

	private static Cipher ENCRYPT_CIPHER;

	private static Cipher DECRYPT_CIPHER;

	private static KeyGenerator KEY_GENERATOR;

	private static byte[] ek;

	static
	{
		try
		{
			ENCRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION);
			DECRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION);
			KEY_GENERATOR = KeyGenerator.getInstance(AES_ALGORITHM);
			KEY_GENERATOR.init(ENC_BITS);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			//
		}
	}

	public static String encryptAppKey(final String newAppKey, final String appKey, final String sek)
	        throws CryptographyException
	{
		try
		{
			ek = generatePaddedSek(sek, appKey);

			final String encAppKey = encryptEK(decodeBase64StringTOByte(newAppKey), ek);

			return encAppKey;

		}
		catch (final JSONException | IOException | InvalidKeyException | IllegalBlockSizeException
		        | BadPaddingException ex)
		{
			throw new CryptographyException("Failed to encrypt app Key", ex);
		}
	}

	public static JSONObject encrypt(final String sek, final String appKey, final String data, final String action)
	        throws CryptographyException
	{
		try
		{
			ek = generatePaddedSek(sek, appKey);

			final byte[] base64Data = getJsonBase64Payload(data);
			final String hmac = BCHmac(base64Data, ek);
			final String encData = encryptEK(base64Data, ek);

			final JSONObject obj = new JSONObject();

			obj.put("data", encData);
			obj.put("hmac", hmac);
			obj.put("action", action);

			return obj;

		}
		catch (final JSONException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
		        | IOException ex)
		{
			throw new CryptographyException("Failed to encrypt data", ex);
		}
	}

	public static JSONObject encrypt(final String sek, final String appKey, final String data, final String action,
	        final String pan, final String otp) throws CryptographyException
	{
		try
		{
			String sid = new StringBuilder(pan.toUpperCase()).append("|").append(otp).toString();

			ek = generatePaddedSek(sek, appKey);

			final byte[] base64Data = getJsonBase64Payload(data);

			// If st =EVC,sign will be HMAC-SHA256 of base64 of summary payload using encoded base 64 {PAN+ OTP}

			// Do not base64 encode sid as per discussion
			// https://groups.google.com/forum/?hl=en#!searchin/gst-suvidha-provider-gsp-discussion-group/file$20gstr|sort:date/gst-suvidha-provider-gsp-discussion-group/u3yZx1zrVTQ/8A1nsQEhBwAJ
			// final String sign = BCHmac(base64Data, Base64.encodeBase64(sid.getBytes()));
			final String sign = BCHmac(base64Data, sid.getBytes());

			final String encData = encryptEK(base64Data, ek);

			final JSONObject obj = new JSONObject();

			obj.put("sid", sid);
			obj.put("st", "EVC");
			obj.put("data", encData);
			obj.put("sign", sign);
			obj.put("action", action);

			return obj;

		}
		catch (final JSONException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
		        | IOException ex)
		{
			throw new CryptographyException("Failed to encrypt data", ex);
		}
	}

	private static String encodeBase64String(final byte[] bytes)
	{
		return new String(java.util.Base64.getEncoder().encode(bytes));
	}

	private static byte[] decodeBase64StringTOByte(final String stringData) throws UnsupportedEncodingException
	{
		return java.util.Base64.getDecoder().decode(stringData.getBytes(CHARACTER_ENCODING));
	}

	private static String encryptEK(final byte[] plaintext, final byte[] secret) throws CryptographyException
	{
		final SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		try
		{
			ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, sk);
			return Base64.encodeBase64String(ENCRYPT_CIPHER.doFinal(plaintext));
		}
		catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e)
		{
			throw new CryptographyException("Failed to encrypt data", e);
		}

	}

	private static byte[] decrypt(final String plaintext, final byte[] secret)
	        throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		final SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, sk);
		return DECRYPT_CIPHER.doFinal(Base64.decodeBase64(plaintext));
	}

	private static byte[] generatePaddedSek(final String sek, final String decAppKey) throws InvalidKeyException,
	        IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException
	{
		final byte[] decSek = decrypt(sek, decodeBase64StringTOByte(decAppKey));
		return decSek;
	}

	private static String BCHmac(final byte[] data, final byte[] Ek)
	{
		final HMac hmac = new HMac(new SHA256Digest());

		final byte[] resBuf = new byte[hmac.getMacSize()];
		hmac.init(new KeyParameter(Ek));
		hmac.update(data, 0, data.length);
		hmac.doFinal(resBuf, 0);

		return encodeBase64String(resBuf);
	}

	private static byte[] getJsonBase64Payload(final String str) throws JSONException
	{

		JSONObject obj = null;
		obj = new JSONObject(str);

		return Base64.encodeBase64(obj.toString().getBytes());
	}
}
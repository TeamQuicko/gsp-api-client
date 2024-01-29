package in.gov.gst.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;

import in.gov.gst.exception.CryptographyException;

public class OtpUtil
{

	public static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";

	public static final String AES_ALGORITHM = "AES";

	public static final int ENC_BITS = 256;

	public static final String CHARACTER_ENCODING = "UTF-8";

	private static Cipher ENCRYPT_CIPHER;

	static
	{
		try
		{
			ENCRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			//
		}
	}

	private static String encryptOTP(final byte[] plaintext, final byte[] secret)
	        throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		final SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, sk);
		return Base64.encodeBase64String(ENCRYPT_CIPHER.doFinal(plaintext));

	}

	public static String encrypt(final String appKey, final String otp) throws CryptographyException
	{
		try
		{
			final byte[] decryptedAppKey = java.util.Base64.getDecoder().decode(appKey.getBytes(CHARACTER_ENCODING));
			return encryptOTP(otp.getBytes(), decryptedAppKey);
		}
		catch (JSONException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
		        | UnsupportedEncodingException e)
		{

			throw new CryptographyException("Failed to Encrypt data", e);
		}
	}

}
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

import in.gov.gst.exception.CryptoException;

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

	private static byte[] decodeBase64StringTOByte(final String stringData) throws UnsupportedEncodingException
	{
		return java.util.Base64.getDecoder().decode(stringData.getBytes(CHARACTER_ENCODING));
	}

	private static String encryptEK(final byte[] plaintext, final byte[] secret)
	        throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		final SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, sk);
		return Base64.encodeBase64String(ENCRYPT_CIPHER.doFinal(plaintext));

	}

	public static String encrypt(final String appKey, final String otp) throws CryptoException
	{
		try
		{
			return encryptEK(otp.getBytes(), decodeBase64StringTOByte(appKey));
		}
		catch (JSONException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
		        | UnsupportedEncodingException e)
		{

			throw new CryptoException("Failed to Encrypt data", e);
		}
	}

}
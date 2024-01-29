package in.gov.gst.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import in.gov.gst.exception.CryptoException;
import in.gov.gst.exception.VerificationException;

public class ResponseUtil
{

	private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";

	private static final String AES_ALGORITHM = "AES";

	private static final int ENC_BITS = 256;

	private static final String CHARACTER_ENCODING = "UTF-8";

	private static Cipher DECRYPT_CIPHER;

	private static KeyGenerator KEY_GENERATOR;

	static
	{
		try
		{
			DECRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION);
			KEY_GENERATOR = KeyGenerator.getInstance(AES_ALGORITHM);
			KEY_GENERATOR.init(ENC_BITS);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
	}

	private static byte[] decodeBase64StringToByte(final String stringData) throws UnsupportedEncodingException
	{
		return java.util.Base64.getDecoder().decode(stringData.getBytes(CHARACTER_ENCODING));
	}

	private static byte[] decrypt(final String plaintext, final byte[] secret)
	        throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		final SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, sk);
		return DECRYPT_CIPHER.doFinal(Base64.decodeBase64(plaintext));
	}

	private static byte[] generatePaddedSek(final String sek, final String decAppKey)
	        throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
	{
		final byte[] decSek = decrypt(sek, decodeBase64StringToByte(decAppKey));
		return decSek;
	}

	private static byte[] genDecryptedREK(final String rek, final byte[] ek)
	        throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		final byte[] encRek = decrypt(rek, ek);
		return encRek;
	}

	private static String decryptGstrData(final String gstrResp, final byte[] encRek)
	        throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException
	{
		final String originalData = new String(decodeBase64StringToByte((new String((decrypt(gstrResp, encRek))))));
		return originalData;
	}

	private static void HMACVerify(final String data, final byte[] verificationKey, final String hmac)
	        throws VerificationException
	{
		String HMAC_SHA256_ALGORITHM = "HmacSHA256";
		String result = null;
		try
		{
			Key signingKey = new SecretKeySpec(verificationKey, HMAC_SHA256_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(Base64.encodeBase64String(data.getBytes()).getBytes());
			result = Base64.encodeBase64String(rawHmac);
			if (!result.equals(hmac))
			{
				throw new VerificationException("Incorrect HMAC");
			}
		}
		catch (final NoSuchAlgorithmException | InvalidKeyException e)
		{
			throw new VerificationException("Failed to verify response");
		}

	}

	public static String decrypt(final String appKey, final String sek, final String rek, final String data)
	        throws CryptoException
	{
		try
		{
			final byte[] paddedSEK = generatePaddedSek(sek, appKey);

			final byte[] decryptedREK = genDecryptedREK(rek, paddedSEK);

			return decryptGstrData(data, decryptedREK);
		}
		catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e)
		{
			throw new CryptoException("Failed to decrypt Data");
		}

	}

	public static String decryptwithHMACVerification(final String appKey, final String sek, final String rek,
	        final String data, final String hmac) throws CryptoException, VerificationException
	{
		try
		{
			final byte[] paddedSEK = generatePaddedSek(sek, appKey);

			final byte[] decryptedREK = genDecryptedREK(rek, paddedSEK);

			final String decrytedData = decryptGstrData(data, decryptedREK);

			HMACVerify(decrytedData, decryptedREK, hmac);

			return decrytedData;
		}
		catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e)
		{
			throw new CryptoException("Failed to decrypt Data");
		}

	}

	public static String decodeb64Payload(final String payload) throws UnsupportedEncodingException
	{
		return new String(decodeBase64StringToByte(payload));
	}
}
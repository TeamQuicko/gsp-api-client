package in.gov.gst.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AppKeyUtil
{

	public static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";

	public static final String AES_ALGORITHM = "AES";

	public static final int ENC_BITS = 256;

	public static final String CHARACTER_ENCODING = "UTF-8";

	private static Cipher DECRYPT_CIPHER;

	private static KeyGenerator KEYGEN;

	static
	{
		try
		{
			DECRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION);
			KEYGEN = KeyGenerator.getInstance(AES_ALGORITHM);
			KEYGEN.init(ENC_BITS);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
	}

	private static byte[] decodeBase64StringToByte(final String payload) throws UnsupportedEncodingException
	{
		return java.util.Base64.getDecoder().decode(payload.getBytes(CHARACTER_ENCODING));
	}

	public static byte[] decrypt(final String encryptedPayload, final byte[] secret)
	        throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		final SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, sk);
		return DECRYPT_CIPHER.doFinal(Base64.decodeBase64(encryptedPayload));
	}

	public static String generateSecureKey()
	{
		final SecretKey secretKey = KEYGEN.generateKey();
		return new String(java.util.Base64.getEncoder().encode(secretKey.getEncoded()));
	}

	public static String encrypt(PublicKey publicKey, final byte[] payload) throws Exception, NoSuchAlgorithmException,
	        NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{

		final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		final byte[] encryptedByte = cipher.doFinal(payload);
		final String encodedString = new String(java.util.Base64.getEncoder().encode(encryptedByte));
		return encodedString;
	}

	public static String encryptAppkey(PublicKey publicKey, final String appKey) throws InvalidKeyException,
	        NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception
	{
		return encrypt(publicKey, decodeBase64StringToByte(appKey));
	}

}
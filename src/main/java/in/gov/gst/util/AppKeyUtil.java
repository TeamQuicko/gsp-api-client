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

	private static Cipher ENCRYPT_CIPHER;

	private static Cipher DECRYPT_CIPHER;

	private static KeyGenerator KEYGEN;

	static
	{
		try
		{
			ENCRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION);
			DECRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION);
			KEYGEN = KeyGenerator.getInstance(AES_ALGORITHM);
			KEYGEN.init(ENC_BITS);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
	}

	private static String encodeBase64String(final byte[] bytes)
	{
		return new String(java.util.Base64.getEncoder().encode(bytes));
	}

	public static byte[] decodeBase64StringToByte(final String stringData) throws UnsupportedEncodingException
	{
		return java.util.Base64.getDecoder().decode(stringData.getBytes(CHARACTER_ENCODING));
	}

	private static String encryptEK(final byte[] plainText, final byte[] secret)
	        throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException
	{
		final SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, sk);
		return Base64.encodeBase64String(ENCRYPT_CIPHER.doFinal(plainText));

	}

	public static byte[] decrypt(final String plainText, final byte[] secret)
	        throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		final SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, sk);
		return DECRYPT_CIPHER.doFinal(Base64.decodeBase64(plainText));
	}

	public static String generateSecureKey()
	{
		final SecretKey secretKey = KEYGEN.generateKey();
		return encodeBase64String(secretKey.getEncoded());
	}

	public static String encrypt(PublicKey publicKey, final byte[] bytes) throws Exception, NoSuchAlgorithmException,
	        NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{

		final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		final byte[] encryptedByte = cipher.doFinal(bytes);
		final String encodedString = new String(java.util.Base64.getEncoder().encode(encryptedByte));
		return encodedString;
	}

	public static String encryptAppkey(PublicKey publicKey, final String appKey) throws InvalidKeyException,
	        NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, Exception
	{
		return encrypt(publicKey, decodeBase64StringToByte(appKey));
	}

}
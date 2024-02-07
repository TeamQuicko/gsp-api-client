package in.gov.gst.proxy.client;

import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import in.gov.gst.util.AppKeyUtil;

@TestInstance(Lifecycle.PER_CLASS)
public class ProxyClientTest
{

	protected String GSTIN = "33DMOPD7318K1ZT";

	protected String STATE_CD = "33";

	protected String BASE_URL = "https://devapi.gst.gov.in";

	protected String WHITE_LISTED_IP = "137.232.3.35";

	protected String CLIENT_ID = "l7xx4c8835dc0fe64433933b756b23582f02";

	protected String CLIENT_SECRET = "629da1a56eca4bf488e560df4f9dbad1";

	protected String commonPassword = "Api@706com";

	protected String commonUsername = "commonapiuser";

	protected String appKey;

	protected String encryptedAppKey;

	protected String encryptedCommonPassword;

	@BeforeAll
	void proxyClientSetup() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
	        IllegalBlockSizeException, BadPaddingException, Exception
	{

		FileInputStream fin = new FileInputStream(
		        "C:\\Users\\kishan.dhrangadhariy\\Downloads\\GST_Sandbox_Latest_Public_key\\GST_Sandbox_Latest_Public_key\\GSTN_G2B_SANDBOX_UAT_public.cert.cer");

		CertificateFactory f = CertificateFactory.getInstance("X.509");

		X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);

		PublicKey pk = certificate.getPublicKey();

		appKey = AppKeyUtil.generateSecureKey();

		encryptedAppKey = AppKeyUtil.encryptAppkey(pk, appKey);

		encryptedCommonPassword = AppKeyUtil.encrypt(pk, commonPassword.getBytes("UTF-8"));

	}

}

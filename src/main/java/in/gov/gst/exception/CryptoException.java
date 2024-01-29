package in.gov.gst.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoException extends Exception
{

	private static final long serialVersionUID = -7564188491314522698L;

	private static final Logger logger = LoggerFactory.getLogger(CryptoException.class);

	public CryptoException(final String msg)
	{
		super(msg);
		logger.error(msg);
	}

	public CryptoException(final String msg, final Throwable cause)
	{
		super(msg, cause);
		logger.error(msg, cause);
	}

}

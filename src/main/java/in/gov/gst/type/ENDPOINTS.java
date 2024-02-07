package in.gov.gst.type;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.gov.gst.util.FilterUtils;

public abstract class ENDPOINTS
{

	public enum URLPath
	{

		GSTN_COMMONS_AUTHENTICATE("/commonapi/v0.2/authenticate"),

		GSTN_COMMONS_SEARCH_TAX_PAYER("/commonapi/{version}/search"),

		GSTN_COMMONS_TRACK_RETURNS("/commonapi/{version}/returns"),

		GSTN_TAX_PAYER("/taxpayerapi/{version}/authenticate"),

		GSTN_TAX_PAYER_LEDGER("/taxpayerapi/{version}/ledgers"),

		GSTN_TAX_PAYER_RETURNS("/taxpayerapi/{version}/returns"),

		GSTN_TAX_PAYER_RETURNS_FILE("/taxpayerapi/{version}/returns/gstr"),

		GSTN_TAX_PAYER_RETURNS_P2F("/taxpayerapi/{version}/returns/gstrptf"),

		GSTN_TAX_PAYER_RETURNS_GSTR1("/taxpayerapi/{version}/returns/gstr1"),

		GSTN_TAX_PAYER_RETURNS_GSTR2A("/taxpayerapi/{version}/returns/gstr2a"),

		GSTN_TAX_PAYER_RETURNS_GSTR2B("/taxpayerapi/{version}/returns/gstr2b"),

		GSTN_TAX_PAYER_RETURNS_GSTR3B("/taxpayerapi/{version}/returns/gstr3b"),

		GSTN_TAX_PAYER_RETURNS_GSTR4("/taxpayerapi/{version}/returns/gstr4"),

		GSTN_TAX_PAYER_RETURNS_GSTR4A("/taxpayerapi/{version}/returns/gstr4a"),

		GSTN_TAX_PAYER_RETURNS_GSTR4X("/taxpayerapi/{version}/returns/gstr4x"),

		GSTN_TAX_PAYER_RETURNS_GSTR5("/taxpayerapi/{version}/returns/gstr5"),

		GSTN_TAX_PAYER_RETURNS_GSTR6("/taxpayerapi/{version}/returns/gstr6"),

		GSTN_TAX_PAYER_RETURNS_GSTR6A("/taxpayerapi/{version}/returns/gstr6a"),

		GSTN_TAX_PAYER_RETURNS_GSTR7("/taxpayerapi/{version}/returns/gstr7"),

		GSTN_TAX_PAYER_RETURNS_GSTR8("/taxpayerapi/{version}/returns/gstr8"),

		GSTN_TAX_PAYER_RETURNS_GSTR9("/taxpayerapi/{version}/returns/gstr9"),

		GSTN_TAX_PAYER_RETURNS_GSTR9A("/taxpayerapi/{version}/returns/gstr9a"),

		GSTN_TAX_PAYER_RETURNS_GSTR9C("/taxpayerapi/{version}/returns/gstr9c"),

		GSTN_TAX_PAYER_RETURNS_GSTR10("/taxpayerapi/{version}/returns/gstr10"),

		GSTN_TAX_PAYER_RETURNS_ITC3("/taxpayerapi/{version}/returns/itc03"),

		GSTN_TAX_PAYER_RETURNS_ITC4("/taxpayerapi/{version}/returns/itc04"),

		GSTN_TAX_PAYER_RETURNS_TDS("/taxpayerapi/{version}/returns/tdstcscredit");

		private final String value;

		URLPath(final String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}
	}

	public static String build(String baseUrl, URLPath urlPath, Object... args)
	{
		String url = new StringBuilder().append(baseUrl).append(urlPath.getValue()).toString();
		String regex = "\\{(\\w*)\\}";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher;

		int i = 0;

		do
		{
			matcher = pattern.matcher(url);

			if (matcher.find())
			{
				url = url.replace(matcher.group(0),
				        args != null && args.length > i && args[i] != null && !args[i].toString().isEmpty()
				                ? (args[i] instanceof List || args[i] instanceof String[]
				                        ? FilterUtils.listToCSV((List<String>) args[i])
				                        : args[i].toString())
				                : "");
			}

			i++;

		}
		while (matcher.find());

		return url.toString();
	}
}

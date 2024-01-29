package in.gov.gst.util;

import java.util.List;

public class FilterUtils
{

	public static String listToCSV(final List<String> list) throws IllegalArgumentException
	{
		final StringBuilder csv = new StringBuilder();
		for (int index = 0; index < list.size(); index++)
		{
			csv.append(list.get(index));

			if (index != list.size() - 1)
			{
				csv.append(",");
			}
		}
		return csv.toString();
	}
}
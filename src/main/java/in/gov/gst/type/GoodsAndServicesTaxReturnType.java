package in.gov.gst.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GoodsAndServicesTaxReturnType
{

	GSTR_1("R1", "GSTR-1"),

	GSTR_1A("R1A", "GSTR-1A"),

	GSTR_2("R2", "GSTR-2"),

	GSTR_2A("R2A", "GSTR-2A"),

	GSTR_3("R3", "GSTR-3"),

	GSTR_3B("R3B", "GSTR-3B"),

	GSTR_4("R4", "GSTR-4"),

	GSTR_4X("R4X", "GSTR-4X"),

	GSTR_5("R5", "GSTR-5"),

	GSTR_6("R6", "GSTR-6"),

	GSTR_6A("R6A", "GSTR-6A"),

	GSTR_7("R7", "GSTR-7"),

	GSTR_8("R8", "GSTR-8"),

	GSTR_9("R9", "GSTR-9"),

	GSTR_9A("R9A", "GSTR-9A"),

	GSTR_9C("R9C", "GSTR-9C");

	private final String type;

	private final String value;

	GoodsAndServicesTaxReturnType(final String type, final String value)
	{
		this.type = type;
		this.value = value;
	}

	@JsonValue
	public String value()
	{
		return this.value;
	}

	@JsonValue
	public String type()
	{
		return this.type;
	}

	@JsonCreator
	public static GoodsAndServicesTaxReturnType fromValue(final String value)
	{
		for (final GoodsAndServicesTaxReturnType returnType : GoodsAndServicesTaxReturnType.values())
		{
			if (returnType.value.equalsIgnoreCase(value))
			{
				return returnType;
			}
		}
		throw new IllegalArgumentException(value);
	}

	@Override
	public String toString()
	{
		return this.value;

	}
}

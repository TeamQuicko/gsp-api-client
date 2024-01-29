package in.gov.gst.mapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

public class ObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper
{

	private static final long serialVersionUID = 1123276100501474040L;

	public ObjectMapper()
	{
		this.registerModule(new JodaModule());
		this.registerModule(new JsonOrgModule());
		this.setSerializationInclusion(Include.NON_NULL);

	}

}

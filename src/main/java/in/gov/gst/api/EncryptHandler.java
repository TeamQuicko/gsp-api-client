package in.gov.gst.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.gst.util.ResponseUtil;

@RestController
@RequestMapping("/decrypt")
public class EncryptHandler
{

	@PostMapping("")
	public ResponseEntity<Map<String, Object>> encrypt(@RequestBody final Map<String, Object> reqBody,
	        @RequestParam(value = "app_key", required = true) final String appKey,
	        @RequestParam(value = "sek", required = true) final String sek,
	        @RequestParam(value = "rek", required = true) final String rek)
	{

		final JSONObject reqJson = new JSONObject(reqBody);
		try
		{
			final JSONObject res = new JSONObject();
			res.put("data", ResponseUtil.decrypt(appKey, sek, rek, reqJson.toString()));

			ResponseEntity<Map<String, Object>> response =
			        new ResponseEntity<Map<String, Object>>(res.toMap(), HttpStatus.OK);
			return response;
		}
		catch (Exception e)
		{
			Map<String, Object> res;
			res = new HashMap<String, Object>();
			res.put("error", e.getClass().getName());
			res.put("message", e.getMessage());
			ResponseEntity<Map<String, Object>> response =
			        new ResponseEntity<Map<String, Object>>(res, HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}

	}

}

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

import in.gov.gst.util.RequestUtil;

@RestController
@RequestMapping("/encrypt")
public class DecryptHandler2
{

	@PostMapping("")
	public ResponseEntity<Map<String, Object>> encrypt(@RequestBody final Map<String, Object> reqBody,
	        @RequestParam(value = "app_key", required = true) final String appKey,
	        @RequestParam(value = "sek", required = true) final String sek,
	        @RequestParam(value = "action", required = true) final String action)
	{

		final JSONObject reqJson = new JSONObject(reqBody);
		Map<String, Object> res;
		try
		{
			res = RequestUtil.encrypt(sek, appKey, reqJson.toString(), action).toMap();

			ResponseEntity<Map<String, Object>> response = new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
			return response;
		}
		catch (Exception e)
		{
			res = new HashMap<String, Object>();
			res.put("error", e.getClass().getName());
			res.put("message", e.getMessage());
			ResponseEntity<Map<String, Object>> response =
			        new ResponseEntity<Map<String, Object>>(res, HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}

	}

	@PostMapping("/evc")
	public ResponseEntity<Map<String, Object>> encryptEvC(@RequestBody final Map<String, Object> reqBody,
	        @RequestParam(value = "app_key", required = true) final String appKey,
	        @RequestParam(value = "sek", required = true) final String sek,
	        @RequestParam(value = "action", required = true) final String action,
	        @RequestParam(value = "pan", required = true) final String pan,
	        @RequestParam(value = "otp", required = true) final String otp)
	{

		final JSONObject reqJson = new JSONObject(reqBody);
		Map<String, Object> res;
		try
		{
			res = RequestUtil.encrypt(sek, appKey, reqJson.toString(), action, pan, otp).toMap();

			ResponseEntity<Map<String, Object>> response = new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
			return response;
		}
		catch (Exception e)
		{
			res = new HashMap<String, Object>();
			res.put("error", e.getClass().getName());
			res.put("message", e.getMessage());
			ResponseEntity<Map<String, Object>> response =
			        new ResponseEntity<Map<String, Object>>(res, HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}

	}
}

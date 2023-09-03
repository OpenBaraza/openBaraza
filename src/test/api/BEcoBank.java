
import java.util.concurrent.TimeUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BEcoBank {
	
	public static void main(String[] args) {
		System.out.println("Starting");
		BEcoBank ecobank = new BEcoBank();

		String sURL = "https://academics.aua.ac.ke/aua/dataserver";
		String userName = "ecobank";
		String password = "228T846VL";
		String path = "?view=11:0&where=student_id='2006090024'";

		JSONObject jData = new JSONObject();

		String token = ecobank.authenticate(sURL, userName, password);
		JSONObject jResp = ecobank.makeCall(sURL + path, token, "read", jData.toString());
	}

	public static String authenticate(String myURL, String appKey, String appPass) {
		String auth = null;
		if(appKey == null || appPass == null) return auth;
		
		try {
			String authUser = Base64.getEncoder().encodeToString(appKey.getBytes("UTF-8"));
			String authPass = Base64.getEncoder().encodeToString(appPass.getBytes("UTF-8"));

			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
				.url(myURL)
				.get()
				.addHeader("action", "authorization")
				.addHeader("authUser", authUser)
				.addHeader("authPass", authPass)
				.addHeader("cache-control", "no-cache")
				.build();
			Response response = client.newCall(request).execute();
			String rBody = response.body().string();
			System.out.println(rBody);
			
			JSONObject jResp = new JSONObject(rBody);
			int ResultCode = jResp.getInt("ResultCode");
			if(jResp.has("ResultCode") && (jResp.getInt("ResultCode") == 0)) {
				auth = jResp.getString("access_token");
			}
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return auth;
	}
	
	
	public JSONObject makeCall(String url, String auth, String action, String jData) {
		JSONObject jResp = new JSONObject();
		
		try {			
System.out.println("BASE 2010 : " + jData);
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jData);
			Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("action", action)
				.addHeader("authorization", auth)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			String resp = response.body().string();
System.out.println(resp);

			jResp = new JSONObject(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return jResp;
	}


}

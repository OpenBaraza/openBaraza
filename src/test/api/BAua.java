
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

public class BAua {
	
	public static void main(String[] args) {
		System.out.println("Starting AUA test");
		BAua aua = new BAua();

		String sURL = "https://academics.aua.ac.ke/aua/dataserver";
		//String sURL = "https://portal.dewcis.com/hr/dataserver";
		String userName = "ecobank";
		String password = "228T846VL";

		String token = aua.authenticate(sURL, userName, password);
		System.out.println("Token : " + token);
		
		sURL = "https://academics.aua.ac.ke/aua/dataserver?view=11:0&where=student_id='S2010125'";
		aua.makeCall(sURL, token, "read", "{}");
		
		JSONObject jData = new JSONObject();
		jData.put("transaction_amount", "1.00");
		jData.put("customer_reference", "AU040621002");
		jData.put("student_id", "S2020001");
		jData.put("transaction_type", "01");
		jData.put("transaction_detail", "EMMANUEL ROTICH");
		sURL = "https://academics.aua.ac.ke/aua/dataserver?view=12:0";
		aua.makeCall(sURL, token, "data", jData.toString());

	}

	public String authenticate(String myURL, String appKey, String appPass) {
		String auth = null;
		if(appKey == null || appPass == null) return auth;
		
		try {
			String authUser = Base64.getEncoder().encodeToString(appKey.getBytes("UTF-8"));
			String authPass = Base64.getEncoder().encodeToString(appPass.getBytes("UTF-8"));
System.out.println("Base64 Encode Username and password : " + authUser + " : " + authPass);

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
			response.close();
			
			JSONObject jResp = new JSONObject(rBody);
			int ResultCode = jResp.getInt("ResultCode");
			if(jResp.has("ResultCode") && (jResp.getInt("ResultCode") == 0)) {
			 	auth = jResp.getString("access_token");
System.out.println("Authenticated : " + auth);
			} else {
System.out.println("Not Authenticated");
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
				.addHeader("authorization", auth)
				.addHeader("action", action)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			String resp = response.body().string();
System.out.println("RESP : " + resp);

			jResp = new JSONObject(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return jResp;
	}

}

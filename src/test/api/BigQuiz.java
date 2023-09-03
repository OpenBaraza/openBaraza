

import java.util.Base64;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BigQuiz {
	
	public static void main(String[] args) {
		System.out.println("Starting Auth test");
		BigQuiz bq = new BigQuiz();
		//bq.apply();
		
		String token = bq.auth();
		
		String myURL = "https://demo.dewcis.com/bigquiz/dataserver?view=10:0";
		bq.sendData(myURL, token, "read", "{}");
	}
	
	public void apply() {
		JSONObject jParams = new JSONObject();
		jParams.put("first_name", "Glenn");
		jParams.put("surname", "Onyi");
		jParams.put("middle_name", "Tedd");
		jParams.put("applicant_email", "gto@gmail.com");
		jParams.put("applicant_phone", "0719458873");
		jParams.put("first_password", "Tedd123.");
		
		String myURL = "https://demo.dewcis.com/bigquiz/dataserver?view=3:0";
		sendData(myURL, null, "udata", jParams.toString());
	}
	
	public String auth() {
		String myURL = "https://demo.dewcis.com/bigquiz/dataserver";
		String userName = "root";
		String password = "baraza";

		String token = authenticate(myURL, userName, password);
		System.out.println("Token : " + token);
		
		return token;
	}
	
	public String sendData(String myURL, String auth, String action, String data) {
		String resp = null;
		
		try {
System.out.println("BASE URL : " + myURL);
System.out.println("BASE ACTION : " + action);
System.out.println("BASE DATA : " + data);
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, data);
			if(auth == null) {
				Request request = new Request.Builder()
					.url(myURL)
					.post(body)
					.addHeader("action", action)
					.addHeader("content-type", "application/json")
					.build();
				Response response = client.newCall(request).execute();
				resp = response.body().string();
			} else {
				Request request = new Request.Builder()
					.url(myURL)
					.post(body)
					.addHeader("action", action)
					.addHeader("authorization", auth)
					.addHeader("content-type", "application/json")
					.build();
				Response response = client.newCall(request).execute();
				resp = response.body().string();
			}
			
System.out.println(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
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

}

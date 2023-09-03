import java.util.concurrent.TimeUnit;
import java.util.Base64;
import java.util.Iterator;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BDataClient {

	public static void main(String args[]) {

		String myURL = "http://demo.dewcis.com/hr/dataserver";
		String data = "{}";
		
		BDataClient dataClient = new BDataClient();
		String formDef = dataClient.getUForm(myURL, "1:0", data);
		
		String token = dataClient.authenticate(myURL, "root", "baraza");
		String resp = dataClient.sendData(myURL, "4:0", token, "read", data);
		resp = dataClient.sendData(myURL, "5:0", token, "read", data);
		resp = dataClient.sendData(myURL, "7:0", token, "report", data);
		resp = dataClient.sendData(myURL, "1:0", token, "menu", data);

System.out.println("BASE 3030 : " + resp);
	}

	public String authenticate(String myURL, String appKey, String appPass) {
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
System.out.println("BASE 1040 : " + rBody);
			
			JSONObject jResp = new JSONObject(rBody);
			int ResultCode = jResp.getInt("ResultCode");
			if(jResp.has("ResultCode") && (jResp.getInt("ResultCode") == 0)) {
				auth = jResp.getString("access_token");
System.out.println("BASE 1050 : " + auth);
			}
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return auth;
	}
	
	public String sendData(String myURL, String viewLink, String auth, String action, String data) {
		String resp = null;
		
		try {			
System.out.println("BASE 2010 : " + data);
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, data);
			Request request = new Request.Builder()
				.url(myURL + "?view=" + viewLink)
				.post(body)
				.addHeader("action", action)
				.addHeader("authorization", auth)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			
System.out.println(response.body().string());
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
	}

	public String getUForm(String myURL, String viewLink, String data) {
		String resp = null;
		
		try {			
System.out.println("BASE 2010 : " + data);
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, data);
			Request request = new Request.Builder()
				.url(myURL + "?view=" + viewLink)
				.post(body)
				.addHeader("action", "uform")
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			
System.out.println(response.body().string());
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
	}
}




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

public class BAuth {
	
	public static void main(String[] args) {
		System.out.println("Starting Auth test");
		BAuth auth = new BAuth();

		//String sURL = "https://demo.dewcis.com/aua/dataserver";
		//String sURL = "https://portal.dewcis.com/hr/dataserver";
		String sURL = "http://localhost:9090/hr/dataserver";
		String userName = "root";
		String password = "baraza";

		String token = auth.authenticate(sURL, userName, password);
		
		System.out.println("Token : " + token);
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

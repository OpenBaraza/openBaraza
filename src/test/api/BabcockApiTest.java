
import java.util.concurrent.TimeUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BabcockApiTest {
	
	public static void main(String[] args) {
		BabcockApiTest bat = new BabcockApiTest();
		bat.testApi();
	}

	public void testApi() {
		System.out.println("Starting Babcock API test");
		
		String sURL = "https://sandbox.dewcis.com/babcock/dataserver";
		String userName = "rootadmin";
		String password = "ndovu";

		String token = authenticate(sURL, userName, password);
		if(token == null) return;
		
		// Reset email
		JSONObject jData = new JSONObject();
		jData.put("import_grade_id", "1222002");
		jData.put("course_id", "GEDS002");
		jData.put("session_id", "2022/2023.1");
		jData.put("student_id", "22/0905");
		jData.put("score", "74");
		
		String sView = "?view=30:0:4&linkdata=2022/2023.1&where=studentid='22/0905'";
		String sView14 = sendData(sURL + sView, token, "read", "{}");
		String sView15 = sendData(sURL + "?view=105:0:0", token, "data", jData.toString());
	}


	public static String authenticate(String myURL, String appKey, String appPass) {
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
	
}

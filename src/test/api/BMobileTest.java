
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

public class BMobileTest {
	
	public static void main(String[] args) {
		BMobileTest mobileTest = new BMobileTest(); 
	}

	public BMobileTest() {
		//testPos();
		testHR();
		//testAttendance();
		//testPicture();
	}
	
	public void testPos() {
		System.out.println("Starting Mobile API test");
		
		String sURL = "https://demo.dewcis.com/hcm/pos_server";
		String userName = "root";
		String password = "baraza";

		String token = authenticate(sURL, userName, password);
		
		// Reset email
		JSONObject jReset = new JSONObject();
		jReset.put("request_email", "dennisgichangi@gmail.com");
		jReset.put("validation_code", "baraza");
		//String reset = sendData(sURL, null, "email_reset", jReset.toString());
		
		// Get menu
		String sMenu = sendData(sURL, token, "menu", "{}");
		String sDashboard = sendData(sURL, token, "dashboard", "{}");
		String sView1 = sendData(sURL + "?view=200:0", token, "view", "{}");
		String sView2 = sendData(sURL + "?view=200:0:0", token, "view", "{}");
		String sView3 = sendData(sURL + "?view=200:0:0:0", token, "view", "{}");
		String sView4 = sendData(sURL + "?view=200:0:0:0", token, "grid", "{}");
		
		String sView11 = sendData(sURL + "?view=405:0", token, "view", "{}");
		String sView12 = sendData(sURL + "?view=405:0:0", token, "view", "{}");
		String sView14 = sendData(sURL + "?view=405:0:0", token, "form", "{}");
		String sView15 = sendData(sURL + "?view=405:0:0", token, "data", getItem().toString());

	}
	
	public JSONObject getItem() {
		JSONObject jItem = new JSONObject();
		
		jItem.put("item_category_id", "2");
		jItem.put("tax_type_id", "6");
		jItem.put("item_unit_id", "1");
		jItem.put("item_name", "API Test");
		jItem.put("for_sale", "true");
	
		return jItem;
	}
	
	public void testHR() {
		System.out.println("Starting Mobile API test");
		
		String sURL = "https://sandbox.dewcis.com/hcm/dataserver";
		String userName = "joseph.kamau";
		String password = "baraza";
		
		/*sURL = "https://apps.hcm.co.ke/hcmke/dataserver";
		userName = "rose.ndolo";
		password = "166N65SA";*/

		String token = authenticate(sURL, userName, password);
		
		// Get menu
		String sMenu = sendData(sURL, token, "menu", "{}");
		String sDashboard = sendData(sURL, token, "dashboard", "{}");
		//String sView1 = sendData(sURL + "?view=10:0", token, "view", "{}");
		//String sView2 = sendData(sURL + "?view=10:0:1", token, "view", "{}");
		//String sGrid = sendData(sURL + "?view=10:0:1", token, "grid", "{}");
		String sView3 = sendData(sURL + "?view=5:0", token, "view", "{}");
		String sGrid3 = sendData(sURL + "?view=5:0", token, "grid", "{}");
		String sData3 = sendData(sURL + "?view=5:0", token, "read", "{}");
		
		/*String sData1 = sendData(sURL + "?view=10:0", token, "read", "{}");
		String sData2 = sendData(sURL + "?view=10:0:1&linkdata=1", token, "read", "{}");*/
		
		//String sReport = sendData(sURL + "?view=20:0", token, "report", "{}");
		//String sReport = sendData(sURL + "?view=80:0:0:0&linkdata=5", token, "report", "{}");
		//getPdfReport(sURL + "?view=80:0:0:0&linkdata=5", token, "pdfreport", "{}");
	}

	public void testAttendance() {
		System.out.println("Starting Mobile API test");
		
		String sURL = "https://demo.dewcis.com/hcm/dataserver";
		String userName = "root";
		String password = "baraza";

		String token = authenticate(sURL, userName, password);

		String sDashboard = sendData(sURL, token, "dashboard", "{}");

		// Get add attendnace
		JSONObject jAttend = new JSONObject();
		jAttend.put("log_type", "1");		// log_type 1 - Clocking, 4 - lunch, 7 - break
		jAttend.put("log_in_out", "OUT");
		jAttend.put("long", "-122.084");
		jAttend.put("lat", "37.4219983");
		String sAttend1 = sendData(sURL + "?view=1:0", token, "attendance", jAttend.toString());

		sDashboard = sendData(sURL, token, "dashboard", "{}");
	}
	
	public void testPicture() {
		File fl = new File("/home/dennis/Downloads/HCM.png");
		String sURL = "https://demo.dewcis.com/hcm/putbarazapictures";
		uploadImage(sURL, fl);
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
	
	public String getPdfReport(String myURL, String auth, String action, String data) {
		String resp = null;
		
		try {
System.out.println("BASE URL : " + myURL);
System.out.println("BASE ACTION : " + action);
System.out.println("BASE DATA : " + data);
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, data);

			Request request = new Request.Builder()
				.url(myURL)
				.post(body)
				.addHeader("action", action)
				.addHeader("authorization", auth)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			
			InputStream in = response.body().byteStream();
			File targetFile = new File("a.pdf");
			Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
System.out.println(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
	}


	public JSONObject uploadImage(String myURL, File myFile) {

		try {
			OkHttpClient client = new OkHttpClient();
		 
			RequestBody formBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("file", "image.jpg", RequestBody.create(MediaType.parse("image/jpg"), myFile))
				.build();

			Request request = new Request.Builder()
				.url(myURL)
				.post(formBody)
				.build();
			Response response = client.newCall(request).execute();
			
			String resp = response.body().string();
			
			System.out.println("uploadImage : " + resp);

			return new JSONObject(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}
		
		return null;
	}
	
}

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

public class BDataTest {

	public static void main(String args[]) {

		String myURL = "http://demo.dewcis.com/mcdss_portal/mobiledata?tag=authenticate";
		//String myURL = "http://192.168.0.4:8080/mcdss_portal/mobiledata?tag=authenticate";
		//String myURL = "http://localhost:8080/mcdss_portal/mobiledata?tag=authenticate";
		JSONObject data = new JSONObject();
		data.put("mobile", "0711572011");
		
		BDataTest dataTest = new BDataTest();
		String resp = dataTest.sendData(myURL, data.toString());
		
System.out.println("BASE 3030 : " + resp);
	}

	public String sendData(String myURL, String data) {
		String resp = null;
		
		try {			
System.out.println("BASE 2010 : " + data);
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, data);
			Request request = new Request.Builder()
				.url(myURL)
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



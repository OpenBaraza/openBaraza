
import java.util.concurrent.TimeUnit;
import java.util.Base64;
import java.util.Iterator;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BJenga {
	
	
	public String authenticate() {
		String resp = null;
		
		try {
		
System.out.println("Processing Access Token.....\n");
    
			OkHttpClient client = new OkHttpClient();
			
            //Building the form body.
            RequestBody formBody = new FormBody.Builder()
                .add("username", "1630497587")
                .add("password", "9753")
                .build();
                
            Request requesthttp = new Request.Builder()
                .url("https://uat.jengahq.io/identity/v2/token")
                .addHeader("Authorization", "Basic ckpBZzVnakRxZmtZTU9Rc2FBYlFwQWhNbTFpOXFvSlk6ZkE0cnd0b3Y4YXY0VEVKeg==")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Host", "uat.jengahq.io")
                .addHeader("Connection", "keep-alive")
                .post(formBody)
                .build();   
                

            Response response = client.newCall(requesthttp).execute(); //passing the request
            
            resp = response.body().string(); //response is in json format
            
            JSONObject jsonObj = new JSONObject(resp);
            
            resp = jsonObj.getString("access_token"); // return the specific key-value

System.out.println("Processing Access Token Complete.\n");
			
		} 
		catch(Exception ex) {
			System.out.println("Error : " + ex);
		} 
		return resp;

	}
	
	
	
	public String testTransaction(String mobileNumber, String businessNumber, String reference, String amount, String description) {
		String resp = null;
		
		try {
			JSONObject jData = new JSONObject();
			
			JSONObject jCustomer = new JSONObject();
			jCustomer.put("mobileNumber", mobileNumber);
			jCustomer.put("countryCode", "KE");
			jData.put("customer", jCustomer);
			
			JSONObject jTrx = new JSONObject();
			jTrx.put("amount", amount);
			jTrx.put("description", description);
			jTrx.put("businessNumber", businessNumber);
			jTrx.put("reference", reference);
			jData.put("transaction", jTrx);
			
System.out.println("BASE 2010 : " + jData.toString());

            //get access token
            String access_token = authenticate();
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jData.toString());
			Request request = new Request.Builder()
				.url("https://uat.jengahq.io/transaction/v2/payment/mpesastkpush")
				.post(body)
				.addHeader("Authorization", "Bearer " +access_token)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
						
System.out.println(response.body().string());
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		} catch(JSONException ex) {
			System.out.println("JSON Error : " + ex);
		}

		return resp;
	}
	
	public static void main(String[] args) {
		BJenga jenga = new BJenga();
 		//System.out.println(jenga.authenticate());
		jenga.testTransaction("0711572013", "811894", "1234", "1", "5678");
		//jenga.testTransaction("0707866136", "811894", "1234", "1", "5678");
	}

}

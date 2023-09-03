
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

import okhttp3.FormBody;
import okhttp3.*;


public class Access_Token {


	public String authenticate() {
		String resp = null;
		
		try {
		
    System.out.println("Processing request.....\n\n");
    
			OkHttpClient client = new OkHttpClient();
			
            JSONObject lsnObj = new JSONObject();
			lsnObj.put("username", "DU_SPNET");
			lsnObj.put("password", "MuU7Y5zQH3TkGhiN9L");
			lsnObj.put("infinityCode", "1328KE46406");
			
        System.out.println(lsnObj.toString());
        
            MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, lsnObj.toString());
            
            Request requesthttp = new Request.Builder()
                .url("https://secure3.transunionafrica.com/duv2/login")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cache-Control", "no-cache")
                //.addHeader("Host", "uat.jengahq.io")
                .addHeader("Connection", "keep-alive")
                .post(body)
                .build();   
                

            Response response = client.newCall(requesthttp).execute(); //passing the request
            
            resp = response.body().string(); //response is in json format
            
            JSONObject jsonObj = new JSONObject(resp);
            
            resp = jsonObj.getString("access_token"); // return the specific key-value
        			
			
		} 
		catch(Exception ex) {
			System.out.println("Error : " + ex);
		} 
		return resp;

	}
	
	
	public static void main(String [] args) {
	
		Access_Token auth = new Access_Token();
		System.out.println("Access Token: "+auth.authenticate()+"\n\n");
	}
	
	
}


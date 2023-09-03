/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2023.0329
 * @since       2.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.server.sms;

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
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BHormuudSMS {

	String token = null;

	public BHormuudSMS(String userName, String apiKey) {
		String sURL = "https://smsapi.hormuud.com/token";

		token = authenticate(sURL, userName, apiKey);
	}

	public String sendSMS(String number, String message) {
		String resp = "";

		if(token == null) return resp;

		// Reset email
		JSONObject jData = new JSONObject();
		jData.put("mobile", number);
		jData.put("message", message);
		jData.put("senderid", "123");
		jData.put("mType", -1);
		jData.put("eType", -1);
		jData.put("UDH", "");

		String sURL = "https://smsapi.hormuud.com/api/sendSMS";
		String sResp = sendData(sURL, token, "data", jData.toString());

		return resp;
	}

	public static String authenticate(String myURL, String appUser, String appPass) {
		String auth = null;
		if(appUser == null || appPass == null) return auth;

		try {
			OkHttpClient client = new OkHttpClient();

			RequestBody formBody = new FormBody.Builder()
				.add("Username", appUser)
				.add("Password", appPass)
				.add("grant_type", "password")
				.build();
			Request request = new Request.Builder()
				.url(myURL)
				.post(formBody)
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

			Response response = client.newCall(request).execute();
			String rBody = response.body().string();
//System.out.println(rBody);

			JSONObject jResp = new JSONObject(rBody);
			if(jResp.has("access_token")) {
			 	auth = jResp.getString("access_token");
//System.out.println("Authenticated : " + auth);
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
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(data, mediaType);

			Request request = new Request.Builder()
				.url(myURL)
				.post(body)
				.addHeader("Authorization", "Bearer " + auth)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			String sResp = response.body().string();
System.out.println(sResp);

			JSONObject jResp = new JSONObject(sResp);
			if(jResp.has("ResponseCode")) resp = jResp.getString("ResponseCode");
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
	}
	
}

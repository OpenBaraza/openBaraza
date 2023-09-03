/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2020.0329
 * @since       2.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.server.sms;

import java.util.logging.Logger;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.baraza.xml.BElement;
import org.baraza.utils.BLogHandle;

public class BSafaricomSMS {
	Logger log = Logger.getLogger(BSafaricomSMS.class.getName());
	BLogHandle logHandle;
	
	String loginUrl = "https://dsvc.safaricom.com:9480/api/auth/login";
	String refreshUrl = "https://dsvc.safaricom.com:9480/api/auth/RefreshToken";
	String bulksmsUrl= "https://dsvc.safaricom.com:9480/api/public/CMS/bulksms";
	String responsURL = "https://apps.dewcis.com/sms/sms_response";
	String smsUserName = "etiqet";
	String token = null;
	String refreshToken = null;
	int refreshCounter = 0;

	public BSafaricomSMS(BElement view, BLogHandle logHandle) {
		this.logHandle = logHandle;
		logHandle.config(log);
		
		login();

		log.info("Starting Safaricom SMS Server.");
	}
	
	public void login() {
		/* Set your app credentials */
		String userName = "EtiqetAPI";
		String password = "ETIQETAPI@ps1214";
		
		JSONObject jLogin = new JSONObject();
		jLogin.put("username", userName);
		jLogin.put("password", password);

		JSONObject jResp = sendData(loginUrl, jLogin.toString());
		if(jResp.has("token")) {
			token = jResp.getString("token");
			refreshToken = jResp.getString("refreshToken");
			System.out.println(token);
		}
	}

	public void getRefreshToken() {
		if((refreshToken != null) && (refreshCounter > 50)) {
			try {
				OkHttpClient client = new OkHttpClient();
				Request request = new Request.Builder()
					.url(refreshUrl)
					.addHeader("content-type", "application/json")
					.addHeader("X-Requested-With", "XMLHttpRequest")
					.addHeader("X-Authorization", "Bearer " + refreshToken)
					.build();
				Response response = client.newCall(request).execute();
				String resp = response.body().string();
				if(resp == null) resp = "";
				else resp = resp.trim();
System.out.println("\nRefresh : " + resp);

				if(resp.indexOf("}") > 3) {
					JSONObject jResp = new JSONObject(resp);
					if(jResp.has("token")) {
						token = jResp.getString("token");
						refreshCounter = 0;
					}
				} else {
					login();
				}
			} catch(IOException ex) {
				System.out.println("IO Error : " + ex);
			}
		}
		refreshCounter++;
   	}
   	
	// user bulk sms
	public JSONObject sendSMS(String senderName, String number, String message, String correlator)  {
		if(token == null) {
			JSONObject jErrResp = new JSONObject();
			jErrResp.put("error", "No token");
			return jErrResp;
		}
		
		JSONArray jDataSet = new JSONArray();
		JSONObject jData = new JSONObject();
		jData.put("userName", smsUserName);
		jData.put("channel", "sms");
		jData.put("packageId", "5049");
		//jData.put("packageId", "6159");
		jData.put("oa", senderName);
		jData.put("msisdn", number);
		jData.put("message", message);
		jData.put("uniqueId", correlator);
		jData.put("actionResponseURL", responsURL);

		jDataSet.put(jData);
		JSONObject jBulksms = new JSONObject();
		jBulksms.put("timeStamp", timeStamp());
		jBulksms.put("dataSet", jDataSet);

		System.out.println("Request body in json, values are : " + jBulksms.toString());

		JSONObject jResp = sendData(bulksmsUrl, token, jBulksms.toString());

	    return jResp;
	}

	public JSONObject sendData(String myURL, String myData) {
		JSONObject jResp = new JSONObject();
		
		try {			
System.out.println("BASE 2010 : \n" + myData);

			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(myData, mediaType);
			Request request = new Request.Builder()
				.url(myURL)
				.post(body)
				.addHeader("content-type", "application/json")
				.addHeader("X-Requested-With", "XMLHttpRequest")
				.build();
			Response response = client.newCall(request).execute();
			String resp = response.body().string();
System.out.println(resp);

			jResp = new JSONObject(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return jResp;
	}

	public JSONObject sendData(String myURL, String auth, String data) {
		JSONObject jResp = new JSONObject();
		
		try {			
System.out.println("BASE 2010 : \n" + data);
						
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(data, mediaType);
			Request request = new Request.Builder()
				.url(myURL)
				.post(body)
				.addHeader("content-type", "application/json")
				.addHeader("X-Requested-With", "XMLHttpRequest")
				.addHeader("X-Authorization", "Bearer " + auth)
				.build();
			Response response = client.newCall(request).execute();
			String resp = response.body().string();
System.out.println(resp);

			jResp = new JSONObject(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}
		
		return jResp;
	}

	//get current timestamp
	public static String timeStamp() {
		String results = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); //get current timestamp and its format
		return results;
	}

}

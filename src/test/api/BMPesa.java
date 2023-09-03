
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

public class BMPesa {

	String authUrl = "https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
	//String authUrl = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
	
	String mpesaUrl = "https://api.safaricom.co.ke/mpesa/";
	//String mpesaUrl = "https://sandbox.safaricom.co.ke/mpesa/";
	
	String callBackURL = "https://demo.dewcis.com/banking/mpcallback";
	
	public static void main(String[] args) {
		BMPesa mpesa = new BMPesa();
		
		String baseUrl = "https://demo.dewcis.com/banking/";
		String validationURL = baseUrl + "validation";
		String confirmationURL =  baseUrl + "confirmation";
		
		if(args.length == 0) return;
		
		if(args[0].equals("reg")) {
			String auth = mpesa.authenticate("AlvIdAfCArA2uJwNhl8qW342lYPG01vd", "QkL36BCqW8FJN1ZR");
			String resp = mpesa.registerURL(auth, "603080", validationURL, confirmationURL);
		} else if(args[0].equals("kenic")) {
			String kenicBaseUrl = "https://registry.kenic.or.ke/payapi/";
			String validationKenic = kenicBaseUrl + "validation";
			String confirmationKenic =  kenicBaseUrl + "confirmation";
		
			String auth = mpesa.authenticate("kPM13enJUx6PJHIUop8VEinjLWpKnkz0", "nrJwRwA5jkjYjCJP");
			String resp = mpesa.registerURL(auth, "502100", validationKenic, confirmationKenic);
		} else if(args[0].equals("test")) {
			String auth = mpesa.authenticate("AlvIdAfCArA2uJwNhl8qW342lYPG01vd", "QkL36BCqW8FJN1ZR");
			JSONObject jResp = mpesa.testTransaction(auth, "603080", "CustomerPayBillOnline", "123", "254708374149", "ESL");
		} else if(args[0].equals("score")) {
			//String auth = mpesa.authenticate("NygL0Wc9i9Dug9w9z6NK2MBLgIGbsrXh", "A2MlmGjdRLj9KSWC");
			//String auth = mpesa.authenticate("Yl4rgFZMkbfmt6tEcmDaiQv9aqIn44nR", "KYSLq6yZesu9uOqa");
			String auth = mpesa.authenticate("dwv5DMi1BbgmQKgThOSWyuRU7x3ZGI29", "C4bAUGrn99ubhvwC");
			JSONObject jResp = mpesa.checkScore(auth, "254711572013", "21407379");
		} else if(args[0].equals("stk")) {
			String auth = mpesa.authenticate("dwv5DMi1BbgmQKgThOSWyuRU7x3ZGI29", "C4bAUGrn99ubhvwC");
			String passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";

			JSONObject jResp = mpesa.stkPush(auth, passKey, "811894", "10", "254711572013", "LN21", "Loan Payment");
		}
	}

	public String authenticate(String app_key, String app_secret) {
		
		String auth = null;
		try {
			String appKeySecret = app_key + ":" + app_secret;
			byte[] byteData = appKeySecret.getBytes("ISO-8859-1");
			String encoded = Base64.getEncoder().encodeToString(byteData);

			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
				.url(authUrl)
				.get()
				.addHeader("authorization", "Basic " + encoded)
				.addHeader("cache-control", "no-cache")
				.build();
			Response response = client.newCall(request).execute();
			
			String rBody = response.body().string();
			System.out.println("BASE 1010 : " + rBody);

			JSONObject jObject = new JSONObject(rBody);
			auth = jObject.getString("access_token");

			System.out.println("access_token : " + auth);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		} catch(JSONException ex) {
			System.out.println("JSON Error : " + ex);
		}

		return auth;
	}
	
	public String registerURL(String auth, String shortCode, String validationURL, String confirmationURL) {
		String resp = null;
		
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("ShortCode", shortCode);
			jObject.put("ResponseType", "Completed");
			jObject.put("ValidationURL", validationURL);
			jObject.put("ConfirmationURL", confirmationURL);
System.out.println("BASE 2010 : " + jObject.toString());
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jObject.toString());
			Request request = new Request.Builder()
				.url("https://api.safaricom.co.ke/mpesa/c2b/v1/registerurl")
				.post(body)
				.addHeader("authorization", "Bearer " + auth)
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
	
	public JSONObject testTransaction(String auth, String shortCode, String commandID, String amount, String MSISDN, String billRefNumber) {
		String url = mpesaUrl + "c2b/v1/simulate";
		
		JSONObject jData = new JSONObject();
		jData.put("ShortCode", shortCode);
		jData.put("CommandID", commandID);
		jData.put("Amount", amount);
		jData.put("Msisdn", MSISDN);
		jData.put("BillRefNumber", billRefNumber);
			
		JSONObject jResp = makeCall(url, auth, jData.toString());

		return jResp;
	}
	
	public JSONObject stkPush(String auth, String passKey, String shortCode, String amount, String MSISDN, String billRefNumber, String TransactionDesc) {
		String url = mpesaUrl + "stkpush/v1/processrequest";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String lmpTimestamp = sdf.format(new Date());	
		String lmpPassord = "";
		try {
			String bscPasskey = shortCode + passKey + lmpTimestamp;
			byte[] byteData = bscPasskey.getBytes("ISO-8859-1");
			lmpPassord = Base64.getEncoder().encodeToString(byteData);
		} catch(UnsupportedEncodingException ex) {
			System.out.println("Unsupported Encoding Exception Error : " + ex);
		}
System.out.println("BASE 1010 : " + lmpTimestamp);

		JSONObject jData = new JSONObject();
		jData.put("BusinessShortCode", shortCode);
		jData.put("Password", lmpPassord);
		jData.put("Timestamp", lmpTimestamp);
		jData.put("TransactionType", "CustomerPayBillOnline");
		jData.put("Amount", amount);
		jData.put("PartyA", MSISDN);
		jData.put("PartyB", shortCode);
		jData.put("PhoneNumber", MSISDN);
		jData.put("CallBackURL", callBackURL);
		jData.put("AccountReference", billRefNumber);
		jData.put("TransactionDesc", TransactionDesc);
		
		JSONObject jResp = makeCall(url, auth, jData.toString());
		return jResp;
	}
	
	public JSONObject checkScore(String auth, String MSISDN, String idNumber) {
		String url = mpesaUrl + "creditScore/v1/customer";
		
		JSONObject jData = new JSONObject();
		jData.put("mpesa_msisdn", MSISDN);
		jData.put("document_id", idNumber);
			
		JSONObject jResp = makeCall(url, auth, jData.toString());

		return jResp;
	}
	
	public JSONObject makeCall(String url, String auth, String jData) {
		JSONObject jResp = new JSONObject();
		
		try {			
System.out.println("BASE 2010 : " + jData);
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jData);
			Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("authorization", "Bearer " + auth)
				.addHeader("content-type", "application/json")
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


}

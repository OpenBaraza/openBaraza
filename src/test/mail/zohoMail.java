
import java.io.IOException;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class zohoMail {
 
	public static void main(String[] args) {

		String clientId = "1000.8AOEP77IP5EA89716T5BURALHCYGSH";
		String clientSecret = "9ca20789a9e67ec08f15c868cfc83b7f1e62c4d578";
		String redirectUri = "https://api.spidercash.co.ke/spidercash/zohocallback.jsp";
		String authUrl = "https://accounts.zoho.com/oauth/v2/auth"
			+ "?scope=ZohoMail.messages.CREATE"
			+ "&client_id=" + clientId
			+ "&response_type=code&access_type=online"
			+ "&redirect_uri=" + redirectUri
			+ "&state=987654321";
		System.out.println(authUrl);
		
		
		authUrl = "https://accounts.zoho.com/oauth/v2/token"
			+ "?code=1000.a91508bd939027bc9601ae3ca6708945.e040e47ccfda6b3828ac8e60eb28dd81"
			+ "&grant_type=authorization_code"
			+ "&client_id=" + clientId
			+ "&client_secret=" + clientSecret
			+ "&redirect_uri=" + redirectUri
			+ "&scope=ZohoMail.messages.CREATE";

		authUrl = "https://accounts.zoho.com/oauth/v2/token"
			+ "?code=1000.ecabcc17997336a1ef3d335616e17122.4bf81b0cf47f0fbf7eb9693c0c650913"
			+ "&grant_type=authorization_code"
			+ "&client_id=" + clientId
			+ "&client_secret=" + clientSecret
			+ "&redirect_uri=" + redirectUri
			+ "&scope=ZohoMail.messages.CREATE";
		System.out.println(authUrl);
		
		String auth = "Zoho-oauthtoken 1000.7594c63cdd7ac57aef1f74e5a7648df1.03d5c1ee5395c5dd3f45191824de0b8f";
		
		JSONObject jMail = new JSONObject();
		jMail.put("fromAddress", "spider.verification@spidercash.co.ke");
		jMail.put("toAddress", "dennis.gichangi@dewcis.com");
		jMail.put("subject", "Email - Always and Forever");
		jMail.put("content", "Email can never be dead. The most neutral and effective way, that can be used for one to many and two way communication.");
		
		
		//callGet(authUrl);
		String myUrl = "https://mail.zoho.com/api/accounts";
		//myUrl = "https://mail.zoho.com/api/oauth/user/info";
		getData(myUrl, auth);
	}
	
	public static String callGet(String myURL) {
		String resp = null;
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
				.url(myURL)
				.get()
				.addHeader("cache-control", "no-cache")
				.build();
			Response response = client.newCall(request).execute();
			String rBody = response.body().string();
			
			resp = rBody;
			System.out.println(rBody);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
	}
	
	public static String postData(String myURL, String auth, String data) {
		String resp = null;
		
System.out.println("BASE 2010 : " + data);

		try {			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, data);
			Request request = new Request.Builder()
				.url(myURL)
				.post(body)
				.addHeader("authorization", auth)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			resp = response.body().string();
			
System.out.println(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
	}
	
	public static String getData(String myURL, String auth) {
		String resp = null;
		
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
				.url(myURL)
				.get()
				.addHeader("authorization", auth)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			resp = response.body().string();
			
System.out.println(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
	}
}

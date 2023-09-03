
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSocketFactory;
import java.security.cert.CertificateException;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.List;
import java.io.IOException;

public class safaricomSms {

    public static void main(String[] args) {
		/* Set your app credentials */
		String userName = "EtiqetAPI";
		String password = "ETIQETAPI@ps1214";
		
		JSONObject jLogin = new JSONObject();
		jLogin.put("username", userName);
		jLogin.put("password", password);

		String url = "https://dtsvc.safaricom.com:8480/api/auth/login";		// login uri
		//url = "https://dtsvc.safaricom.com:8480/api/auth/login";		// Test bed
		url = "https://dsvc.safaricom.com:9480/api/auth/login";		// production
		JSONObject jResp = sendData(url, jLogin.toString());
		
		if(jResp.has("token")) {
			String token = jResp.getString("token");
			System.out.println(token);
			
			bulkSms(token);
		}
   	}
   	
	// user bulk sms
	public static String bulkSms(String token)  {
		String baseUrl= "https://dsvc.safaricom.com:9480/api/public/CMS/bulksms";
		String responsURL = "https://demo.dewcis.com/sms/sms_response";

		JSONArray jDataSet = new JSONArray();
		JSONObject jData = new JSONObject();
		jData.put("userName", "etiqet");
		jData.put("channel", "sms");
		jData.put("packageId", "5049");
		jData.put("oa", "Dewcis");
		jData.put("msisdn", "254711572013");
		jData.put("message", "Hello testing online promo nov 1");
		jData.put("uniqueId", "2500688298721");
		jData.put("actionResponseURL", responsURL);

		jDataSet.put(jData);
		JSONObject jBulksms = new JSONObject();
		jBulksms.put("timeStamp", timeStamp());
		jBulksms.put("dataSet", jDataSet);

		System.out.println("Request body in json, values are : " + jBulksms.toString());

	    String results = sendData(baseUrl, token, jBulksms.toString());

	    return results;
	}

	public static JSONObject sendData(String myURL, String data) {
		JSONObject jResp = new JSONObject();
		
		try {			
System.out.println("BASE 2010 : \n" + data);
			
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder = configureToIgnoreCertificate(builder);
			OkHttpClient client = builder.build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, data);
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

	public static String sendData(String myURL, String auth, String data) {
		String resp = null;
		
		try {			
System.out.println("BASE 2010 : \n" + data);
			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, data);
			Request request = new Request.Builder()
				.url(myURL)
				.post(body)
				.addHeader("content-type", "application/json")
				.addHeader("X-Requested-With", "XMLHttpRequest")
				.addHeader("X-Authorization", "Bearer " + auth)
				.build();
			Response response = client.newCall(request).execute();
			resp = response.body().string();
			
System.out.println(resp);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
	}

    private static OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
        try {

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ex) {
            System.out.println("Exception while configuring IgnoreSslCertificate " + ex);
        }
        return builder;
    }

	//get current timestamp
	public static String timeStamp() {
	    String results = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); //get current timestamp and its format
	    return results;
   }

}


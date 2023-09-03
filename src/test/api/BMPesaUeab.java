
import java.util.concurrent.TimeUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BMPesaUeab {

	String authUrl = "https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
	String mpesaUrl = "https://www.jada.co.ke/jada/bar.php";	
	String callBackURL = "https://www.jada.co.ke/jada/bar.php";
	
	public static void main(String[] args) {
		BMPesaUeab bmpesa = new BMPesaUeab();
		

		bmpesa.makeGetCall("254711572013", "7", "BA10001", "UEAB Application fee");
		
	}

	public String makeGetCall(String phoneNumber, String amount, String billRefNumber, String TransactionDesc) {
		
		String rBody = null;
		try {
		
			HttpUrl.Builder urlBuilder = HttpUrl.parse(mpesaUrl).newBuilder();
			urlBuilder.addQueryParameter("phone_number", phoneNumber);
			urlBuilder.addQueryParameter("amount", amount);
			urlBuilder.addQueryParameter("TransactionDesc", TransactionDesc);
			urlBuilder.addQueryParameter("AccountReference", billRefNumber);
			String url = urlBuilder.build().toString();

			Request request = new Request.Builder()
				.url(url)
				.build();

			OkHttpClient client = new OkHttpClient();
			Response response = client.newCall(request).execute();
			
			rBody = response.body().string();
			System.out.println("BASE 1010 : " + rBody);

		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		} catch(JSONException ex) {
			System.out.println("JSON Error : " + ex);
		}

		return rBody;
	}
	
	

}

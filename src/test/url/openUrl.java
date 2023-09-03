

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

class openUrl {

	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) {
		openUrl ol1 = new openUrl();
		ol1.sendPost("https://appsdemo.tamshi.com/includes/central.php");
		System.out.println("Base 1010");
		
		openUrl ol2 = new openUrl();
		ol2.sendPost("https://demo.dewcis.com/hr/application.jsp");
		System.out.println("Base 2010");
	}
	
	public String sendPost(String url) {
		StringBuffer result = new StringBuffer();
		
		HttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
		urlParameters.add(new BasicNameValuePair("cn", ""));
		urlParameters.add(new BasicNameValuePair("locale", ""));
		urlParameters.add(new BasicNameValuePair("caller", ""));
		urlParameters.add(new BasicNameValuePair("num", "12345"));

		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + post.getEntity());
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) result.append(line);

			System.out.println(result.toString());
		} catch(UnsupportedEncodingException ex) {
			System.out.println("Encoding Error : " + ex);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return result.toString();
	}


}

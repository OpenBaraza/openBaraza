

import java.io.IOException;

import java.util.Collections;

import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.ConnectionSpec;
import okhttp3.CipherSuite;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

class openLink {

	public static void main(String[] args) {
		openLink ol1 = new openLink("https://appsdemo.tamshi.com/includes/central.php");
		System.out.println("Base 1010");
		
		openLink ol2 = new openLink("https://demo.dewcis.com/hr/application.jsp");
		System.out.println("Base 2010");
	}
    
    public openLink(String url) {

		ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
			.tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
			.cipherSuites(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
				CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
				CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
				CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
			.build();       

		OkHttpClient client = new OkHttpClient.Builder() 
			.connectionSpecs(Collections.singletonList(spec))
			.build();

		RequestBody formBody = new FormBody.Builder()
			.add("message", "Your message")
			.build();
			
		Request request = new Request.Builder()
			.url(url)
			.post(formBody)
			.build();

		try {
			Response response = client.newCall(request).execute();
		
		} catch (IOException e) {
			e.printStackTrace();
		}


	}


}

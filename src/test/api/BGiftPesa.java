
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.logging.Logger;
import java.util.Base64;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.security.KeyStore;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BGiftPesa {
	Logger log = Logger.getLogger(BGiftPesa.class.getName());

	private static final String BASE_URL = "https://3api.giftpesa.com";
	private static final String AUTH_ENDPOINT = "/authorize";
	private static final String MERCHANTS_ENDPOINT = "/merchants";
	private static final String DISBURSE_ENDPOINT = "/disburse";
	private static final String QUERY_ENDPOINT = "/query/";
	private static final String REDEEM_ENDPOINT = "/redeem";

	private static final String API_KEY = "9367f64a5c4d4da1ec05ccbb";
	private static final String USERNAME = "dgichangi";

	public static void main(String args[]) {
		BGiftPesa gift = new BGiftPesa();

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String strDate = dateFormatter.format(new Date());
		String passKey = API_KEY + ":" + strDate;

		String encriptPass = gift.encriptPassword("./gp-public.crt", passKey);
		String token = gift.authenticate(BASE_URL + AUTH_ENDPOINT, encriptPass, USERNAME);
	}

	public String encriptPassword(String certFileName, String password) {
		String entriptPass = null;

		try {
			File certFile = new File(certFileName);
			String key = new String(Files.readAllBytes(certFile.toPath()), Charset.defaultCharset());
			System.out.println(key);
			String publicKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----", "")
				.replaceAll(System.lineSeparator(), "")
				.replace("-----END PUBLIC KEY-----", "");
			System.out.println(publicKeyPEM);
			byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
			PublicKey publicKey = keyFactory.generatePublic(keySpec);

			byte[] input = password.getBytes();
			Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] cipherText = cipher.doFinal(input);
			entriptPass = Base64.getEncoder().encodeToString(cipherText);
		} catch (FileNotFoundException ex) {
			log.severe("Error : " + ex);
		} catch (IOException ex) {
			log.severe("Error : " + ex);
		} catch (NoSuchAlgorithmException ex) {
			log.severe("Error : " + ex);
		} catch (InvalidKeySpecException ex) {
			log.severe("Error : " + ex);
		} catch (NoSuchPaddingException ex) {
			log.severe("Error : " + ex);
		} catch (InvalidKeyException ex) {
			log.severe("Error : " + ex);
		} catch (IllegalBlockSizeException ex) {
			log.severe("Error : " + ex);
		} catch (BadPaddingException ex) {
			log.severe("Error : " + ex);
		}

		return entriptPass;
	}

	public String authenticate(String authUrl, String encriptPass, String userName) {
		String auth = null;
		try {
System.out.println("BASE 2010 : " + authUrl);
System.out.println("BASE 2020 : " + encriptPass);

			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
				.url(authUrl)
				.get()
				.addHeader("authorization", "Basic " + encriptPass)
				.addHeader("Username", userName)
				.addHeader("cache-control", "no-cache")
				.build();
			Response response = client.newCall(request).execute();

			String rBody = response.body().string();
System.out.println("BASE 2020 : " + rBody);

			JSONObject jObject = new JSONObject(rBody);
			auth = jObject.getString("Token");

			System.out.println("access_token : " + auth);
		} catch(IOException ex) {
			log.severe("IO Error : " + ex);
		} catch(JSONException ex) {
			log.severe("JSON Error : " + ex);
		}

		return auth;
	}

	public String getJson() {
		JSONObject jObj = new JSONObject();

		jObj.put("DisbursementID": "<DisbursementID>");
		jObj.put("DisbursementTitle": "XYZ Disbursement");
		jObj.put("VoucherStartDate": "20210512");
		jObj.put("VoucherEndDate": "20210820");


	}

}

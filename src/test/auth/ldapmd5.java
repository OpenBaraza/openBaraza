
import java.util.Base64;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

class ldapmd5 {

	public static void main(String[] args) {
		String md5pass = digestMd5("baraza");
		System.out.println(md5pass);
		System.out.println(Base64.getEncoder().encodeToString(md5pass.getBytes()));
	}

	private static String digestMd5(String password) {
		String base64 = null;
		try {
			// Static getInstance method is called with hashing MD5 
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method is called to calculate message digest 
			//  of an input digest() return array of byte 
			byte[] md5pass = md.digest(password.getBytes()); 

			// Convert byte array into signum representation 
			BigInteger no = new BigInteger(1, md5pass); 

			// Convert message digest into hex value 
			String hashtext = no.toString(16); 
			while (hashtext.length() < 32) hashtext = "0" + hashtext;
			System.out.println(hashtext);

			System.out.println(DatatypeConverter.printHexBinary(md5pass));

			byte[] md5pass2 = DatatypeConverter.parseHexBinary(hashtext);
			System.out.println(Base64.getEncoder().encodeToString(md5pass2));

			base64 = Base64.getEncoder().encodeToString(md5pass);
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("NoSuchAlgorithmException : " + ex);
		}
		return "{MD5}" + base64;
	}
}


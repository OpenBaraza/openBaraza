

import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class ncbaHash {

	public static void main(String[] args) {
		String hashVal = "Cba123Coba65#&Pay BillJK20DJ7lNX201511021228133000.0088010012345254722998054JOHN DOESUCCESS";
		
		hashVal = "vuvuzelah448FTB210907BWPU2109070225-120000.0066349300170457762KENYA NETWORK INFORMATION CENTRESUCCESS";
		hashVal = "vuvuzelahPay BillTEST1111111111201511022009121.00TEST254711111111KENICSUCCESS";

		digestHash(hashVal);
	}

	private static void digestHash(String hashVal) {
		String base64 = null;
		try {
			// Static getInstance method is called with hashing MD5 
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// digest() method is called to calculate message digest 
			//  of an input digest() return array of byte 
			byte[] encodedhash = digest.digest(hashVal.getBytes(StandardCharsets.UTF_8));

			// Convert message digest into hex value 
			String hashText = bytesToHex(encodedhash);
			System.out.println(hashText);

			String base64Text = Base64.getEncoder().encodeToString(hashText.getBytes());
			System.out.println(base64Text);
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("NoSuchAlgorithmException : " + ex);
		}
	}
	
	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (int i = 0; i < hash.length; i++) {
		    String hex = Integer.toHexString(0xff & hash[i]);
		    if(hex.length() == 1) {
		        hexString.append('0');
		    }
		    hexString.append(hex);
		}
		return hexString.toString();
	}
}



import java.util.Properties;
import java.io.ByteArrayOutputStream;

import javax.mail.Session;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.MessagingException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

public class GMail {
 
	public static void main(String[] args) {

		GMail gmail = new GMail();
		String gAccessToken = gmail.connect("165280036670-3a970jf2m1ih55hmakhm2cph006m616a.apps.googleusercontent.com");
	
System.out.println("BASE 2010 : " + gAccessToken);
	}

	public String connect(String accessToken) {
		GoogleCredential gCredential = new GoogleCredential().setAccessToken(accessToken);
		return gCredential.getAccessToken();
	}

    public MimeMessage createEmail(String to, String from, String subject, String bodyText) {
		try {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			MimeMessage email = new MimeMessage(session);

			email.setFrom(new InternetAddress(from));
			email.addRecipient(RecipientType.TO, new InternetAddress(to));
			email.setSubject(subject);
			email.setText(bodyText);
		} catch(AddressException ex) {
			System.out.println("Address Exception : " +  ex);
		} catch(MessagingException ex) {
			System.out.println("Messaging Exception : " +  ex);
		}

		return null;
    }



}

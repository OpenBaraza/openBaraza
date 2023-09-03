import java.util.Properties;
import java.util.Map;
import java.util.Date;
import java.util.logging.Logger;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.PasswordAuthentication;
import com.sun.mail.util.MailSSLSocketFactory;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPAddressSucceededException;

public class mailTest {

	public static void main(String args[]) {
		String host = "mail.yatimaoutreachorganization.org";
		String imaphost = "mail.yatimaoutreachorganization.org";
		int imapPort = 143;
		String mailuser = "hcm@yatimaoutreachorganization.org";
		String mailpassword = "Hcm@2023";
		String imapType = "imap";
		String imapssl = "mail";
		String smtptls = "false";
		String smtppauth = "true";

		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", host);

		if(imapssl.equals("true")) {
			props.setProperty("mail.store.protocol", "imaps");
			props.setProperty("mail.imap.host", host);
			props.setProperty("mail.imap.port", "993");
			props.setProperty("mail.imap.connectiontimeout", "15000");
			props.setProperty("mail.imap.timeout", "15000");
			props.setProperty("mail.imap.writetimeout", "15000");
			try {
				MailSSLSocketFactory socketFactory= new MailSSLSocketFactory();
				socketFactory.setTrustAllHosts(true);
				props.put("mail.imaps.ssl.socketFactory", socketFactory);
			} catch(GeneralSecurityException ex) {
				System.out.println("MailSSLSocketFactory error " + ex);
			}
			props.setProperty("mail.imap.auth.plain.disable", "true");
			props.setProperty("mail.imap.starttls.enable", "true");
			imapType = "imaps";
			imapPort = 993;
		} else {
			props.setProperty("mail.store.protocol", "imap");
			props.setProperty("mail.imap.host", host);
			props.setProperty("mail.imap.port", "143");
			props.setProperty("mail.imap.connectiontimeout", "15000");
			props.setProperty("mail.imap.timeout", "15000");
			props.setProperty("mail.imap.writetimeout", "15000");
			System.clearProperty("mail.imap.auth.plain.disable");
			System.clearProperty("mail.imap.starttls.enable");
			System.clearProperty("ssl.SocketFactory.provider");
			System.clearProperty("mail.imap.socketFactory.class");
		}

		// Get a Session object
		try {
			Session session = Session.getInstance(props, null);
			session.setDebug(true);
			Store store = session.getStore(imapType);
			store.connect(imaphost, imapPort, mailuser, mailpassword);

			Folder mainFolder = store.getDefaultFolder();
			System.out.println(mainFolder.getName());
			for(Folder inFolder : mainFolder.list()) {
				System.out.println(inFolder.getName());
			}

			Folder inboxFolder = store.getFolder("INBOX");
			for(Folder inFolder : inboxFolder.list()) {
				System.out.println(inFolder.getName());
			}

			Folder sentFolder = inboxFolder.getFolder("Sent");
			if (sentFolder.exists()) {
				System.out.println(sentFolder.getName() + " exists");
			} else {
				if (sentFolder.create(Folder.HOLDS_MESSAGES)) {
					sentFolder.setSubscribed(true);
					System.out.println("Folder was created successfully");
				} else {
					System.out.println("Folder was not created");
				}
			}

			if (smtppauth.equals("true")) {
				props.put("mail.smtp.auth", "true");
				props.put("mail.debug", "true");

				final String mUser = mailuser;
				final String mPassword = mailpassword;
				session = Session.getInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mUser, mPassword);
					}
				});
		    } else if (smtptls.equals("true")) {
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.ssl.trust", "*");
				props.put("mail.smtp.port", "587");
				props.put("mail.debug", "true");

				final String mUser = mailuser;
				final String mPassword = mailpassword;
				session = Session.getInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mUser, mPassword);
					}
				});
			}

			Transport trans = session.getTransport();
			trans.connect(host, mailuser, mailpassword);

			String to = "dennis.gichangi@dewcis.com,hr@dewcis.com,denniswachira@gmail.com,lenywaithy@gmail.com";
			//String to = "george.ombati@corban-construction.com, otieno.olewe@corban-construction.com, hr@dewcis.com";

			Message message = new MimeMessage(session);
			message.setHeader("X-Mailer", "Baraza Java Mailer");
			message.setFrom(new InternetAddress(mailuser));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject("Requisition Approval request");
			message.setText("Dear Mail Crawler,"
				+ "\n\n Test approval emails");

			Transport.send(message);

			System.out.println("Done");

		} catch (Exception ex) {
			System.out.println("Mail exception :  " + ex);
		}
	}
}



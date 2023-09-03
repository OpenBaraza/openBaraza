import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Store;
import javax.mail.FetchProfile;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import java.security.NoSuchProviderException;


public class microsoftMail {

    private static final Logger LOGGER = Logger.getAnonymousLogger();

    private static final String SERVIDOR_SMTP = "smtp.office365.com";
    private static final int PORTA_SERVIDOR_SMTP = 587;
    private static final String CONTA_PADRAO = "safarilogix@charleston.co.ke";
    private static final String SENHA_CONTA_PADRAO = "KDnwe557";

    private final String from = "safarilogix@charleston.co.ke";
    private final String to = "denniswachira@gmail.com";

    private final String subject = "Teste";
    private final String messageContent = "Teste de Mensagem";

    public void sendEmail() {
        final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CONTA_PADRAO, SENHA_CONTA_PADRAO);
            }

        });

        try {
            final Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            message.setText(messageContent);
            message.setSentDate(new Date());
            Transport.send(message);
        } catch (final MessagingException ex) {
            LOGGER.log(Level.WARNING, "Erro ao enviar mensagem: " + ex.getMessage(), ex);
        }
    }

    public Properties getEmailProperties() {
        final Properties config = new Properties();
        config.put("mail.smtp.auth", "true");
        config.put("mail.smtp.starttls.enable", "true");
        config.put("mail.smtp.host", SERVIDOR_SMTP);
        config.put("mail.smtp.port", PORTA_SERVIDOR_SMTP);
        return config;
    }
    
    
	public void connectIMAP() {
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		Properties props= new Properties();
		
		props.put("mail.imap.ssl.enable", "true");
		props.put("mail.imap.port", "993");
		
		props.put("mail.imap.auth.mechanisms", "XOAUTH2");
		props.put("mail.imap.sasl.mechanisms", "XOAUTH2");
		
		props.put("mail.imap.auth.login.disable", "true");
		props.put("mail.imap.auth.plain.disable", "true");
		
		props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.socketFactory.port", "993");
		props.setProperty("mail.imap.starttls.enable", "true");
		
		props.put("mail.debug", "true");
		props.put("mail.debug.auth", "true");

		Session session = Session.getInstance(props);
		session.setDebug(true);
	
		try {
			final Store store = session.getStore("imap");					
			store.connect("outlook.office365.com", CONTA_PADRAO, SENHA_CONTA_PADRAO);	
			
			if(store.isConnected()){
				System.out.println("Connection Established using imap protocol successfully !");		
			}
		//} catch (NoSuchProviderException e) {	// session.getStore()
		//	e.printStackTrace();
		} catch (MessagingException e) {		// store.connect()
			e.printStackTrace();
		}
	}
	
    public static void main(final String[] args) {
        new microsoftMail().sendEmail();
        //new microsoftMail().connectIMAP();
    }

}


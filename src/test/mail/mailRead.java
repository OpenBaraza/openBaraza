/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */

import java.util.logging.Logger;
import java.util.Properties;
import java.util.Map;
import java.util.Date;
import java.util.Vector;
import java.util.Calendar;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.InetAddress;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import com.sun.mail.util.MailSSLSocketFactory;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPAddressSucceededException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;

public class mailRead implements ActionListener {
	Logger log = Logger.getLogger(mailRead.class.getName());
	Session session = null;
	Store store = null;
	SMTPTransport trans = null;
	private boolean mailActive = false;
	private String mailfrom = null;
	private String sentbox = null;
	private String inbox = null;
	private String attachFile = null;
	private String attachDir = "./attach/";
 	private int attnum = 1;
 	
 	String pdfPassword = "299796";
 	
	Vector<Vector<String>> rowData;
	Vector<String> columnNames;

	DefaultTableModel tableModel;
	JTable table;
	JTextField txtMailHost, txtUserName, txtSearchWord;
	JPasswordField txtPassword;
	JButton btSearch, btSave, btAnalyse;


	public static void main(String args[]) {
		mailRead mr = new mailRead();
	}
	
	public mailRead() {
		JPanel headerPanel = new JPanel();
		JLabel lblMailHost = new JLabel("Mail host :");
		headerPanel.add(lblMailHost);
		txtMailHost = new JTextField(10);
		headerPanel.add(txtMailHost);
		JLabel lblUserName = new JLabel("Username :");
		headerPanel.add(lblUserName);
		txtUserName = new JTextField(10);
		headerPanel.add(txtUserName);
		JLabel lblPassword = new JLabel("Password :");
		headerPanel.add(lblPassword);
		txtPassword = new JPasswordField(10);
		headerPanel.add(txtPassword);
		btSearch = new JButton("Search");
		btSearch.addActionListener(this);
		headerPanel.add(btSearch);
		btSave = new JButton("Save");
		btSave.addActionListener(this);
		headerPanel.add(btSave);
		JLabel lblSearchWord = new JLabel("Search :");
		headerPanel.add(lblSearchWord);
		txtSearchWord = new JTextField(10);
		headerPanel.add(txtSearchWord);
		btAnalyse = new JButton("Analyse");
		btAnalyse.addActionListener(this);
		headerPanel.add(btAnalyse);
		
		rowData = new Vector<Vector<String>>();
		columnNames = new Vector<String>();
		columnNames.add("From"); columnNames.add("To"); columnNames.add("Subject"); 
		columnNames.add("Date"); columnNames.add("Flag");
		
		tableModel = new DefaultTableModel(rowData, columnNames);
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		
		/* for dennis */
		txtMailHost.setText("mail.dewcis.com");
		txtUserName.setText("dgichangi");
		txtPassword.setText("");
			
		JFrame frame = new JFrame("FrameDemo");
		frame.getContentPane().add(headerPanel, BorderLayout.PAGE_START);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1100, 800);
		frame.setVisible(true);
	}

	public void mailConnect(String host, String mailUser, String mailPassword) {
		String imaphost = host;
		int imapPort = 143;
		String smtppauth = "true";
		String smtptls = "true";
		String ntlm = "false";
		String imapssl = "true";
		String smtpPort = "587";
		String googleAuth = "false";
		String imapType = "imap";


		/*mailfrom = root.getAttribute("mailfrom", "root");
		inbox = root.getAttribute("inbox", "INBOX");
		sentbox = root.getAttribute("sentbox", "Sent");

		String smtppauth = root.getAttribute("smtpauth", "false");
		smtppauth = root.getAttribute("smtppauth", smtppauth);
		smtppauth = root.getAttribute("smtp.auth", smtppauth);
		String smtptls = root.getAttribute("smtptls", "false");
		String ntlm = root.getAttribute("ntlm", "false");
		String imapssl = root.getAttribute("imapssl", "false");
		String smtpPort = root.getAttribute("smtp.port");
		String googleAuth = root.getAttribute("googleauth", "false");
		String imapType = "imap";*/

		try {
    		// Get a Properties object
    		Properties props = System.getProperties();
			props.setProperty("mail.smtp.host", host);
			props.setProperty("mail.smtp.connectiontimeout", "15000");
			props.setProperty("mail.smtp.timeout", "15000");
			props.setProperty("mail.smtp.writetimeout", "15000");
			if (ntlm.equals("true")) {
				props.setProperty("mail.imap.auth.plain.disable", "true");
				props.setProperty("mail.imap.auth.ntlm.disable", "true");
				props.setProperty("mail.imaps.auth.ntlm.domain", "true");
			} else {
				System.clearProperty("mail.imap.auth.plain.disable");
				System.clearProperty("mail.imap.auth.ntlm.disable");
				System.clearProperty("mail.imaps.auth.ntlm.domain");
			}

			if (smtptls.equals("true")) {
				props.setProperty("mail.smtp.starttls.enable", "true");
				props.setProperty("mail.smtp.auth", "true");
				props.setProperty("mail.smtp.port", "587");

				MailSSLSocketFactory smtpSFactory= new MailSSLSocketFactory();
				smtpSFactory.setTrustAllHosts(true);
				props.put("mail.smtp.ssl.socketFactory", smtpSFactory);
			} else {
				System.clearProperty("mail.smtp.starttls.enable");
				System.clearProperty("mail.smtp.auth");
				System.clearProperty("mail.smtp.port");
				System.clearProperty("mail.smtp.ssl.socketFactory");
			}

			if(imapssl.equals("true")) {
				props.setProperty("mail.store.protocol", "imaps");
				props.setProperty("mail.imap.host", host);
				props.setProperty("mail.imap.port", "993");
				props.setProperty("mail.imap.connectiontimeout", "15000");
				props.setProperty("mail.imap.timeout", "15000");
				props.setProperty("mail.imap.writetimeout", "15000");
				MailSSLSocketFactory socketFactory= new MailSSLSocketFactory();
				socketFactory.setTrustAllHosts(true);
				props.put("mail.imaps.ssl.socketFactory", socketFactory);

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
			if(smtpPort != null) props.setProperty("mail.smtp.port", smtpPort);

			// Get a Session object
			if(googleAuth.equals("true")) {
				final String mUser = mailUser;
				final String mPassword = mailPassword;
				
				session = Session.getInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mUser, mPassword);
					}
				});
			} else {
				session = Session.getInstance(props, null);
			}
			session.setDebug(false);
			store = session.getStore(imapType);
			store.connect(imaphost, imapPort, mailUser, mailPassword);
			
			trans = (SMTPTransport)session.getTransport("smtp");
			if (smtppauth.equals("true") || smtptls.equals("true")) {
				props.put("mail.smtp.auth", "true");
		    	trans.connect(host, mailUser, mailPassword);
			} else {
				System.clearProperty("mail.smtp.auth");
		    	trans.connect();
			}
			mailActive = true;
		} catch (Exception ex) {
			mailActive = false;
			log.severe("Mail User " + mailUser);
			log.severe("Mail exception! " + ex);
		}
	}

	public void setAttachFile(String fileDir, String fileName) {
		attachDir = fileDir;
		attachFile = fileName;
	}

	public boolean sendMail(String messageto, String ccto, String subject, String mymail, boolean infile, Map<String, String> headers, Map<String, String> reports) {
		boolean sent = false;
		
		// if there is no address
		if(messageto == null) {
			log.severe("The email has no recepient address");
			return true;
		} else if(messageto.equals("null")) {
			log.severe("The email has no recepient address");
			return true;
		}
		
		try { 
			Message message = new MimeMessage(session);

			Multipart mp = new MimeMultipart();
			MimeBodyPart eheader = new MimeBodyPart();
			eheader.setContent(mymail, "text/html");
			mp.addBodyPart(eheader);

			if (infile == true) {
				MimeBodyPart attachreport = new MimeBodyPart();
				if(attachFile == null) attachFile = "report.pdf";
				if(attachDir == null) attachDir = "./";
				attachreport.attachFile(attachDir + attachFile);
				attachreport.setFileName(attachFile);
				mp.addBodyPart(attachreport);
			}

			for(String report : reports.keySet()) {
				MimeBodyPart attachreport = new MimeBodyPart();
				attachFile = report + ".pdf";
				if(attachDir == null) attachDir = "./";

				attachreport.attachFile(attachDir + attachFile);
				attachreport.setFileName(attachFile);
				mp.addBodyPart(attachreport);
			}
			
			Address fromAddress = new InternetAddress(mailfrom);
			message.setFrom(fromAddress);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(messageto, false));
			
			if(ccto != null) {
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccto, false));
			}

			message.setSubject(subject);
			//message.setContent(mymail, "text/html");
			message.setContent(mp);

			message.setHeader("X-Mailer", "Baraza Java Mailer");
			for (String key : headers.keySet()) {
				message.setHeader(key, headers.get(key));
			}
			message.setSentDate(new Date());

			log.fine("Sending Message to : " + messageto);
			trans.sendMessage(message, message.getAllRecipients());

			// Get record Folder.  Create if it does not exist.
			Folder folder = store.getFolder(sentbox);
			if (folder == null) {
		    	log.severe("Can't get record folder.");
		    	return false;
			}
			if (!folder.exists()) folder.create(Folder.HOLDS_MESSAGES);
			Message[] messages = new Message[1];
			messages[0] = message;
			folder.appendMessages(messages);

			log.fine("Mail was recorded successfully.");
			sent = true;
		} catch (Exception ex) {
			mailActive = false;		
	    	if (ex instanceof SendFailedException) {
				MessagingException sfe = (MessagingException)ex;
				if (sfe instanceof SMTPSendFailedException) {
					SMTPSendFailedException ssfe = (SMTPSendFailedException)sfe;
					String errMsg = "SMTP SEND FAILED : "+ ssfe.toString();
					errMsg += "\n  Command: " + ssfe.getCommand();
					errMsg += "\n  RetCode: " + ssfe.getReturnCode();
					errMsg += "\n  Response: " + ssfe.getMessage();
					log.severe(errMsg);
				} else {
					log.severe("Send failed: " + sfe.toString());
				}

				Exception ne = sfe.getNextException();
				if ((ne != null) && (ne instanceof MessagingException)) {
					sfe = (MessagingException)ne;
					if (sfe instanceof SMTPAddressFailedException) {
						SMTPAddressFailedException ssfe = (SMTPAddressFailedException)sfe;
						String errMsg = "ADDRESS FAILED : "+ ssfe.toString();
						errMsg += "\n  Address: " + ssfe.getAddress();
						errMsg += "\n  Command: " + ssfe.getCommand();
						errMsg += "\n  RetCode: " + ssfe.getReturnCode();
						errMsg += "\n  Response: " + ssfe.getMessage();
						log.severe(errMsg);
					} else if (sfe instanceof SMTPAddressSucceededException) {
						SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException)sfe;
						String errMsg = "ADDRESS SUCCEEDED : " + ssfe.toString();
						errMsg += "\n  Address: " + ssfe.getAddress();
						errMsg += "\n  Command: " + ssfe.getCommand();
						errMsg += "\n  RetCode: " + ssfe.getReturnCode();
						errMsg += "\n  Response: " + ssfe.getMessage();
						log.fine(errMsg);
					}
				}
	    	} else {
				log.severe("Got Exception: " + ex);
	    	}
		}

		return sent;
	}

	public boolean getMails(boolean readMail) {
		return getMails(inbox, null, readMail, false);
	}

	public boolean getMails(String mailbox, boolean readMail) {
		return getMails(mailbox, null, readMail, false);
	}

	public boolean getMails(String mailbox, String searchPhrase, boolean readMail, boolean saveAttachment) {
		boolean mailstatus = false;
		try {
			Folder folder = store.getDefaultFolder();
			if (folder != null) {
    			folder = folder.getFolder(mailbox);
			
				// try to open read/write and if that fails try read-only
				try {
    				folder.open(Folder.READ_WRITE);
				} catch (MessagingException ex) {
    				folder.open(Folder.READ_ONLY);
				}
				int totalMessages = folder.getMessageCount();
				int newMessages = folder.getNewMessageCount();
				mailstatus = true;

				getMails(folder, searchPhrase, readMail, saveAttachment);
				folder.close(false);
			}
		} catch (Exception ex) {
			mailActive = false;
			log.severe("Oops, got mail exception! " + ex.getMessage());
    		ex.printStackTrace();
		}

		return mailstatus;
	}

	public void getMails(Folder folder, String searchPhrase, boolean readMail, boolean saveAttachment) {
		try {
			// Attributes & Flags for all messages ..
			rowData = new Vector<Vector<String>>();
			Message[] msgs = folder.getMessages();

			// Use a suitable FetchProfile
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.FLAGS);
			fp.add("X-Mailer");
			folder.fetch(msgs, fp);

			for (int i = 0; i < msgs.length; i++) {
				String subject = msgs[i].getSubject();
				Calendar dateReceived = Calendar.getInstance();
				dateReceived.setTime(msgs[i].getReceivedDate());
				
				if(searchPhrase == null) {
					log.info("\nMESSAGE #" + (i + 1) + ":");
					rowData.add(dumpEnvelope(msgs[i]));	// Read the headers
					if(readMail) dumpPart(msgs[i], saveAttachment, dateReceived);		// read the message
				} else if(subject.toLowerCase().indexOf(searchPhrase) >= 0) {
					if(dateReceived.get(Calendar.YEAR) >= 2021) {
						log.info("\nMESSAGE #" + (i + 1) + ":");
						rowData.add(dumpEnvelope(msgs[i]));	// Read the headers
						if(readMail) dumpPart(msgs[i], saveAttachment, dateReceived);		// read the message
					}
				}
		    }
		} catch (Exception ex) {
			mailActive = false;
			log.severe("Oops, got mail exception! " + ex.getMessage());
    		ex.printStackTrace();
		}
	}

	public Vector<String> dumpEnvelope(Message m) {
		Vector<String> mailEnv = new Vector<String>();
		try {
			log.fine("This is the message envelope");
	
			// FROM 
			String from = null;
			Address[] a = m.getFrom();		
			if (a != null) {
				for (int j = 0; j < a.length; j++) {
					if(from == null) from = a[j].toString();
					else from += "," + a[j].toString();
				}
			}
			mailEnv.add(from);
			System.out.println("FROM: " + from);
	
			// TO
			String to = null;
			a = m.getRecipients(Message.RecipientType.TO);
			if (a != null) {
				for (int j = 0; j < a.length; j++) {
					if(to == null) to = a[j].toString();
					else to += "," + a[j].toString();
					
					InternetAddress ia = (InternetAddress)a[j];
					if (ia.isGroup()) {
						InternetAddress[] aa = ia.getGroup(false);
						for (int k = 0; k < aa.length; k++) {
							if(to == null) to = aa[k].toString();
							else to += "," + aa[k].toString();
						}
					}
				}
			}
			mailEnv.add(to);
			System.out.println("TO: " + to);
	
			// SUBJECT
			mailEnv.add(m.getSubject());
			System.out.println("SUBJECT: " + m.getSubject());
	
			// DATE
			Date d = m.getSentDate();
			if(d != null) {
				mailEnv.add(d.toString());
				System.out.println("SendDate: " + d.toString());
			}
	
			// FLAGS
			Flags flags = m.getFlags();
			StringBuffer sb = new StringBuffer();
			Flags.Flag[] sf = flags.getSystemFlags(); // get the system flags
	
			boolean first = true;
			for (int i = 0; i < sf.length; i++) {
				String s;
				Flags.Flag f = sf[i];
				if (f == Flags.Flag.ANSWERED) s = "\\Answered";
				else if (f == Flags.Flag.DELETED) s = "\\Deleted";
				else if (f == Flags.Flag.DRAFT) s = "\\Draft";
				else if (f == Flags.Flag.FLAGGED) s = "\\Flagged";
				else if (f == Flags.Flag.RECENT) s = "\\Recent";
				else if (f == Flags.Flag.SEEN) s = "\\Seen";
				else continue;	// skip it
	
				if (first) first = false;
				else sb.append(' ');
				sb.append(s);
			}
	
			// get the user flag strings
			String[] uf = flags.getUserFlags();
			for (int i = 0; i < uf.length; i++) {
				if (first) first = false;
				else sb.append(' ');
				sb.append(uf[i]);
			}
			mailEnv.add(sb.toString());
			System.out.println("FLAGS: " + sb.toString());
	
			// X-MAILER
			String[] hdrs = m.getHeader("X-Mailer");
			if (hdrs != null)
				System.out.println("X-Mailer: " + hdrs[0]);
			else
				System.out.println("X-Mailer NOT available");
		} catch (MessagingException ex) {
			mailActive = false;
			System.out.println("Message reading error " + ex);
		}
		
		return mailEnv;
    }

	public String dumpPart(Part p, boolean saveAttachment, Calendar dateReceived) {
		String mail = "";
		
		try {
			if (p instanceof Message) dumpEnvelope((Message)p);
			String ct = p.getContentType();
	
			try {
				System.out.println("CONTENT-TYPE: " + (new ContentType(ct)).toString());
			} catch (ParseException pex) {
				System.out.println("BAD CONTENT-TYPE: " + ct);
			}
			String filename = p.getFileName();
			if (filename != null) System.out.println("FILENAME: " + filename);
	
			/** Using isMimeType to determine the content type avoids
			* fetching the actual content data until we need it. */
			if (p.isMimeType("text/plain")) {
				System.out.println("This is plain text");
				System.out.println("---------------------------");
				mail += (String)p.getContent();
			} else if (p.isMimeType("multipart/*")) {
				System.out.println("This is a Multipart");
				System.out.println("---------------------------");
				Multipart mp = (Multipart)p.getContent();
				int count = mp.getCount();
				for (int i = 0; i < count; i++) dumpPart(mp.getBodyPart(i), saveAttachment, dateReceived);
			} else if (p.isMimeType("message/rfc822")) {
				System.out.println("This is a Nested Message");
				System.out.println("---------------------------");
				dumpPart((Part)p.getContent(), saveAttachment, dateReceived);
			} else {
				/** If we actually want to see the data, and it's not a
				* MIME type we know, fetch it and check its Java type. 
				Object o = p.getContent();
				if (o instanceof String) {
					System.out.println("This is a string");
					System.out.println("---------------------------");
					System.out.println((String)o);
				} else if (o instanceof InputStream) {
					System.out.println("This is just an input stream");
					System.out.println("---------------------------");
					InputStream is = (InputStream)o;
					int c;
					while ((c = is.read()) != -1) System.out.write(c);
				} else {
					System.out.println("This is an unknown type");
					System.out.println("---------------------------");
					System.out.println(o.toString()); 
				} */
			}
	
			/** If we're saving attachments, write out anything that
			* looks like an attachment into an appropriately named
			* file.  Don't overwrite existing files to prevent mistakes. */
			if (saveAttachment && !p.isMimeType("multipart/*")) {
				String disp = p.getDisposition();
				// many mailers don't include a Content-Disposition
				if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
					if (filename == null) filename = "Attachment" + attnum++;
					System.out.println("Saving attachment to file " + filename);
					try {
						File f = new File(attachDir + filename);
						if (f.exists())		// XXX - could try a series of names
							throw new IOException("file exists");
						((MimeBodyPart)p).saveFile(f);
					} catch (IOException ex) {
						System.out.println("Failed to save attachment: " + ex);
					}
					System.out.println("---------------------------");
					
					String pdfPassword = "299796";
					
					if(filename.contains(".pdf")) {
						String original = attachDir + filename;
						String newFileName =
						newFileName = "" + dateReceived.get(Calendar.DAY_OF_YEAR);
						newFileName = String.format("%4s", newFileName).replace(' ', '0');
						newFileName = "F" + dateReceived.get(Calendar.YEAR) + "" + newFileName;
						newFileName = "files/" + newFileName + ".pdf";
						
						// select a file for Decryption operation
						File inFile = new File(original);
						File outFile = new File(newFileName);

						// Load the PDF file
						PDDocument pdd = PDDocument.load(inFile, pdfPassword);

						// removing all security from PDF file
						pdd.setAllSecurityToBeRemoved(true);

						// Save the PDF file
						pdd.save(outFile);

						// Close the PDF file
						pdd.close();
						System.out.println("Decryption Done...");
					}
				}
			}
		} catch (Exception ex) {
			mailActive = false;
			System.out.println("Read message error " + ex);
		}
		
		return mail;
	}
	
	public void actionPerformed(ActionEvent ev) {
		System.out.println("BASE click : " + ev.getActionCommand());
	
		String host = txtMailHost.getText();
		String mailUser = txtUserName.getText();
		String searchWord = txtSearchWord.getText();
		String mailPassword = new String(txtPassword.getPassword());

		if(searchWord == null) searchWord = "Your Estatement on";
		else if(searchWord.trim().length() == 0) searchWord = "Your Estatement on";
		searchWord = searchWord.toLowerCase();
		
		if(ev.getActionCommand().equals("Search")) {
			mailConnect(host, mailUser, mailPassword);
			getMails("DewCis/Official", searchWord, false, false);
			
			tableModel.setDataVector(rowData, columnNames);
			tableModel.fireTableDataChanged();
		} else if(ev.getActionCommand().equals("Save")) {
			mailConnect(host, mailUser, mailPassword);
			getMails("DewCis/Official", searchWord, true, true);
		} else if(ev.getActionCommand().equals("Analyse")) {
			Analyse(searchWord);
		}
	}
	
	public void Analyse(String keyWord) {
		File folder = new File(attachDir);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				boolean found = AnalysePDF(attachDir + listOfFiles[i].getName(), keyWord);
				if(found) System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
	}
	
	public boolean AnalysePDF(String fileName, String keyWord) {
		boolean found = false;
		try {
			PdfReader reader = new PdfReader(fileName);
			String data = PdfTextExtractor.getTextFromPage(reader, 1);
			
			if(data.toLowerCase().indexOf(keyWord) >= 0) {
				found = true;
				//System.out.println(data);
			}
		} catch(IOException ex) {
			System.out.println("IO error on reading PDF " + fileName + " ERROR " + ex );
		}
		
		return found;
	}

	public boolean getActive() {
		return mailActive;
	}

	public void close() {
		try {
			if(trans != null) trans.close();
			if(store != null) store.close();
			mailActive = false;
		} catch(MessagingException ex) {
			log.severe("Mail System closing error : " + ex);
		}
	}
}

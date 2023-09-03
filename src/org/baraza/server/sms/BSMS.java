/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2020.0329
 * @since       2.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.server.sms;

import java.util.logging.Logger;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.africastalking.Callback;
import com.africastalking.SmsService;
import com.africastalking.sms.Message;
import com.africastalking.sms.Recipient;
import com.africastalking.AfricasTalking;

import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.xml.BElement;
import org.baraza.server.comm.BComm;
import org.baraza.utils.BNumberFormat;
import org.baraza.utils.BLogHandle;


public class BSMS {
	Logger log = Logger.getLogger(BSMS.class.getName());
	BLogHandle logHandle;

	List<BComm> qcomms;
	BSafaricomSMS safaricomSMS = null;

	String serverIP = null;
	String spPassword = null;
	String endPoint = null;
	String smsReceiver = null;
	String startCorrelator = null;
	Map<String, String[]> smsOrgs;
	
	List<String> airtelBlock1;
	List<String> airtelBlock2;

	BDB db = null; 
	int processdelay = 10000;
	boolean executing = false;

	public BSMS(BDB db, BElement node, BLogHandle logHandle) {
		this.db = db;
		this.logHandle = logHandle;
		logHandle.config(log);

		spPassword = node.getAttribute("sppassword");
		endPoint = node.getAttribute("endpoint");
		serverIP = node.getAttribute("serverip", "192.168.9.177");
		smsReceiver = node.getAttribute("smsreceiver");
		startCorrelator = node.getAttribute("startCorrelator", "12345");
		processdelay = Integer.valueOf(node.getAttribute("processdelay", "10000")).intValue();

		String orgSQL = "SELECT org_id, sp_id, service_id, sender_name, sms_rate, "
			+ "send_fon, sc_register, ait_username, ait_app_key, alphanumeric, air_touch_key, provider_id "
			+ "FROM orgs WHERE (is_active = true) AND (provider_id > 0) ORDER BY org_id";
		smsOrgs =  new HashMap<String, String[]>();
		BQuery orgRS = new BQuery(db, orgSQL);
		while(orgRS.moveNext()) {
			System.out.println("org_id : " + orgRS.getString("org_id"));

			String orgID = orgRS.getString("org_id");
			String[] orgParams = new String[11];
			orgParams[0] = orgRS.getString("sp_id");
			orgParams[1] = orgRS.getString("service_id");
			orgParams[2] = orgRS.getString("sender_name");
			orgParams[3] = orgRS.getString("sms_rate");
			orgParams[4] = orgRS.getString("send_fon");
			orgParams[5] = orgRS.getString("sc_register");
			orgParams[6] = orgRS.getString("ait_username");
			orgParams[7] = orgRS.getString("ait_app_key");
			orgParams[8] = orgRS.getString("alphanumeric");
			orgParams[9] = orgRS.getString("air_touch_key");
			orgParams[10] = orgRS.getString("provider_id");
			
			smsOrgs.put(orgID, orgParams);
		}
		
		// Get array block
		String[] ab1 = {"25410","25473","25478"};
		String[] ab2 = {"254750","254751","254752","254753","254754","254755","254756","254762"};
		airtelBlock1 = Arrays.asList(ab1);
		airtelBlock2 = Arrays.asList(ab2);
		
		// Start the safaricom sms service
		if(node.getAttribute("safaricom_user") != null) {
			safaricomSMS = new BSafaricomSMS(node, logHandle);
		}

		qcomms = new ArrayList<BComm>();
		for(BElement nd : node.getElements()) {
			if(nd.getName().equals("COMM")) qcomms.add(new BComm(db, nd, logHandle));
		}

		log.info("Starting Soap SMS Server.");
	}

	public int getDelay() { return processdelay; }

	public int process() {
		log.info("Soap SMS Processing...");
		executing = true;
		
		if(safaricomSMS != null) safaricomSMS.getRefreshToken();

		boolean dbValid = db.isValid();
		if(dbValid) {
			for(BComm qcomm : qcomms) qcomm.process();
			
			sendMessage();
		} else {
			db.reconnect();
		}

		executing = false;
		return processdelay;
	}

	public void sendMessage() {
		String mysql = "SELECT sms_id, sms_number, sms_numbers, message, folder_id, sent, number_error, address_group_id, org_id FROM sms ";
		mysql += "WHERE (folder_id = 0) AND (message_ready = true) AND (sent = false) AND (number_error = false) ORDER BY sms_id";
		BQuery rs = new BQuery(db, mysql);

		while(rs.moveNext()) {
			boolean isSent = true;
			boolean numberError = false;
			String msg = rs.getString("message");
			if(msg == null) msg = "";
			String number = rs.getString("sms_number");
			String numbers = rs.getString("sms_numbers");
			if(number == null) number = "";
			if(rs.getString("address_group_id") ==  null) {
				number = number.replace("\n", ",").replace("\r", "").replace("\"", "").replace("'", "").replace("/", "").replace("+", "").trim();
				if(number.startsWith("0")) number = "254" + number.substring(1, number.length());
				
				if((number.length() > 11) && (number.length() < 15) && BNumberFormat.isNumeric(number)) {
					isSent = sendSMS(number.trim(), msg, rs.getString("sms_id"), rs.getString("org_id"), false);
				} else {
					numberError = true;
				}

				if((numbers != null) && (numbers.trim().length() > 7)) {
					numbers = numbers.replace("\n", ",").replace("\r", "").replace("\"", "").replace("'", "").replace("/", "").replace("+", "").trim();
					System.out.println("Sending messages for numbers : " + numbers);
					
					String[] nums = numbers.split(",");
					for(String num : nums) {
						System.out.println("Sending number : " + num);

						if((num != null) && (num.length() > 3)) {
							num = num.replace("\n", ",").replace("\r", "").replace("\"", "").replace("'", "").replace("/", "").replace("+", "").trim();
							if(num.length() == 9) num = "254" + num;
							else if(num.startsWith("0")) num = "254" + num.substring(1, num.length());
							
							if((num.length() > 11) && (num.length() < 15) && BNumberFormat.isNumeric(num)) {
								isSent = sendSMS(num, msg, rs.getString("sms_id"), rs.getString("org_id"), false);
							} else {
								numberError = true;
							}
						}
					}
					isSent = true;
				}
			}

			mysql = "SELECT sms_address.sms_address_id, address.mobile ";
			mysql += "FROM address INNER JOIN sms_address ON address.address_id = sms_address.address_id ";
			mysql += "WHERE (sms_address.sms_id	= " + rs.getString("sms_id") + ")";
			BQuery rsa = new BQuery(db, mysql);
			while(rsa.moveNext()) {
				number = rsa.getString("mobile");
				if(number == null) number = "";
				number = number.replace("\n", ",").replace("\r", "").replace("\"", "").replace("'", "").replace("/", "").replace("+", "").trim();
				if(number.startsWith("0")) number = "254" + number.substring(1, number.length());
				
				if((number.length() > 11) && (number.length() < 15) && BNumberFormat.isNumeric(number)) {
					isSent = sendSMS(number.trim(), msg, rs.getString("sms_id"), rs.getString("org_id"), false);
				} else {
					numberError = true;
				}
				isSent = true;
			}
			rsa.close();

			mysql = "SELECT address_members.address_member_id, address.mobile ";
			mysql += "FROM address INNER JOIN address_members ON address.address_id = address_members.address_id ";
			mysql += "WHERE (address.table_name = 'sms') ";
			mysql += " AND (address_members.address_group_id = " + rs.getString("address_group_id") + ") ";
			BQuery rsg = new BQuery(db, mysql);
			while(rsg.moveNext()) {
				number = rsg.getString("mobile");
				if(number == null) number = "";
				number = number.replace("\n", ",").replace("\r", "").replace("\"", "").replace("'", "").replace("/", "").replace("+", "").trim();
				if(number.startsWith("0")) number = "254" + number.substring(1, number.length());
				
				if((number.length() > 11) && (number.length() < 15) && BNumberFormat.isNumeric(number)) {
					isSent = sendSMS(number.trim(), msg, rs.getString("sms_id"), rs.getString("org_id"), false);
				} else {
					numberError = true;
				}
				isSent = true;
			}
			rsg.close();

			if(isSent) {
				rs.recEdit();		
				rs.updateField("sent", "true");
				rs.updateField("folder_id", "2");
				rs.recSave();
			}
			if(numberError) {
				rs.recEdit();		
				rs.updateField("number_error", "true");
				rs.recSave();
			}
		}
		rs.close();
	}

	public boolean sendSMS(String number, String message, String smsID, String orgID, boolean isRetry) {
		boolean isSent = false;
		
		if(message == null) return isSent;
		if(!smsOrgs.containsKey(orgID)) return isSent;
		if(smsOrgs.get(orgID)[8] == null) return isSent;
		
		int smsLen = message.length();
		Integer messageParts = 1;
		if(smsLen > 160) {
			messageParts = 1 + (smsLen / 153);
		}
		
		String mSql = "INSERT INTO sms_queue (sms_id, org_id, sms_number, message_parts, sms_price) VALUES (";
		mSql += smsID + "," + orgID + ", '" + number + "', " + messageParts.toString() + ", ";
		mSql += smsOrgs.get(orgID)[3] + ")";
		String correlator = db.executeAutoKey(mSql);
		String sendFon = smsOrgs.get(orgID)[4];

		int retry = 1;
		while(retry != 0) {
			if(retry > 1) System.out.println(number + " MESSAGE RESENDING RETRY\n");
			String sendResults = null;
			
			boolean airtelNo = false;
			if(airtelBlock1.contains(number.substring(0, 5))) airtelNo = true;
			if(airtelBlock2.contains(number.substring(0, 6))) airtelNo = true;
			
			if(smsOrgs.get(orgID)[10].equals("2")) { 				// For sending Afica is talking SMS
				sendResults = aitSend(number, message, orgID);
			} else if(smsOrgs.get(orgID)[10].equals("3")) { 		// For sending airTouch SMS
				sendResults = airTouch(number, message, orgID);
			} else if(smsOrgs.get(orgID)[10].equals("4")) { 		// For sending Hormuud SMS
				sendResults = HormuudSend(number, message, orgID);
			} else {
				sendResults = sendSMS(number, message, orgID, correlator);
			}
			
			if(sendResults == null) {	// retry once for a error on the sending
				try { Thread.sleep(1000); } catch(InterruptedException ex) {}
				if(retry < 5) retry++;
				else retry = 0;
			} else if(sendResults.equals("SVC0901")) { // retry twice for a error on the sending
				try { Thread.sleep(2000); } catch(InterruptedException ex) {}
				if(retry < 10) retry++;
				else retry = 0;
			} else {
				db.executeUpdate("UPDATE sms_queue SET send_results = '" + sendResults + "' WHERE sms_queue_id = " + correlator);
				
				mSql = "UPDATE sms_configs SET send_code = '" + sendResults + "', last_sent = current_timestamp, ";
				if("POL0904".equals(sendResults)) mSql += "send_error = true, narrative = 'Need credit top up' ";
				else mSql += "send_error = false, narrative = null ";
				mSql += "WHERE sms_config_id  = 0";
				db.executeUpdate(mSql);
				
				retry = 0;
				isSent = true;
			}
		}
		
		return isSent;
	}
		
	public String sendSMS(String number, String message, String orgID, String correlator) {
		String sendResults = null;

		if(safaricomSMS == null) return sendResults;
		
		String senderName = smsOrgs.get(orgID)[8];
		JSONObject jResult = safaricomSMS.sendSMS(senderName, number, message, correlator);
		if(jResult.has("statusCode")) sendResults = jResult.getString("statusCode");

		return sendResults;
	}

	public String aitSend(String number, String message, String orgID) {
		String resp = "";

		String userName = smsOrgs.get(orgID)[6];
		String apiKey = smsOrgs.get(orgID)[7];
		String senderName = smsOrgs.get(orgID)[8];

		/* Initialize SDK */
		AfricasTalking.initialize(userName, apiKey);

		System.out.println("Africa is talking SMS : " + senderName);

		/* Get the SMS service */
		SmsService aitSms = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);

		String[] recipients = new String[1];
		recipients[0] = "+" + number;

		/* That’s it, hit send and we’ll take care of the rest */
		try {
			List<Recipient> response = aitSms.send(message, senderName, recipients, true);
			for (Recipient recipient : response) {
				resp = recipient.messageId;
				System.out.println(recipient.number + " : " + resp + " : " + recipient.status);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return resp;
	}

	public String airTouch(String number, String message, String OrgID){
		String resp = "";

		try{
			OkHttpClient client = new OkHttpClient();
			HttpUrl.Builder urlBuilder = HttpUrl.parse("https://client.airtouch.co.ke:9012/sms/api").newBuilder();
			urlBuilder.addQueryParameter("issn", "FCM_TRAVEL");
			urlBuilder.addQueryParameter("msisdn", number);
			urlBuilder.addQueryParameter("text",message);
			urlBuilder.addQueryParameter("username", "travel");
			urlBuilder.addQueryParameter("password", "afd1d26c7571daeb3aa87c0d4a9521c0");
			String url = urlBuilder.build().toString();
			System.out.println("Airtouch SMS " + url);

			Request request = new Request.Builder()
                     .url(url)
                     .build();
			Response response = client.newCall(request).execute();
			resp = response.toString();
			
		}catch(Exception e){
			log.info("Air Touch Error:"+e);
		}

		return resp;
	}

	public String HormuudSend(String number, String message, String orgID) {
		String resp = "";

		String userName = smsOrgs.get(orgID)[6];
		String apiKey = smsOrgs.get(orgID)[7];
		String senderName = smsOrgs.get(orgID)[8];

		System.out.println("Auth " + orgID + " " + userName + " " + apiKey);

		BHormuudSMS hormuud = new BHormuudSMS(userName, apiKey);
		resp = hormuud.sendSMS(number, message);

		return resp;
	}

	public boolean isExecuting() {
		return executing;
	}

	public void close() {
		log.info("Closing Soap SMS Server.");
	}

}

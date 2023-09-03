/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.server.escalation;

import java.util.logging.Logger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.DB.BUser;
import org.baraza.xml.BXML;
import org.baraza.xml.BElement;
import org.baraza.reports.BReportFile;
import org.baraza.server.mail.BMail;
import org.baraza.utils.BLogHandle;

public class BEscalation {
	Logger log = Logger.getLogger(BEscalation.class.getName());
	BDB db;
	BElement root;
	BLogHandle logHandle;
	String testemail;
	String title = "";
	String emailPattern = "";

	boolean executing = false;
	boolean runserver = true;
	int processdelay = 10000;

	public BEscalation(BDB db, BElement root, BLogHandle logHandle) {
		this.db = db;
		this.root = root;
		this.logHandle = logHandle;
		logHandle.config(log);

		emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

		// Get the main XMl configs
		testemail = root.getAttribute("testemail");
		title = root.getAttribute("title", "");
		processdelay = Integer.valueOf(root.getAttribute("processdelay", "2")).intValue()*60*1000;
	}

	public int getDelay() {
		return processdelay;
	}

	public int process() {
		// Create the mail class
		BMail mail = new BMail(root, logHandle);
		if(mail.getActive()) task(mail);
		mail.close();

		return processdelay;
	}

	public void task(BMail mail) {
		log.info("---------- Escalation Client Run : " + title);

		executing = true;

		// Get email limit to send at once
		Integer sendLimit = Integer.valueOf(root.getAttribute("send_limit", "0"));
		int sendCount = 0;

		for(BElement ell : root.getElements()) {
			String keyfield = ell.getAttribute("keyfield");
			String colNames = keyfield;
			for(BElement el : ell.getElements()) {
				String basetable = el.getAttribute("basetable");
				String function = el.getAttribute("function");
				if(function == null) function = el.getAttribute("fnct");

				colNames += ", ";
				if(basetable != null) colNames += basetable + "." + el.getValue();
				else if(function != null) colNames += "(" + function + ")";
				else colNames += el.getValue();
				
				if(el.getName().equals("ADDRESS")) colNames += " as emailaddress";
				else if(el.getName().equals("SUBJECT")) colNames += " as emailsubject";
				else if(function != null) colNames += " as " + el.getValue();
			}
			String mysql = "SELECT " + colNames + " FROM " + ell.getAttribute("table") + " ";
			if(ell.getAttribute("where") != null) mysql += " WHERE " + ell.getAttribute("where");
			log.info(mysql);
			
			String replyTo = null;
			String oeSql = "";
			
			// Make an SQL call before running
			String preQuery = ell.getAttribute("prequery");
			if(preQuery != null) {
				String preSql = "SELECT " + preQuery;
				db.executeQuery(preSql);
				if(db.getDBType() == 2) db.executeQuery("COMMIT");
			}

			BQuery rs = new BQuery(db, mysql);
			while(rs.moveNext()) {
				// Check on send limit
				if(sendLimit > 0) {
					if(sendCount > sendLimit) break;
				}

				Map<String, String> params = new HashMap<String, String>();
				Map<String, String> rptParams = new HashMap<String, String>();
				Map<String, String> headers = new HashMap<String, String>();
				Map<String, String> reports = new HashMap<String, String>();
				Map<String, String> files = new HashMap<String, String>();

				String ccto = null;
				String subject = rs.getString("emailsubject");
				String msg = "<HTML>\n<HEAD></HEAD>\n<BODY>\n";
				
				if(ell.getAttribute("reply.to") != null) {
					oeSql = "SELECT email_from FROM orgs WHERE org_id = " + rs.getString("org_id");
					replyTo = db.executeFunction(oeSql);
				}
				
				for(BElement el : ell.getElements()) {
					if(el.getName().equals("FIELD")) {
						if(rs.getString(el.getValue()) != null)
							msg += "<p>" + rs.getString(el.getValue()) + "</p>\n";
					} else if(el.getName().equals("BODYFIELD")) {
						String reference = el.getAttribute("reference");
						if(rs.getString(el.getValue()) != null)
							params.put(reference, rs.getString(el.getValue()));
						else
							params.put(reference, "");
					} else if(el.getName().equals("HEADER")) {
						String reference = el.getAttribute("reference");
						if(rs.getString(el.getValue()) != null)
							headers.put(reference, rs.getString(el.getValue()));
					} else if(el.getName().equals("PARAM")) {
						if(rs.getString(el.getValue()) != null) {
							rptParams.put(el.getValue(), rs.getString(el.getValue()));
							if(el.getAttribute("name") != null) {
								rptParams.put(el.getAttribute("name"), rs.getString(el.getValue()));
							}
						}
					} else if(el.getName().equals("CCTO")) {
						if(rs.getString(el.getValue()) != null) ccto = rs.getString(el.getValue());
					} else if(el.getName().equals("REPORT")) {
						if(rs.getString(el.getValue())!=null)
							reports.put(el.getValue(), rs.getString(el.getValue()));
					} else if(el.getName().equals("FILE")) {
						String fileName = rs.getString(el.getValue());
						String pathName = el.getAttribute("path");
						if(fileName != null) files.put(fileName, pathName + fileName);
					}
				}
				msg += "\n</BODY>\n</HTML>\n";

				for (String key : params.keySet()) {
					log.info(key + " : " + params.get(key));
					msg = msg.replace(key, params.get(key));
					subject = subject.replace(key, params.get(key));
				}
				
				//for (String key : rptParams.keySet()) log.info(key + " : " + rptParams.get(key));

				//System.out.println(msg);

				String emailaddress = rs.getString("emailaddress");
				if(emailaddress != null) emailaddress = emailaddress.replace(";", ",").replace(" ", "").trim();
				log.info("To : " + emailaddress + "\nCC : " + ccto + "\nSubject : " + subject);
				if(testemail != null) {
					emailaddress = testemail;
					log.info("Test email override : " + emailaddress);
				}
				
				if((emailaddress == null) && (ccto != null)) {
					emailaddress = ccto;
					ccto = null;
				}

				boolean gotreport = false;
				if(ell.getAttribute("attachment") != null) {
					BReportFile rf =  new BReportFile(db, logHandle);
					gotreport = rf.getReport(root.getAttribute("reportpath"), ell.getAttribute("attachment"), rs.getString(keyfield), rptParams);
				}

				boolean reportOkay = true;
				for(String report : reports.keySet()) {
					String reportFile = reports.get(report);
					BReportFile rf =  new BReportFile(db, logHandle);
					rf.setOutput(report);
					if(reportOkay) {
						reportOkay = rf.getReport(root.getAttribute("reportpath"), reportFile, rs.getString(keyfield), rptParams);
					}
				}

				if((emailaddress != null) && (reportOkay) && (isEmailValid(emailaddress))) {
					if(emailaddress.indexOf("@") > 1)
						mail.sendMail(emailaddress, ccto, replyTo, subject, msg, gotreport, headers, reports, files);
				} else if(!reportOkay){
					log.severe("Report generation error");
				} else {
					log.severe("inValid emails");
				}
										
				/* Close the problem log */
				String functable = root.getAttribute("functable");
				String actioncount = ell.getAttribute("actioncount");
				if(actioncount == null) {
					mysql = "SELECT " + ell.getAttribute("action") + "('" + rs.getString(keyfield) + "')";
				} else {
					mysql = "SELECT " + ell.getAttribute("action") + "('" + actioncount;
					mysql += "', '" + rs.getString(keyfield) + "')";
				}

				if(functable != null) mysql += " FROM " + functable;
				if(mail.getActive()) db.executeQuery(mysql);
				if(db.getDBType() == 2) db.executeQuery("COMMIT");

				if(!mail.getActive()) break;

				sendCount++;
			}
			rs.close();
		}

		executing = false;
	}

	public String fmtdate(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm a");
		return sdf.format(dt);
	}

	public boolean isExecuting() {
		return executing;
	}

	public boolean isEmailValid(String emailAddress) {
		boolean isValid = true;
		String emails[] = emailAddress.split(",");
		for(String email : emails) {
			if(!Pattern.compile(emailPattern).matcher(email).matches()) isValid = false;
		}
		return isValid;
	}

	public void close() {
		// Close the connections
		db.close();
	}

}

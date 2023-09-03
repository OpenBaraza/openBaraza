/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.com;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Enumeration;
import java.net.URL;
import java.net.InetAddress;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.codec.binary.Base64;

import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.xml.BElement;
import org.baraza.utils.BNetwork;

public class BLicenseRegister extends HttpServlet {


	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) { 
		PrintWriter out = null;
		try { out = response.getWriter(); } catch(IOException ex) {}
		String resp = "";

		String dbconfig = "java:/comp/env/jdbc/database";
		BDB db = new BDB(dbconfig);
		// If there is no DB connection
		if(db == null) {
			resp = "DB access error";
			out.println(resp);
			return;
		}
		
		Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
			String ce = (String)e.nextElement();
			System.out.println(ce + ":" + request.getParameter(ce));
		}
		
		String systemKey = request.getParameter("system_key");
		String orgName = request.getParameter("org_name");
		String sysKey = request.getParameter("sys_key");
		if(systemKey == null) {
			response.setContentType("application/json;charset=\"utf-8\"");
			resp = getLicense(db, request.getRemoteAddr(), request.getRemoteUser(), orgName, sysKey);
		} else {
			response.setContentType("text/html");
			resp = setLicense(db, systemKey, request);
		}
		 
		out.println(resp);
		db.close();
	}
	
	private String setLicense(BDB db, String sysKey, HttpServletRequest request) {
		String resp = "";
		String holder = request.getParameter("org_name");
		String productKey = request.getParameter("system_identifier");
		String MachineID = request.getParameter("mac_address");
		String databaseID = request.getParameter("database_identifier");
		
		String mysql = "SELECT subscription_id, system_key, subscribed, subscribed_date FROM subscriptions "
			+ "WHERE (approve_status = 'Approved') AND (subscribed = false) "
			+ "AND (system_key = '" + sysKey + "')";
		BQuery rs = new BQuery(db, mysql);
		if(rs.moveFirst()) {
			BLicense license = new BLicense();
			resp = license.createLicense(holder, productKey, MachineID, databaseID);
			db.executeQuery("UPDATE subscriptions SET subscribed = true, subscribed_date = current_timestamp WHERE (system_key = '" + sysKey + "')");
		} else {
			resp = "ERROR";
		}
		rs.close();
				
		return resp;
	}
	
	private String getLicense(BDB db, String remoteAddr, String remoteUser, String orgName, String sysKey) {
		JSONObject jShd = new JSONObject();
		jShd.put("error", true);
		jShd.put("msg", "License regitration failed");

		
		if((orgName == null) || (sysKey == null) || (orgName.trim().length() < 2) ||  (sysKey.trim().length() < 32)) {
			jShd.put("error", true);
			jShd.put("msg", "You must enter a valid organization name and system key");
			return jShd.toString();
		}
		
		// Send these parameters to the server
		try {		
			// Update the system_key and org_name
			db.executeQuery("UPDATE orgs SET system_key = '" + sysKey + "' WHERE org_id = 0");
			db.executeQuery("UPDATE orgs SET org_name = '" + orgName + "' WHERE org_id = 0");

			// Get the organisation name, system key and system identifier
			Map<String, String> params = db.readFields("org_name, system_key, system_identifier", "orgs WHERE org_id = 0");
			String sysID = params.get("system_identifier");
			if(sysID == null) {
				sysID = UUID.randomUUID().toString();
				db.executeQuery("UPDATE orgs SET system_identifier = '" + sysID + "' WHERE org_id = 0");
				params.put("system_identifier", sysID);
			}
			
			// Get the database ID
			String dbName = db.getCatalogName();
			String dbID = db.executeFunction("SELECT datid FROM pg_stat_database WHERE datname = '" + dbName + "'");
			params.put("database_identifier", dbID);
		
			// Get the connecting interface MAC address
			URL myURL = new URL("http://hcm.openbaraza.org/innerkonsult/registerlicense");
			//URL myURL = new URL("http://192.168.0.7:9090/hr/registerlicense");
			InetAddress hostAddress = InetAddress.getByName(myURL.getHost());
			Socket soc = new Socket(hostAddress, 80);
System.out.println("Connecting IP : " + soc.getLocalAddress().getHostAddress());
			// Get MAC address and save on database
			BNetwork net = new BNetwork();
			String macAddr = net.getMACAddress(soc.getLocalAddress().getHostAddress());
			soc.close();
			
			if(macAddr == null) return null;
			db.executeQuery("UPDATE orgs SET mac_address = '" + macAddr + "' WHERE org_id = 0");
			params.put("mac_address", macAddr);
			
			String licStr = net.sendPost(myURL, params);
			if(licStr != null) {
				if(saveLicense(db, licStr)) {
					jShd.put("error", false);
					jShd.put("msg", "Registred okay, refresh page");
				}
			}
		} catch (MalformedURLException ex) {
			System.out.println("License Registration : " + ex);
		} catch (UnknownHostException ex) {
			System.out.println("License Registration : " + ex);
		} catch (IOException ex) {
			System.out.println("License Registration : " + ex);
		}
		
		return jShd.toString();
	}
	
	private boolean saveLicense(BDB db, String licStr) {
		boolean isOkay = false;
		
		String lics[] = licStr.split("===================");
		
		if(lics.length == 2) {
			Base64 decd = new Base64();
			
			// Save the data
			BQuery rs = new BQuery(db, "SELECT org_id, public_key, license FROM orgs WHERE org_id = 0");
			rs.moveFirst();
			rs.recEdit();
			rs.updateBytes("license", decd.decodeBase64(lics[0]));
			rs.updateBytes("public_key", decd.decodeBase64(lics[1]));
			rs.recSave();
			rs.close();
			
			isOkay = true;
		}
		
		return isOkay;
	}

	

}

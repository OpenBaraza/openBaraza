/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2020.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONArray;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.baraza.utils.BWebUtils;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.DB.BUser;
import org.baraza.DB.BTranslations;
import org.baraza.xml.BXML;
import org.baraza.xml.BElement;

public class BDataServer extends HttpServlet {
	Logger log = Logger.getLogger(BDataServer.class.getName());

	BElement root = null;
	BDB db = null;
	BDataOps dataOps = null;
	BTranslations translations = null;
	String reportPath = "./";
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext context = config.getServletContext();
		String xmlCnf = config.getInitParameter("xmlfile");
		String projectDir = context.getInitParameter("projectDir");
		String ps = System.getProperty("file.separator");
		String xmlFile = context.getRealPath("WEB-INF") + ps + "configs" + ps + xmlCnf;
		reportPath = context.getRealPath("reports") + ps;
		if(projectDir != null) {
			xmlFile = projectDir + ps + "configs" + ps + xmlCnf;
			reportPath = projectDir + ps + "reports" + ps;
		}

		BXML xml = new BXML(xmlFile, false);
		
		if(xml.getDocument() != null) {
			root = xml.getRoot();
		
			String dbConfig = "java:/comp/env/jdbc/database";
			db = new BDB(dbConfig);
			db.setOrgID(root.getAttribute("org"));
			
			dataOps = new BDataOps(root, db);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String resp = "";

		log.info("Start Data Server");
		
		BWebUtils.showHeaders(request);
		BWebUtils.showParameters(request);
		
		String action = request.getHeader("action");
		if(action == null) return;
System.out.println("BASE 2010 : " + action);

		if(!db.isValid()) {
			db.reconnect("java:/comp/env/jdbc/database");
			db.setOrgID(root.getAttribute("org"));
		}

		int contentType = 1; 		// JSON content
		
		ServletContext context = getServletContext();
		if(context.getAttribute("translations") !=  null) {
			translations = (BTranslations) context.getAttribute("translations");
		}
		
		JSONObject jResp = new JSONObject();
		if(action.equals("authorization")) {
			jResp = dataOps.authenticate(request);
		} else if(action.equals("udata")) {
			jResp = dataOps.unsecuredData(request);
		} else if(action.equals("uread")) {
			jResp = dataOps.unsecuredReadData(request);
		} else if(action.equals("uform")) {
			jResp = dataOps.getUForm(request, translations);
		} else if(action.equals("email_reset")) {			// Recover password with email
			jResp = dataOps.emailReset(request);
		} else {
			jResp = dataOps.reAuthenticate(request.getHeader("authorization"));
			// Ensure the secure operations happen after re-authentication
			if(jResp.has("ResultCode") && (jResp.getInt("ResultCode") == 0)) {
				String userId = jResp.getString("userId");
System.out.println("BASE userId : " + userId);

				if(action.equals("menu")) {
					jResp = dataOps.getMenu(request, userId);
				} else if(action.equals("dashboard")) {
					jResp = dataOps.getDashboard(request, userId);
				} else if(action.equals("view")) {
					jResp = dataOps.getView(request, userId);
				} else if(action.equals("form")) {
					jResp = dataOps.getForm(request, userId, translations);
				} else if(action.equals("grid")) {
					jResp = dataOps.getGridDef(request, userId, translations);
				} else if(action.equals("data")) {
					jResp = dataOps.securedData(request, userId);
				} else if(action.equals("read")) {
					jResp = dataOps.readData(request, userId);
				} else if(action.equals("grid_update")) {
					jResp = dataOps.updateGrid(request, userId);
				} else if(action.equals("actions")) {
					jResp = dataOps.doActions(request, userId);
				} else if(action.equals("report")) {
					resp = dataOps.getReport(request, userId, reportPath);
					contentType = 2;								// HTML content
				} else if(action.equals("pdfreport")) {
					dataOps.getPdfReport(request, response, userId, reportPath);
					contentType = 3;								// PDF content
				} else if(action.equals("attendance")) {			// Add attendance
					jResp = dataOps.addAttendance(request, userId);
				} else if(action.equals("reset")) {					// Change the password
					jResp = dataOps.changePassword(request, userId);
				} else {
					jResp.put("ResultCode", 10);
					jResp.put("userId", "-1");
					jResp.put("message", "Make right action call");
				}
			}
		}

		// Send feedback
		if(contentType == 1) {
			resp = jResp.toString();
			response.setContentType("application/json;charset=\"utf-8\"");
System.out.println("BASE jRETURN : " + resp);

			try { 
				PrintWriter out = response.getWriter(); 
				out.println(resp);
			} catch(IOException ex) {}
		} else if(contentType == 2) {
			response.setContentType("text/html");
			try { 
				PrintWriter out = response.getWriter(); 
				out.println(resp);
			} catch(IOException ex) {}
		}		

		log.info("End Data Server");
	}
	
	public void destroy() {
		db.close();
	}

}

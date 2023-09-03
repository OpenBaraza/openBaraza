/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.json.JSONArray;
import org.json.JSONObject;


import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.DB.BUser;
import org.baraza.xml.BElement;
import org.baraza.xml.BXML;


public class BAnalytics extends HttpServlet {

	public HttpSession session;

	BDB db = null;
	BUser user = null;
	String contract = "t";

	String orgId = null;
	String userID = null;
	String entity_n = null;
	String xmlBase = null;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext context = config.getServletContext();
		String projectDir = context.getInitParameter("projectDir");
		String ps = System.getProperty("file.separator");
		xmlBase = context.getRealPath("WEB-INF") + ps + "configs" + ps;
		if(projectDir != null) xmlBase = projectDir + ps + "configs" + ps;
		
		db = new BDB("java:/comp/env/jdbc/database");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		JSONObject jResp =  new JSONObject();

		ServletContext context = getServletContext();

		String viewKey = request.getParameter("view");
		String tag = request.getParameter("tag");
		String key = request.getParameter("key");
		if(key == null) key = "0";
		
		BXML xml = new BXML(context, request, true);
		BElement root = xml.getRoot();
		BElement elAnalytics = null;

		if(viewKey == null) {
			elAnalytics = root.getElementByName("ANALYTICS");
		} else {
			String viewKeys[] = viewKey.split(":");
			elAnalytics = root.getElementByKey(viewKeys[0]);
		}
		
		if(!db.isValid()) db.reconnect("java:/comp/env/jdbc/database");
		db.setOrgID(root.getAttribute("org"));
		db.setUser(request.getRemoteAddr(), request.getRemoteUser());

		user = db.getUser();

		if(user != null) {
			orgId = user.getUserOrg();
			userID = user.getUserID();
		}
		System.out.print(userID);
		
		if ("getList".equals(tag)){
			jResp = getAnalyticList(elAnalytics);
		} else if ("getAnalytic".equals(tag)) {
			BElement elA = elAnalytics.getElementByKey(key);
			//System.out.println(key + "\n" + elA.toString());

			if(elA == null) {
				jResp.put("success", 0);
				jResp.put("msg", "No valid key selection");
			} else if(elA.getName().equals("PIECHART")) {
				jResp = getAnalyticPie(elA);
			} else if(elA.getName().equals("LINECHART")) {
				jResp = getAnalyticLine(elA);
			}
		}
		
		response.setContentType("application/json;charset=\"utf-8\"");
		try {
			PrintWriter out = response.getWriter();
			out.println(jResp.toString());
		} catch(IOException ex) {

			System.out.print("The errror is "+ex);
		}
	}

	public JSONObject getAnalyticList(BElement elAnalytics) {
		JSONObject jResp =  new JSONObject();

		JSONArray aData = new JSONArray();
		for(BElement el : elAnalytics.getElements()) {
			JSONObject jData =  new JSONObject();
			jData.put("type", el.getName());
			jData.put("key", el.getAttribute("key"));
			jData.put("name", el.getAttribute("name"));
			aData.put(jData);
		}
		jResp.put("analytics", aData);

		System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject getAnalyticPie(BElement elA) {
		JSONObject jResp =  new JSONObject();
	
		BQuery rs = new BQuery(db, elA, null, null, false);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			for(BElement el : elA.getElements()) {
				jData.put(el.getAttribute("type"), rs.formatData(el));
			}
			
			aData.put(jData);
		}
		rs.close();
		
		jResp.put("type", elA.getName());
		jResp.put("key", elA.getAttribute("key"));
		jResp.put("name", elA.getAttribute("name"));
		jResp.put("analytic", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject getAnalyticLine(BElement elA) {
		JSONObject jResp =  new JSONObject();

		BQuery rs = new BQuery(db, elA, null, null, false);
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			for(BElement el : elA.getElements()) {
				jData.put(el.getValue(), rs.formatData(el));
			}
			
			aData.put(jData);
		}
		rs.close();
		jResp.put("line_data", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}

    
}

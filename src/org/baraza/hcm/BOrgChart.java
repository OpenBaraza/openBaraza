package org.baraza.hcm;

import java.io.PrintWriter;
import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.xml.BXML;
import org.baraza.xml.BElement;
import org.baraza.DB.BUser;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;

public class BOrgChart extends HttpServlet {

	BDB db = null;
	BUser user = null;
	String orgId = null;
	String roleIds = null;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		db = new BDB("java:/comp/env/jdbc/database");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jResp =  new JSONObject();

		ServletContext context = getServletContext();
		BXML xml = new BXML(context, request, false);
		BElement root = xml.getRoot();
		
		if(!db.isValid()) db.reconnect("java:/comp/env/jdbc/database");
		db.setOrgID(root.getAttribute("org"));
		db.setUser(request.getRemoteAddr(), request.getRemoteUser());

		user = db.getUser();
		if(user != null) orgId = user.getUserOrg();

		String tag = request.getParameter("tag");
		if ("org_chart".equals(tag)) {
			jResp = getOrgChat(request);
		}

		response.setContentType("application/json;charset=\"utf-8\"");
		try {
			PrintWriter out = response.getWriter();
			out.println(jResp.toString());
		} catch(IOException ex) {

			System.out.print("The errror is "+ex);
		}
	}

	public JSONObject getOrgChat(HttpServletRequest request) {
		String mySql = "SELECT top_department_role_id FROM orgs WHERE org_id = " + orgId;
		String topDeptRoleId = db.executeFunction(mySql);

		mySql = "SELECT department_role_id, ln_department_role_id, department_role_name "
			+ "FROM department_roles "
			+ "WHERE department_role_id = " + topDeptRoleId
			+ " AND org_id = " + orgId;
		BQuery rs = new BQuery(db, mySql);

		String roles = "[";
		if(rs.moveNext()) {
			roleIds = rs.getString("department_role_id");
			String roleName = rs.getString("department_role_name").replace("'", "");
			roles += "['" + roleName + "', '']";
			roles += getOrgChat(rs.getString("department_role_id"), roleName);
		}
		rs.close();
		roles += "]";

		JSONArray jaRoles = new JSONArray(roles);
		//System.out.println(roles);

		JSONObject jResp =  new JSONObject();
		jResp.put("org_data", jaRoles);

		return jResp;
	}

	public String getOrgChat(String ln_role_id, String ln_role_name) {
		String mySql = "SELECT department_role_id, ln_department_role_id, department_role_name "
			+ "FROM department_roles "
			+ "WHERE ln_department_role_id = " + ln_role_id
			+ " AND department_role_id <> " + ln_role_id
			+ " AND department_role_id NOT IN (" + roleIds + ")";
		BQuery rs = new BQuery(db, mySql);

		String roles = "";
		while(rs.moveNext()) {
			roleIds += "," + rs.getString("department_role_id");
			String roleName = rs.getString("department_role_name").replace("'", "");
			roles += ",['" + roleName + "', '" + ln_role_name + "']";
			roles += getOrgChat(rs.getString("department_role_id"), roleName);
		}
		rs.close();

		return roles;
	}


}

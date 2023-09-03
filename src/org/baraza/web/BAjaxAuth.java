/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.util.Map;
import java.io.PrintWriter;
import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.DB.BDB;

public class BAjaxAuth extends HttpServlet {

	BDB db = null;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		String dbConfig = "java:/comp/env/jdbc/database";
		db = new BDB(dbConfig);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		ServletContext context = getServletContext();
		
		JSONObject auth = new JSONObject();
		
		String userName = request.getParameter("j_username");
		String password = request.getParameter("j_password");

		boolean success = false;
		if ((userName != null) && (password != null)) {
			try {
				// authenticate the current request
				if(request.getUserPrincipal() == null) request.login(userName, password);
				else success = true;
				
				if(request.getUserPrincipal() != null) success = true;
			} catch (ServletException ex) {
				success = false;
				auth.put("error", ex.toString());
			} catch (Exception ex) {
				success = false;
				auth.put("error", "Error configuring session: " + ex.getMessage());
			}
		} else {
			success = false;
			auth.put("error", "Username or password missing");
		}
		
		if(!db.isValid()) db.reconnect("java:/comp/env/jdbc/database");
		
		if(success) {
			auth.put("auth", true);

			String myFields = "entity_id, entity_type_id, org_id, entity_name, function_role";
			String mySource = "vw_entitys WHERE user_name = '" + userName + "' LIMIT 1";
			Map<String, String> mFields = db.readFields(myFields, mySource);
			for(String fieldName : mFields.keySet()) auth.put(fieldName, mFields.get(fieldName));
			
			JSONArray jaRoles = new JSONArray();
			mySource = "vw_entity_subscriptions WHERE user_name = '" + userName + "'";
			Map<String, String> mRoles = db.getMapData("entity_type_id", "entity_role", mySource);
			for(String roleId : mRoles.keySet()) jaRoles.put(mRoles.get(roleId));
			auth.put("roles", jaRoles);
		} else {
			auth.put("auth", false);
		}
		
		//System.out.println("\n\n AUTH : " + auth.toString() + "\n");
		
		response.setContentType("application/json;charset=\"utf-8\"");
		try {
			PrintWriter out = response.getWriter(); 
			out.println(auth.toString());
		} catch(IOException ex) {}
	}
}


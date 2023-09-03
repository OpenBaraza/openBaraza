/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.io.PrintWriter;
import java.io.IOException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.utils.BWebUtils;
import org.baraza.xml.BElement;
import org.baraza.DB.BUser;
import org.baraza.DB.BDB;

public class BWebCalendar extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		ServletContext context = getServletContext();
		BWeb web = new BWeb(context, request);
		BDB db = web.getDB();
		BUser user = web.getUser();
		
		//BWebUtils.showParameters(request);
		String startDate = request.getParameter("start");
		String endDate = request.getParameter("end");
		
		JSONArray aEvents = web.getCalendar(startDate, endDate);
		//System.out.println(aEvents.toString());
		
		response.setContentType("application/json;charset=\"utf-8\"");
		try {
			PrintWriter out = response.getWriter(); 
			out.println(aEvents.toString());
		} catch(IOException ex) {}

		web.close();
	}
}


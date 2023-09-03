package org.baraza.hcm;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Date;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.text.ParseException;
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

import org.baraza.utils.BWebUtils;
import org.baraza.xml.BElement;
import org.baraza.web.BWeb;
import org.baraza.DB.BUser;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;

public class BZKTecoApi extends HttpServlet {
	BDB db = null;
	BUser user = null;
	String orgId = "0";
	String userID = "0";
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		db = new BDB("java:/comp/env/jdbc/database");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		ServletContext context = getServletContext();
		if(!db.isValid()) db.reconnect("java:/comp/env/jdbc/database");
		JSONObject jResp =  new JSONObject();
		
		String sBody = BWebUtils.requestBody(request);
		System.out.println(sBody);

		JSONObject jBody = new JSONObject(sBody);
		JSONArray jaData = jBody.getJSONArray("attendance");
		String machineSerial = jBody.getString("serial");
		String machineDate = jBody.getString("date_time");
		
		checkMachineDate(machineSerial, machineDate);

		for(int j = 0; j < jaData.length(); j++) {
			String logId = checkLogId(jaData.getJSONObject(j), machineSerial);
			if(logId == null) {
				System.out.println(jaData.getJSONObject(j));
			
				addAccessLog(jaData.getJSONObject(j), machineSerial);
			}
		}

		String updSql = "SELECT process_zkteco_logs('0', '0', '0')";
		db.executeFunction(updSql);
		
		response.setContentType("application/json;charset=\"utf-8\"");
		try {
			PrintWriter out = response.getWriter();
			out.println(jResp.toString());
		} catch(IOException ex) {}
	}
	
	public void checkMachineDate(String machineSerial, String machineDate) {
		try {
			SimpleDateFormat dFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date mDateTime = dFormatter.parse(machineDate);
			Date cDateTime = new Date();

			long minutes = Math.abs(cDateTime.getTime() - mDateTime.getTime()) / 60000;
			System.out.println("Machine date : " + mDateTime.toString() + " as at " + cDateTime.toString());
			System.out.println("Minutes difference : " + minutes);

			if(minutes > 70) {
				String emailId = db.executeFunction("SELECT sys_email_id FROM sys_emails WHERE use_type = 50 AND org_id = 0");

				String inSql = "INSERT INTO sys_emailed (sys_email_id, org_id, table_id, "
					+ "table_name, email_type, mail_body) "
					+ "VALUES (" + emailId + ", 0, 0, 'zkteco_logs', 50, 'Serial number : "
					+ machineSerial + " Machine date " + machineDate + " at " + cDateTime.toString() + "')";
				db.executeQuery(inSql);
			}
		} catch(ParseException ex) {
			System.out.println("Machine date error : " + ex);
		}
	}
	
	public String checkLogId(JSONObject jData, String machineSerial) {
		String logSql = "SELECT zkteco_log_id "
			+ "FROM zkteco_logs "
			+ "WHERE (log_date = '" + jData.getString("attendance").trim() + "'::timestamp) "
			+ "AND (machine_id = '" + machineSerial + "') "
			+ "AND (staff_id = '" + jData.getString("id") + "')";
		String logId = db.executeFunction(logSql);
		return logId;
	}

	public void addAccessLog(JSONObject jData, String machineSerial) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		mData.put("machine_id", machineSerial);
		mData.put("staff_id", jData.getString("id"));
		mData.put("log_date", jData.getString("attendance"));
		mData.put("mode_state", jData.getString("mode_state"));
		mData.put("login_mode", jData.getString("login_mode"));
		mData.put("check_state", jData.getString("check_state"));

		String inSql = "INSERT INTO zkteco_logs (org_id, machine_id, staff_id, log_date, "
			+ "mode_state, login_mode, check_state) "
			+ "VALUES (?,?,?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	}


}

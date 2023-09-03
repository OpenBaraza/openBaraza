/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2020.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.LinkedHashMap;
import java.io.PrintWriter;
import java.io.IOException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.utils.BWebUtils;
import org.baraza.xml.BElement;
import org.baraza.DB.BUser;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;

public class BCanvas extends HttpServlet {

	BDB db = null;
	BUser user = null;
	String bmcId = "0";
	String scrumboardId = "0";
	String orgId = "0";
	String userID = "0";
	
	Map<String, Date> eventCall;
	Map<String, JSONObject> eventData;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		db = new BDB("java:/comp/env/jdbc/database");

		eventCall = new HashMap<String, Date>();
		eventData = new HashMap<String, JSONObject>();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		ServletContext context = getServletContext();
		BWeb web = new BWeb(context, request);
		
		user = web.getUser();
		if(user != null) {
			orgId = user.getUserOrg();
			userID = user.getUserID();
		}
		if(orgId == null) orgId = "0";
		if(userID == null) userID = "-1";
		
		if(!db.isValid()) db.reconnect("java:/comp/env/jdbc/database");
		db.setOrgID(web.getRoot().getAttribute("org"));
		
		//BWebUtils.showParameters(request);
		//System.out.println(BWebUtils.requestBody(request));
		
		JSONObject jResp =  new JSONObject();

		HttpSession session = request.getSession(true);
		if(session.getAttribute("bmcId") != null) {
			bmcId = (String)session.getAttribute("bmcId");
		}
		if(session.getAttribute("scrumboardId") != null) {
			scrumboardId = (String)session.getAttribute("scrumboardId");
		}

		/* Calling functions 
		canvas?fnct=addBmcNote
		canvas?fnct=editBmcNote
		canvas?fnct=moveBmcNote
		canvas?fnct=delBmcNote
		canvas?fnct=getBmcNote
		canvas?fnct=getBmcNotes

		canvas?fnct=addVpcNote
		canvas?fnct=editVpcNote
		canvas?fnct=moveVpcNote
		canvas?fnct=delVpcNote
		canvas?fnct=getVpcNote
		canvas?fnct=getVpcNotes

		canvas?fnct=addScrumNote
		canvas?fnct=editScrumNote
		canvas?fnct=moveScrumNote
		canvas?fnct=delScrumNote
		canvas?fnct=getScrumNote
		canvas?fnct=getScrumNotes
		canvas?fnct=getScrumArchives
		*/

		String fnct = request.getParameter("fnct");
		String eventId = fnct + userID;
		//System.out.println("BASE fnct : " + userID + " : " + fnct);
		if("addBmcNote".equals(fnct)) {
			jResp = addBmcNote(request);
		} else if("editBmcNote".equals(fnct)) {
			jResp = editBmcNote(request);
		} else if("moveBmcNote".equals(fnct)) {
			jResp = moveBmcNote(request);
		} else if("delBmcNote".equals(fnct)) {
			jResp = delBmcNote(request);
		} else if("getBmcNote".equals(fnct)) {
			jResp = getBmcNote(request);
		} else if("getBmcNotes".equals(fnct)) {
			jResp = getBmcNotes(request);
		} else if("addVpcNote".equals(fnct)) {
			jResp = addVpcNote(request);
		} else if("editVpcNote".equals(fnct)) {
			jResp = editVpcNote(request);
		} else if("moveVpcNote".equals(fnct)) {
			jResp = moveVpcNote(request);
		} else if("delVpcNote".equals(fnct)) {
			jResp = delVpcNote(request);
		} else if("getVpcNote".equals(fnct)) {
			jResp = getVpcNote(request);
		} else if("getVpcNotes".equals(fnct)) {
			jResp = getVpcNotes(request);
		} else if("addScrumNote".equals(fnct)) {
			jResp = addScrumNote(request);
		} else if("editScrumNote".equals(fnct)) {
			jResp = editScrumNote(request);
		} else if("moveScrumNote".equals(fnct)) {
			jResp = moveScrumNote(request);
		} else if("delScrumNote".equals(fnct)) {
			jResp = delScrumNote(request);
		} else if("archiveScrumNote".equals(fnct)) {
			jResp = archiveScrumNote(request);
		} else if("getScrumNote".equals(fnct)) {
			jResp = getScrumNote(request);
		} else if("getScrumNotes".equals(fnct)) {
			jResp = getScrumNotes(request, "false");
		} else if("getScrumArchives".equals(fnct)) {
			jResp = getScrumNotes(request, "true");
		} else if("assignScrumNote".equals(fnct)) {
			jResp = assignScrumNote(request);
		} else if("unassignScrumNote".equals(fnct)) {
			jResp = assignScrumNote(request);
		} else if("getScrumBoards".equals(fnct)) {
			jResp = getLastCall(eventId);
			if(jResp == null) {
				jResp = getScrumBoards(request);
				setLastCall(eventId, jResp);
			}
		} else if("getScrumBoard".equals(fnct)) {
			jResp = getLastCall(eventId);
			if(jResp == null) {
				jResp = getScrumBoard(request);
				setLastCall(eventId, jResp);
			}
		}else if("getStandupMessages".equals(fnct)) {
			jResp = getLastCall(eventId);
			if(jResp == null) {
				jResp = getStandupMessages(request);
				setLastCall(eventId, jResp);
			}
		} else if("getLastMessage".equals(fnct)) {
			jResp = getLastCall(eventId);
			if(jResp == null) {
				jResp = getLastMessage(request);
				setLastCall(eventId, jResp);
			}
		} else if("addStandupMessage".equals(fnct)) {
			jResp = addStandupMessage(request);
		} else if("addChatMessage".equals(fnct)) {
			jResp = addChatMessage(request);
		} else if("getBMCProgress".equals(fnct)) {
			jResp = getBMCProgress(request);
		} else if("getVPCProgress".equals(fnct)) {
			jResp = getVPCProgress(request);
		} else if("updateVPCProgress".equals(fnct)) {
			jResp = updateVPCProgress(request);
		} else if("updateBMCProgress".equals(fnct)) {
			jResp = updateBMCProgress(request);
		}
		
		response.setContentType("application/json;charset=\"utf-8\"");
		try {
			PrintWriter out = response.getWriter();
			out.println(jResp.toString());
		} catch(IOException ex) {}

	
		web.close();
	}
	
	public JSONObject getLastCall(String eventId) {
		Date lastCall = eventCall.get(eventId);
		if(lastCall == null) return null;
		
		Date currDate = new Date();
		long diff = currDate.getTime() - lastCall.getTime();
		if(diff > 5000) return null;
		
		return eventData.get(eventId);
	}
	
	public void setLastCall(String eventId, JSONObject jData) {
		Date currDate = new Date();
		eventCall.put(eventId, currDate);
		eventData.put(eventId, jData);
	}
	
	public JSONObject addBmcNote(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		mData.put("bmc_id", bmcId);
		mData.put("canvas_area_id", request.getParameter("note_segment"));
		mData.put("area_label", request.getParameter("note_label"));
		mData.put("area_value", request.getParameter("note_content"));
		mData.put("area_details", request.getParameter("note_additional_details"));
		
		String inSql = "INSERT INTO bmc_areas (org_id, bmc_id, canvas_area_id, area_label, area_value, area_details) VALUES (?,?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getBmcNotes(request);
	}
	
	public JSONObject editBmcNote(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("canvas_area_id", request.getParameter("note_segment"));
		mData.put("area_label", request.getParameter("note_label"));
		mData.put("area_value", request.getParameter("note_content"));
		mData.put("area_details", request.getParameter("note_additional_details"));
		String noteId = request.getParameter("note_id");
		
		String updSql = "UPDATE bmc_areas SET canvas_area_id = ?, area_label = ?, area_value = ?, area_details = ? "
			+ "WHERE bmc_area_id = " + noteId;
		db.saveRec(updSql, mData);
	
		return getBmcNotes(request);
	}

	public JSONObject moveBmcNote(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("canvas_area_id", request.getParameter("note_segment"));
		String noteId = request.getParameter("note_id");
		
		String updSql = "UPDATE bmc_areas SET canvas_area_id = ? WHERE bmc_area_id = " + noteId;
		db.saveRec(updSql, mData);
	
		return getBmcNotes(request);
	}
	
	public JSONObject delBmcNote(HttpServletRequest request) {		
		String noteId = request.getParameter("note_id");
		String delSql = "DELETE FROM bmc_areas WHERE bmc_area_id = " + noteId;
		db.executeQuery(delSql);
	
		return getBmcNotes(request);
	}
	
	public JSONObject getBmcNote(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String noteId = request.getParameter("note_id");
		String mySql = "SELECT bmc_area_id, canvas_area_id, area_label, area_value, area_details FROM bmc_areas "
			+ "WHERE bmc_area_id = " + noteId;
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("note_id", rs.getString("bmc_area_id"));
			jData.put("note_segment", rs.getString("canvas_area_id"));
			jData.put("note_content", rs.getString("area_value"));
			jData.put("note_label", rs.getString("area_label"));
			jData.put("note_additional_details", rs.getString("area_details"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("note", aData);
		
		//System.out.println(jResp.toString());

		return jResp;
	}
	
	public JSONObject getBmcNotes(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT bmc_area_id, canvas_area_id, area_label, area_value, area_details FROM bmc_areas "
			+ "WHERE (org_id = " + orgId + ") AND (bmc_id = " + bmcId + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("note_id", rs.getString("bmc_area_id"));
			jData.put("note_segment", rs.getString("canvas_area_id"));
			jData.put("note_content", rs.getString("area_value"));
			jData.put("note_label", rs.getString("area_label"));
			jData.put("note_additional_details", rs.getString("area_details"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("notes", aData);
		
		//System.out.println(jResp.toString());

		return jResp;
	}	
	
	public JSONObject addVpcNote(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		mData.put("bmc_id", bmcId);
		mData.put("canvas_area_id", request.getParameter("note_segment"));
		mData.put("area_label", request.getParameter("note_label"));
		mData.put("area_value", request.getParameter("note_content"));
		mData.put("area_details", request.getParameter("note_additional_details"));
		
		String inSql = "INSERT INTO vpc_areas (org_id, bmc_id, canvas_area_id, area_label, area_value, area_details) VALUES (?,?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getVpcNotes(request);
	}
	
	public JSONObject editVpcNote(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("canvas_area_id", request.getParameter("note_segment"));
		mData.put("area_label", request.getParameter("note_label"));
		mData.put("area_value", request.getParameter("note_content"));
		mData.put("area_details", request.getParameter("note_additional_details"));
		String noteId = request.getParameter("note_id");
		
		String updSql = "UPDATE vpc_areas SET canvas_area_id = ?, area_label = ?, area_value = ?, area_details = ? "
			+ "WHERE vpc_area_id = " + noteId;
		db.saveRec(updSql, mData);
	
		return getVpcNotes(request);
	}

	public JSONObject moveVpcNote(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("canvas_area_id", request.getParameter("note_segment"));
		String noteId = request.getParameter("note_id");

		String updSql = "UPDATE vpc_areas SET canvas_area_id = ? WHERE vpc_area_id = " + noteId;
		db.saveRec(updSql, mData);
	
		return getVpcNotes(request);
	}
	
	public JSONObject delVpcNote(HttpServletRequest request) {		
		String noteId = request.getParameter("note_id");
		String delSql = "DELETE FROM vpc_areas WHERE vpc_area_id = " + noteId;
		db.executeQuery(delSql);
	
		return getVpcNotes(request);
	}
	
	public JSONObject getVpcNote(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String noteId = request.getParameter("note_id");
		String mySql = "SELECT vpc_area_id, canvas_area_id, area_label, area_value, area_details FROM vpc_areas "
			+ "WHERE vpc_area_id = " + noteId;
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("note_id", rs.getString("vpc_area_id"));
			jData.put("note_segment", rs.getString("canvas_area_id"));
			jData.put("note_content", rs.getString("area_value"));
			jData.put("note_label", rs.getString("area_label"));
			jData.put("note_additional_details", rs.getString("area_details"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("note", aData);
		
		//System.out.println(jResp.toString());

		return jResp;
	}
	
	public JSONObject getVpcNotes(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT vpc_area_id, canvas_area_id, area_label, area_value, area_details FROM vpc_areas "
			+ "WHERE (org_id = " + orgId + ") AND (bmc_id = " + bmcId + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("note_id", rs.getString("vpc_area_id"));
			jData.put("note_segment", rs.getString("canvas_area_id"));
			jData.put("note_content", rs.getString("area_value"));
			jData.put("note_label", rs.getString("area_label"));
			jData.put("note_additional_details", rs.getString("area_details"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("notes", aData);
		
		//System.out.println(jResp.toString());

		return jResp;
	}
	
	public JSONObject addScrumNote(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		if(!"-1".equals(bmcId)) mData.put("bmc_id", bmcId);
		if(!"-1".equals(scrumboardId)) mData.put("scrum_board_id", scrumboardId);
		mData.put("canvas_area_id", request.getParameter("note_segment"));
		mData.put("area_label", request.getParameter("note_label"));
		mData.put("area_value", request.getParameter("note_content"));
		mData.put("area_details", request.getParameter("note_additional_details"));
		
		if(!"-1".equals(bmcId)) {
			String inSql = "INSERT INTO scrum_areas (org_id, bmc_id, canvas_area_id, area_label, area_value, area_details) "
				+ "VALUES (?,?,?,?,?,?)";
			String keyFieldId = db.saveRec(inSql, mData);
		}
		if(!"-1".equals(scrumboardId)) {
			String inSql = "INSERT INTO scrum_areas (org_id, scrum_board_id, canvas_area_id, area_label, area_value, area_details) "
				+ "VALUES (?,?,?,?,?,?)";
			String keyFieldId = db.saveRec(inSql, mData);
		}
	
		return getScrumNotes(request, "false");
	}
	
	public JSONObject editScrumNote(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("canvas_area_id", request.getParameter("note_segment"));
		mData.put("area_label", request.getParameter("note_label"));
		mData.put("area_value", request.getParameter("note_content"));
		mData.put("area_details", request.getParameter("note_additional_details"));
		String noteId = request.getParameter("note_id");
		
		String updSql = "UPDATE scrum_areas SET canvas_area_id = ?, area_label = ?, area_value = ?, area_details = ? "
			+ "WHERE scrum_area_id = " + noteId;
		db.saveRec(updSql, mData);
	
		return getScrumNotes(request, "false");
	}

	public JSONObject moveScrumNote(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("canvas_area_id", request.getParameter("note_segment"));
		String noteId = request.getParameter("note_id");
		
		String updSql = "UPDATE scrum_areas SET canvas_area_id = ? WHERE scrum_area_id = " + noteId;
		db.saveRec(updSql, mData);
	
		return getScrumNotes(request, "false");
	}
	
	public JSONObject delScrumNote(HttpServletRequest request) {		
		String noteId = request.getParameter("note_id");
		String delSql = "DELETE FROM scrum_areas WHERE scrum_area_id = " + noteId;
		db.executeQuery(delSql);
	
		return getScrumNotes(request, "false");
	}

	public JSONObject archiveScrumNote(HttpServletRequest request) {		
		String noteId = request.getParameter("note_id");
		String delSql = "UPDATE scrum_areas SET is_archived = true WHERE scrum_area_id = " + noteId;
		db.executeQuery(delSql);
	
		return getScrumNotes(request, "false");
	}
	
	public JSONObject getScrumNote(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String noteId = request.getParameter("note_id");
		String mySql = "SELECT scrum_area_id, canvas_area_id, area_label, area_value, area_details FROM scrum_areas "
			+ "WHERE scrum_area_id = " + noteId;
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("note_id", noteId);
			jData.put("note_segment", rs.getString("canvas_area_id"));
			jData.put("note_content", rs.getString("area_value"));
			jData.put("note_label", rs.getString("area_label"));
			jData.put("note_additional_details", rs.getString("area_details"));

			String assignedSql = "SELECT todo_id FROM todos WHERE (scrum_area_id = " + noteId + ") AND (entity_id = " + userID + ") AND (is_active = true)";
			if(db.executeFunction(assignedSql) != null) jData.put("assigned", true);

			String mySource = " vw_todos WHERE (scrum_area_id = " + noteId + ") AND (is_active = true)";
			JSONArray jaTasks = db.jsonTable("entity_name", mySource);
			if(jaTasks.length() > 0) jData.put("assign_list", jaTasks);

			aData.put(jData);
		}
		rs.close();
		jResp.put("note", aData);
		
		//System.out.println(jResp.toString());

		return jResp;
	}
	
	public JSONObject getScrumNotes(HttpServletRequest request, String archives) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT scrum_area_id, canvas_area_id, area_label, area_value, area_details "
			+ "FROM scrum_areas ";
		if(!"-1".equals(bmcId)) {
			mySql += "WHERE (org_id = " + orgId + ") AND (bmc_id = " + bmcId + ") AND (is_archived = " 
				+ archives + ")";
		}
		if(!"-1".equals(scrumboardId)) {
			mySql += "WHERE (org_id = " + orgId + ") AND (scrum_board_id = " + scrumboardId 
				+ ") AND (is_archived = " + archives + ")";
		}
		
		if(archives.toLowerCase().equals("true")) mySql += " ORDER BY scrum_area_id DESC LIMIT 20";
			
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			String noteId = rs.getString("scrum_area_id");
			jData.put("note_id", noteId);
			jData.put("note_segment", rs.getString("canvas_area_id"));
			jData.put("note_content", rs.getString("area_value"));
			jData.put("note_label", rs.getString("area_label"));
			jData.put("note_additional_details", rs.getString("area_details"));

			String assignedSql = "SELECT todo_id FROM todos WHERE (scrum_area_id = " + noteId + ") AND (entity_id = " + userID + ") AND (is_active = true)";
			if(db.executeFunction(assignedSql) != null) jData.put("assigned", true);

			String mySource = " vw_todos WHERE (scrum_area_id = " + noteId + ") AND (is_active = true)";
			JSONArray jaTasks = db.jsonTable("entity_name", mySource);
			if(jaTasks.length() > 0) jData.put("assign_list", jaTasks);

			aData.put(jData);
		}
		rs.close();
		jResp.put("notes", aData);
		
		//System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject assignScrumNote(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();

		String fnct = request.getParameter("fnct");
		String noteId = request.getParameter("note_id");

		String mySource = "todos WHERE (scrum_area_id = " + noteId + ") AND (entity_id = " + userID + ")";
		JSONArray jaTasks = db.jsonTable("todo_id, is_active", mySource);

		if("assignScrumNote".equals(fnct)) {
			if(jaTasks.length() == 0) {
				String inSql = "INSERT INTO todos (scrum_area_id, org_id, entity_id) VALUES ("
					+ noteId + "," + orgId + "," + userID + ")";
				db.executeQuery(inSql);
			} else {
				String scrumTaskId = jaTasks.getJSONObject(0).getString("todo_id");
				String updSql = "UPDATE todos SET is_active = true, priority = 1 WHERE todo_id = " + scrumTaskId;
				db.executeUpdate(updSql);
			}
			jResp.put("assigned", true);
		} else if("unassignScrumNote".equals(fnct)) {
			if(jaTasks.length() > 0) {
				String scrumTaskId = jaTasks.getJSONObject(0).getString("todo_id");
				String updSql = "UPDATE todos SET is_active = false, priority = 0 WHERE todo_id = " + scrumTaskId;
				db.executeUpdate(updSql);
			}
			jResp.put("assigned", false);
		}
	
		//System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject getScrumBoards(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT scrum_board_id, scrum_board_name, messagecount FROM vw_staff_boards "
			+ "WHERE (is_active = true) AND (board_active = true) AND (entity_id = " + userID + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("board_id", rs.getString("scrum_board_id"));
			jData.put("board_name", rs.getString("scrum_board_name"));
			jData.put("messagecount", rs.getString("messagecount"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("boards", aData);
		
		//System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject getScrumBoard(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String scrumId = request.getParameter("scrum_board_id");
		if(scrumId == null) scrumId = "0";
		if(scrumId.trim().length() == 0)  scrumId = "0";
		String mySql = "SELECT scrum_board_id, scrum_board_name, standup_time, details, " +
			"(SELECT COUNT (entity_id) FROM vw_scrum_staff WHERE scrum_board_id = " + scrumId + ") AS member_count " +
			"FROM scrum_boards WHERE (scrum_board_id = " + scrumId + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("scrum_board_id", rs.getString("scrum_board_id"));
			jData.put("scrum_board_name", rs.getString("scrum_board_name"));
			jData.put("standup_time", rs.getString("standup_time"));
			jData.put("member_count", rs.getString("member_count"));
			jData.put("details", rs.getString("details"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("details", aData);
		
		//System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject getStandupMessages(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String scrumId = request.getParameter("scrum_board_id");
		String lastCreated = request.getParameter("last_timestamp");
		if(scrumId == null) scrumId = "0";
		if(scrumId.trim().length() == 0)  scrumId = "0";
		String mySql = "SELECT message_id, message_type, entity_name, created, scrum_chat, done_yesterday, todo_today, impediments " +
		"FROM vw_scrum_messages WHERE (scrum_board_id = '" + scrumId + "') AND ((current_date - created::date) < 30) " +
		"ORDER BY created ASC LIMIT 1000";

		if (lastCreated != null && !lastCreated.isEmpty() ) {
			mySql = "SELECT message_id, message_type, entity_name, created, scrum_chat, done_yesterday, todo_today, impediments " +
			"FROM vw_scrum_messages WHERE (scrum_board_id = '" + scrumId + "') AND (created > '" + lastCreated + "') ORDER BY created ASC";
		}

		if(!"0".equals(scrumId)) {
			BQuery rs = new BQuery(db, mySql);
	
			JSONArray aData = new JSONArray();
			while(rs.moveNext()) {
				JSONObject jData =  new JSONObject();
				jData.put("message_id", rs.getString("message_id"));
				jData.put("message_type", rs.getString("message_type"));
				jData.put("user", rs.getString("entity_name"));
				jData.put("created", rs.getString("created"));
				jData.put("scrum_chat", rs.getString("scrum_chat"));
				jData.put("yesterday", rs.getString("done_yesterday"));
				jData.put("todo", rs.getString("todo_today"));
				jData.put("impediments", rs.getString("impediments"));
				aData.put(jData);
			}
			rs.close();
			jResp.put("messages", aData);
		}
		
		//System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject getLastMessage(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String scrumId = request.getParameter("scrum_board_id");
		if(scrumId == null) scrumId = "0";
		if(scrumId.trim().length() == 0)  scrumId = "0";
		String mySql = "SELECT standup_id, entity_name, created, done_yesterday, todo_today, impediments " +
		"FROM vw_standup WHERE (scrum_board_id = " + scrumId + ") AND (entity_id = " + userID + ") ORDER BY created DESC LIMIT 1";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("standup_id", rs.getString("standup_id"));
			jData.put("user", rs.getString("entity_name"));
			jData.put("created", rs.getString("created"));
			jData.put("yesterday", rs.getString("done_yesterday"));
			jData.put("todo", rs.getString("todo_today"));
			jData.put("impediments", rs.getString("impediments"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("message", aData);
		
		//System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject addStandupMessage(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("entity_id", userID);
		mData.put("org_id", orgId);
		mData.put("scrum_board_id", request.getParameter("scrum_board_id"));
		mData.put("done_yesterday", request.getParameter("yesterday"));
		mData.put("todo_today", request.getParameter("today"));
		mData.put("impediments", request.getParameter("impediments"));
		
		String inSql = "INSERT INTO standup (entity_id, org_id, scrum_board_id, done_yesterday, todo_today, impediments) VALUES (?,?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getStandupMessages(request);
	}

	public JSONObject addChatMessage(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("entity_id", userID);
		mData.put("org_id", orgId);
		mData.put("scrum_board_id", request.getParameter("scrum_board_id"));
		mData.put("scrum_chat", request.getParameter("message"));
		
		String inSql = "INSERT INTO scrum_chats (entity_id, org_id, scrum_board_id, scrum_chat) VALUES (?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getStandupMessages(request);
	}

	public JSONObject getBMCProgress(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT bmc_id, bmc_wizard FROM bmc WHERE (bmc_id = " + bmcId + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("wizard_data", rs.getString("bmc_wizard"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("bmc_data", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject getVPCProgress(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT bmc_id, vpc_wizard FROM bmc WHERE (bmc_id = " + bmcId + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("wizard_data", rs.getString("vpc_wizard"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("vpc_data", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject updateBMCProgress(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("bmc_wizard", request.getParameter("data"));
		
		String updSql = "UPDATE bmc SET bmc_wizard = ? WHERE (bmc_id = " + bmcId + ")";
		db.saveRec(updSql, mData);
	
		return getBMCProgress(request);
	}

	public JSONObject updateVPCProgress(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("vpc_wizard", request.getParameter("data"));
		
		String updSql = "UPDATE bmc SET vpc_wizard = ? WHERE (bmc_id = " + bmcId + ")";
		db.saveRec(updSql, mData);
	
		return getVPCProgress(request);
	}
}


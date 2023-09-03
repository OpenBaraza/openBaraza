/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
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
import java.util.Base64;
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

import org.apache.commons.lang3.RandomStringUtils;

import org.baraza.utils.BWebUtils;
import org.baraza.utils.BTextFormat;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.DB.BUser;
import org.baraza.DB.BTranslations;
import org.baraza.xml.BXML;
import org.baraza.xml.BElement;
import org.baraza.reports.BWebReport;

public class BDataOps {
	Logger log = Logger.getLogger(BDataOps.class.getName());

	String tokenKey = "baraza$";
	BElement root = null;
	BElement menuXml = null;
	BDB db = null;
	Map<String, BUser> users;
	
	public BDataOps(BElement root, BDB db) {
		users = new HashMap<String, BUser>();
		this.db = db;
		this.root = root;
		menuXml = root.getFirst();
		
		tokenKey += RandomStringUtils.randomAlphanumeric(8);
	}
	
	public JSONObject authenticate(HttpServletRequest request)  {
		JSONObject jResp = new JSONObject();

		String authUser = request.getHeader("authuser");
		String authPass = request.getHeader("authpass");
		if(authUser == null || authPass == null) {
			jResp.put("ResultCode", 1);
			jResp.put("ResultDesc", "Wrong username or password");
			return jResp;
		}

		authUser = new String(Base64.getDecoder().decode(authUser));
		authPass = new String(Base64.getDecoder().decode(authPass));
		
		authUser = authUser.replaceAll("\n", "").trim();
		
		String authFunction = root.getAttribute("authentication");
		
		String userId = "-1";
		if(authFunction == null) {
			try {
				request.login(authUser, authPass);
				BUser user = new BUser(db, request.getRemoteAddr(), authUser);
				userId = user.getUserID();
			} catch(ServletException ex) {
				System.out.println("Authentication Exception : " + ex);
			}
		} else {
			String pswdSql = "SELECT " + authFunction + "('" + authUser + "','" + authPass + "','" 
				+ request.getRemoteAddr() + "','')";
			userId = db.executeFunction(pswdSql);
			if(userId == null) userId = "-1";
		}
//System.out.println("BASE 2010 : " + authUser + " : " + authPass + " : " + userId);

		if(userId.equals("-1")) {
			jResp.put("ResultCode", 1);
			jResp.put("ResultDesc", "Wrong username or password");
		} else {
			users.put(userId, new BUser(db, request.getRemoteAddr(), authUser, userId));

			String token = BWebUtils.createToken(tokenKey, userId, users.get(userId).getUserOrgId());
//System.out.println("BASE 3010 : " + token);
			
			jResp.put("ResultCode", 0);
			jResp.put("access_token", token);
			jResp.put("expires_in", "15");
		}
		
		return jResp;
	}
	
	public JSONObject reAuthenticate(String token) {
		JSONObject jResp = new JSONObject();
		String userId = BWebUtils.decodeToken(tokenKey, token);
System.out.println("BASE 3030 : " + userId);
			
		if(userId == null) {
			jResp.put("ResultCode", 1);
			jResp.put("access_error", "Wrong token");
		} else {
			jResp.put("ResultCode", 0);
			jResp.put("userId", userId);
		}
		
		return jResp;
	}
	
	public JSONObject unsecuredData(HttpServletRequest request) {
		JSONObject jResp = new JSONObject();
		String body = BWebUtils.requestBody(request);
		if(body == null) body = "{}";
		JSONObject jParams = new JSONObject(body);
		
		String viewKey = request.getParameter("view");
		BElement view = getView(viewKey);
		
		if(!view.getName().equals("FORM")) {
			jResp.put("ResultCode", 2);
			jResp.put("ResultDesc", "Wrong object");
			return jResp;
		}
		
		String linkData = request.getParameter("linkdata");
		String keyData = request.getParameter("keydata");
System.out.println("BASE 2010 : linkData " + linkData);
System.out.println("BASE 2020 : keyData " + keyData);
System.out.println("BASE 2020 : " + jParams.toString());
		
		if(view.getAttribute("secured", "true").equals("false")) {
			jResp = postData(view, request.getRemoteAddr(), jParams, null, keyData, linkData);
		} else {
			jResp.put("ResultCode", 1);
			jResp.put("ResultDesc", "Security issue");
		}
			
		return jResp;
	}
	
	public JSONObject getUForm(HttpServletRequest request, BTranslations translations) {
		System.out.println("BASE 5010 : " + "Start form");
		JSONObject jResp = new JSONObject();
		String viewKey = request.getParameter("view");
System.out.println("BASE 3010 : " + viewKey);
		BElement view = getView(viewKey);
		
		if(!view.getName().equals("FORM")) {
			jResp.put("ResultCode", 2);
			jResp.put("ResultDesc", "Wrong object");
			return jResp;
		}
		
		if(view.getAttribute("secured", "true").equals("false")) {
			jResp = getForm(request, null, translations);
		} else {
			jResp.put("ResultCode", 1);
			jResp.put("ResultDesc", "Security issue");
		}
			
		return jResp;
	}
	
	public JSONObject getMenu(HttpServletRequest request, String userId) {
		JSONObject jResp = new JSONObject();
		
		BUser user = null;
		if(userId == null) return jResp;
		else user = users.get(userId);
		
		jResp = getSubMenu(user, menuXml, 0);
		
		return jResp;
	}
	
	public JSONObject getSubMenu(BUser user, BElement mel, int level) {
		JSONObject jResp = new JSONObject();
		
		JSONArray jTable = new JSONArray();
		for(BElement el : mel.getElements()) {
			boolean hasAccess = user.checkAccess(el.getAttribute("role"), el.getAttribute("access"));
			if(hasAccess) {
				if(el.isLeaf()) {
					Integer mKey = Integer.valueOf(el.getValue());
					JSONObject jField = new JSONObject();
					jField.put("key", mKey);
					jField.put("name", el.getAttribute("name"));
					if(el.getAttribute("dashboard") != null) jField.put("dashboard", el.getAttribute("dashboard"));
					jTable.put(jField);
				} else {
					jTable.put(getSubMenu(user, el, level+1));
				}
			}
		}
		
		if(level == 0) {
			jResp.put("menu", jTable);
		} else {
			jResp.put("submenu", jTable);
			jResp.put("name", mel.getAttribute("name"));
		}
		
		return jResp;
	}
	
	public JSONObject getDashboard(HttpServletRequest request, String userId) {
		JSONObject jResp = new JSONObject();
		
		BUser user = null;
		if(userId == null) return jResp;
		user = users.get(userId);
		BElement view = root.getElementByKey("1");
		if(view == null) return jResp;
		BElement eDashboard = view.getElementByName("DASHBOARD");
		if(eDashboard == null) return jResp;
		
		// Links
		BElement eLinks = eDashboard.getElementByName("LINKS");
		if(eLinks != null) {
			JSONArray jLinks = new JSONArray();
			for(BElement el : eLinks.getElements()) {
				boolean hasAccess = user.checkAccess(el.getAttribute("role"), el.getAttribute("access"));
				if(hasAccess) {
					JSONObject jLink = new JSONObject();
					jLink.put("view", el.getAttribute("view"));
					jLink.put("icon", el.getAttribute("icon"));
					jLink.put("title", el.getAttribute("title"));
					
					jLinks.put(jLink);
				}
			}
			jResp.put("links", jLinks);
		}
		
		// Attandance
		BElement eAttendance = eDashboard.getElementByName("ATTENDANCE");
		if(eAttendance != null) {
			boolean hasAccess = user.checkAccess(eAttendance.getAttribute("role"), eAttendance.getAttribute("access"));
			if(hasAccess) {
				BElement eAccessLogs = eAttendance.getElementByName("ACCESSLOG");
				String lWhere = "(log_time_out is null)";
				
				BQuery alRs = new BQuery(db, eAccessLogs, lWhere, null, user, false);
				if(alRs.moveFirst()) jResp.put("accesslog", alRs.getJSON());
				jResp.put("attendance", true);
			}
		}
		
		return jResp;
	}

	public JSONObject getView(HttpServletRequest request, String userId) {
		JSONObject jResp = new JSONObject();
		
		BUser user = null;
		if(userId != null) user = users.get(userId);
		String viewKey = request.getParameter("view");
		BElement view = getView(viewKey);
		
		jResp.put("type", view.getName());
		jResp.put("typeId", BWebUtils.getViewType(view.getName()));
		jResp.put("name", view.getAttribute("name"));
		
		if(view.getAttribute("jump.empty") != null) jResp.put("jump.empty", view.getAttribute("jump.empty"));
		if(view.getAttribute("jump.view") != null) jResp.put("jump.view", view.getAttribute("jump.view"));
		
		int viewCount = 0;
		JSONArray jFields = new JSONArray();
		JSONArray jViews = new JSONArray();
		for(BElement el : view.getElements()) {
			int viewType = BWebUtils.getViewType(el.getName());
			int fieldType = BWebUtils.getFieldType(el.getName());
			if(viewType > 0) {
				JSONObject jView = new JSONObject();
				jView.put("viewPos", viewCount);
				jView.put("typeId", viewType);
				jView.put("type", el.getName());
				jView.put("name", el.getAttribute("name"));
				jViews.put(jView);
			} else if(fieldType > 0) {
				JSONObject jField = new JSONObject();
				jField.put("field_type", fieldType);
				jField.put("name", el.getValue());
				if(el.getAttribute("title") != null) jField.put("title", el.getAttribute("title"));
				if(el.getAttribute("fnct") != null) jField.put("fnct", el.getAttribute("fnct"));
				if(el.getAttribute("raw") != null) jField.put("raw", true);
				jFields.put(jField);
			} else if(el.getName().equals("ACTIONS")) {
				jResp.put("actions", true);
			}
		}
		jResp.put("fields", jFields);
		jResp.put("views", jViews);
		
		return jResp;
	}

	public JSONObject getForm(HttpServletRequest request, String userId, BTranslations translations) {
		JSONObject jResp = new JSONObject();
		JSONArray jTable = new JSONArray();
		
		String linkData = request.getParameter("linkdata");
		String viewKey = request.getParameter("view");
		BElement view = getView(viewKey);
		BUser user = null;
		Integer languageId = Integer.valueOf(0);
		Integer orgId = Integer.valueOf(0);
		if(userId != null) {
			user = users.get(userId);
			orgId = user.getUserOrgId();
			languageId = user.getLanguageId();
		}
		
		if(!view.getName().equals("FORM")) return jResp;
				
		for(BElement el : view.getElements()) {
			if(BWebUtils.canDisplayField(el.getName())) {
				int fieldType = BWebUtils.getFieldType(el.getName());
				JSONObject jField = new JSONObject();
				jField.put("type", fieldType);
				jField.put("name", el.getValue());
				if(el.getAttribute("tab") != null) jField.put("tab", el.getAttribute("tab"));
				if(el.getAttribute("required", "false").equals("true")) jField.put("required", true);
				if(user == null) {
					if(el.getAttribute("default") != null) jField.put("default", el.getAttribute("default"));
				} else {
					String defaultValue = db.getDefaultValue(el, user);
					if(!defaultValue.equals("")) {
						if(el.getAttribute("format", "").equals("nohtml")) defaultValue = BTextFormat.htmlToText(defaultValue);
						jField.put("default", defaultValue);
					}
				}
				if(el.getAttribute("title") != null) {
					String fieldTitle = el.getAttribute("title");
					if(el.getAttribute("lang") != null) {
						fieldTitle = translations.getTitle(orgId, languageId, el.getAttribute("lang"), fieldTitle);
					} else if(languageId > 0) { //if the language is not english
						fieldTitle = translations.getTitle(orgId, languageId, el.getValue(), fieldTitle);
					}
					jField.put("title", fieldTitle);
				}
				
				if(el.getName().equals("COMBOBOX")) {
					String comboboxSQL = BWebUtils.comboboxSQL(el, user, db.getOrgID(), linkData);
					
					String listId = el.getAttribute("lpkey", el.getValue());
					
					jField.put("list_id", listId);
					jField.put("list_value", el.getAttribute("lpfield"));
					
					for(String attributeName : el.getAttributeNames()) {
						if(attributeName.startsWith("select.")) jField.put(attributeName, el.getAttribute(attributeName));
					}

					BQuery cmbrs = new BQuery(db, comboboxSQL);
					jField.put("list", cmbrs.getJSON());
				
					cmbrs.close();
				} else if(el.getName().equals("COMBOLIST")) {
					JSONArray jComboList = new JSONArray();
					for(BElement ell : el.getElements()) {
						String mykey = ell.getAttribute("key", ell.getValue());
					
						JSONObject jItem = new JSONObject();
						jItem.put("id", mykey);
						jItem.put("value", ell.getValue());
						jComboList.put(jItem);
					}
					jField.put("list_id", "id");
					jField.put("list_value", "value");
					jField.put("list", jComboList);
				}
				jTable.put(jField);
			}
		}
		jResp.put("form", jTable);

		if(view.getAttribute("location", "false").equals("true")) {
			JSONObject jField = new JSONObject();
			jField.put("longitude", "longitude");
			jField.put("latitude", "latitude");
			jResp.put("location", jField);
		}
		
		if(view.getAttribute("phone", "false").equals("true")) {
			JSONObject jField = new JSONObject();
			jField.put("model", "phone_model");
			jField.put("make", "phone_make");
			jField.put("phone_serial", "phone_serial_number");
			jField.put("sim_serial", "sim_serial_number");
			jResp.put("phone", jField);
		}
		
		// Get data to form for edit
		String keyField = view.getAttribute("keyfield");
		String keyData = request.getParameter("keydata");
System.out.println("BASE 3040 : " + keyData);

		if((keyData != null) && (keyField != null)) {
			String whereSql = keyField + "='" + keyData + "'"; 
			BQuery rs = new BQuery(db, view, whereSql, null, user, false);
			if(rs.moveNext()) {
				jResp.put("data", rs.getJSON());
			}
			rs.close();
		}
		
		return jResp;
	}

	public JSONObject securedData(HttpServletRequest request, String userId) {
		JSONObject jResp = new JSONObject();
		String body = BWebUtils.requestBody(request);
		if(body == null) body = "{}";
		
		String viewKey = request.getParameter("view");
		String keyData = request.getParameter("keydata");
		String linkData = request.getParameter("linkdata");
		BElement view = getView(viewKey);
		BUser user = users.get(userId);

System.out.println("BASE 2010 : linkData " + linkData);
System.out.println("BASE 2020 : keyData " + keyData);
System.out.println("BASE 2020 : body " + body);

		if(!view.getName().equals("FORM")) return jResp;
		if(user == null) return jResp;
		
		JSONObject jParams = new JSONObject(body);
		jResp = postData(view, request.getRemoteAddr(), jParams, user, keyData, linkData);
			
		return jResp;
	}

	public JSONObject getGridDef(HttpServletRequest request, String userId, BTranslations translations) {
		JSONObject jResp = new JSONObject();
		
		String viewKey = request.getParameter("view");
		String linkData = request.getParameter("linkdata");
		BElement view = getView(viewKey);
		BUser user = null;
		Integer languageId = Integer.valueOf(0);
		Integer orgId = Integer.valueOf(0);
		if(userId != null) {
			user = users.get(userId);
			orgId = user.getUserOrgId();
			languageId = user.getLanguageId();
		}
		
		if(view.getName().equals("FORM")) return jResp;

		// Add the titles for columns
		JSONArray jGrid = new JSONArray();
		JSONArray jViews = new JSONArray();
		for(BElement el : view.getElements()) {
			int viewType = BWebUtils.getViewType(el.getName());
			int fieldType = BWebUtils.getFieldType(el.getName());
			if(viewType == 0) {
				if(el.getAttribute("title") != null) {
					String fieldTitle = el.getAttribute("title");
					if(el.getAttribute("lang") != null) {
						fieldTitle = translations.getTitle(orgId, languageId, el.getAttribute("lang"), fieldTitle);
					} else if(languageId > 0) { //if the language is not english
						fieldTitle = translations.getTitle(orgId, languageId, el.getValue(), fieldTitle);
					}
					
					JSONObject jField = new JSONObject();
					jField.put("type", fieldType);
					jField.put("name", el.getValue());
					jField.put("title", fieldTitle);
					if(el.getAttribute("default") != null) jField.put("default", el.getAttribute("default"));
					if(el.getAttribute("total") != null) jField.put("total", fieldTitle);
					if(el.getAttribute("raw") != null) jField.put("raw", true);
					
					if(fieldType == 11) {			// COMBOBOX
						String comboboxSQL = BWebUtils.comboboxSQL(el, user, db.getOrgID(), linkData);
						String listId = el.getAttribute("lpkey", el.getValue());
						
						jField.put("list_id", listId);
						jField.put("list_value", el.getAttribute("lpfield"));

						BQuery cmbrs = new BQuery(db, comboboxSQL);
						jField.put("list", cmbrs.getJSON());
						cmbrs.close();
					} else if(fieldType == 10) {	// COMBOLIST
						JSONArray jComboList = new JSONArray();
						for(BElement ell : el.getElements()) {
							String mykey = ell.getAttribute("key", ell.getValue());
						
							JSONObject jItem = new JSONObject();
							jItem.put("id", mykey);
							jItem.put("value", ell.getValue());
							jComboList.put(jItem);
						}
						jField.put("list_id", "id");
						jField.put("list_value", "value");
						jField.put("list", jComboList);
					}
					
					jGrid.put(jField);
				}
			} else {
				JSONObject jView = new JSONObject();
				jView.put("type", el.getName());
				jView.put("typeId", viewType);
				jView.put("name", el.getAttribute("name"));
				
				if(viewType == 7) {		// FORM type to check for new and edit allow
					if(el.getAttribute("new", "true").equals("true")) jView.put("new", true);
					if(el.getAttribute("edit", "true").equals("true")) jView.put("edit", true);
				}
				
				jViews.put(jView);
			}
		}
		jResp.put("grid", jGrid);
		jResp.put("views", jViews);

		// Add action list
		JSONArray opList = getActions(view, user);
		if(opList.length() > 0) jResp.put("actions", opList);
		
		return jResp;
	}

	public JSONObject unsecuredReadData(HttpServletRequest request) {
		JSONObject jResp = new JSONObject();

		String linkData = request.getParameter("linkdata");
		String keyData = request.getParameter("keydata");
		String viewKey = request.getParameter("view");
		BElement view = getView(viewKey);
		
		if(view.getName().equals("FORM")) return jResp;

		String whereSql = request.getParameter("where");
		if(BWebUtils.checkInjection(whereSql)) whereSql = null;
System.out.println("BASE 3020 WHERE : " + whereSql);

		if(view.getAttribute("linkfield") != null) {
			if(whereSql == null) whereSql = "(" + view.getAttribute("linkfield") + " = '" + linkData + "')";
			else whereSql = " AND (" + view.getAttribute("linkfield") + " = '" + linkData + "')";
		}
		
		String keyField = view.getAttribute("keyfield");
		if((keyData != null) && (keyField != null)) {
			if(whereSql == null) whereSql = "(" + keyField + " = '" + keyData + "')";
			else whereSql = " AND (" + keyField + " = '" + keyData + "')";
		}

		if(view.getAttribute("secured", "true").equals("false")) {
			BQuery rs = new BQuery(db, view, whereSql, null, false);
			if(rs.moveNext()) {
				jResp.put("data", rs.getJSON());
			}
			rs.close();
		} else {
			jResp.put("ResultCode", 1);
			jResp.put("ResultDesc", "Security issue");
		}

		return jResp;
	}

	public JSONObject readData(HttpServletRequest request, String userId) {
		JSONObject jResp = new JSONObject();

		String linkData = request.getParameter("linkdata");
		String keyData = request.getParameter("keydata");
		String viewKey = request.getParameter("view");
		BElement view = getView(viewKey);
		BUser user = users.get(userId);

		if(view.getName().equals("FORM")) return jResp;

		String whereSql = request.getParameter("where");
		if(BWebUtils.checkInjection(whereSql)) whereSql = null;

		if(view.getAttribute("linkfield") != null) {
			if(whereSql == null) whereSql = "(" + view.getAttribute("linkfield") + " = '" + linkData + "')";
			else whereSql += " AND (" + view.getAttribute("linkfield") + " = '" + linkData + "')";
		}
System.out.println("BASE 3020 WHERE : " + whereSql);

		String keyField = view.getAttribute("keyfield");
		if((keyData != null) && (keyField != null)) {
			if(whereSql == null) whereSql = "(" + keyField + " = '" + keyData + "')";
			else whereSql += " AND (" + keyField + " = '" + keyData + "')";
		}
		
		BQuery rs = new BQuery(db, view, whereSql, null, user, false);
		if(rs.moveNext()) {
			jResp.put("data", rs.getJSON());
		}
		rs.close();

		return jResp;
	}

	public JSONObject updateGrid(HttpServletRequest request, String userId) {
		JSONObject jResp = new JSONObject();

		String linkData = request.getParameter("linkdata");
		String keyData = request.getParameter("keydata");
		String viewKey = request.getParameter("view");
		String fnct = request.getParameter("fnct");
		String body = BWebUtils.requestBody(request);
		if(body == null) body = "{}";
		BElement view = getView(viewKey);
		BUser user = users.get(userId);
		JSONObject jParams = new JSONObject(body);

System.out.println("BASE 2050 : linkData " + linkData);
System.out.println("BASE 2050 : keyData " + keyData);
System.out.println("BASE 2050 : fnct " + fnct);
System.out.println("BASE 2050 : body " + body);
System.out.println("BASE 2050 : body " + view.toString());
		
		if(!view.getName().equals("GRID")) return jResp;

		List<String> viewData = new ArrayList<String>();
		Map<String, String[]> reqParams = new HashMap<String, String[]>();
		for(String paramName : jParams.keySet()) {
			String[] pArray = new String[1];
			pArray[0] = jParams.getString(paramName);
			reqParams.put(paramName, pArray);
		}
		
		String saveMsg = null;
		if("insert".equals(fnct)) {
			BQuery rs = new BQuery(db, view, null, null, user, false);
			rs.recAdd();
			if(linkData != null && view.getAttribute("linkfield") != null) rs.updateField(view.getAttribute("linkfield"), linkData); 
			saveMsg = rs.updateFields(reqParams, viewData, request.getRemoteAddr(), linkData);
			jResp.put("data", rs.getRowJSON());
			rs.close();
		} else if("update".equals(fnct)) {
			String whereSql = view.getAttribute("keyfield") + " = '" + keyData + "'";
			BQuery rs = new BQuery(db, view, whereSql, null, user, false);
			rs.moveFirst();
			rs.recEdit();
			saveMsg = rs.updateFields(reqParams, viewData, request.getRemoteAddr(), "");
			rs.refresh();
			if(rs.moveFirst()) jResp.put("data", rs.getRowJSON());
			rs.close();
		} else if("delete".equals(fnct)) {
			String whereSql = view.getAttribute("keyfield") + " = '" + keyData + "'";
			BQuery rs = new BQuery(db, view, whereSql, null, user, false);
			rs.moveFirst();
			saveMsg = rs.recDelete();
			rs.close();
		}

		if(saveMsg == null) saveMsg = "";
		if(saveMsg.equals("")) {
			jResp.put("ResultCode", 0);
			jResp.put("ResultDesc", "Data posted");
		} else {
			jResp.put("ResultCode", 2);
			jResp.put("ResultDesc", saveMsg);
		}
		
		return jResp;
	}
	
	public JSONArray getActions(BElement view, BUser user) {
		JSONArray opList = new JSONArray();
		
		BElement opt = view.getElementByName("ACTIONS");
		if(opt != null) {
			Integer i = 0;
			for(BElement el : opt.getElements()) {
				boolean hasAccess = user.checkAccess(el.getAttribute("role"), el.getAttribute("access"));
				if(hasAccess) {
					JSONObject jOpt = new JSONObject();
					jOpt.put("aid", i.toString());
					jOpt.put("action", el.getValue());
					if(el.getAttribute("jump.view") != null) jOpt.put("jump.view", el.getAttribute("jump.view"));
					opList.put(jOpt);
				}
				i++;
			}
		}
		
		return opList;
	}
	
	public JSONObject doActions(HttpServletRequest request, String userId) {
		JSONObject jResp = new JSONObject();
		String body = BWebUtils.requestBody(request);
		if(body == null) body = "{}";
		
		String viewKey = request.getParameter("view");
		String action = request.getParameter("action");
System.out.println("BASE 3350 : " + action);
System.out.println("BASE 3360 : " + body);

		BElement view = getView(viewKey);
		BUser user = users.get(userId);
		
		if(view.getName().equals("GRID") || view.getName().equals("FORMVIEW")) {
			JSONArray jIds = new JSONArray(body);
			jResp = doActions(view, action, request.getRemoteAddr(), jIds, user, null);
		} else {
			jResp.put("ResultCode", -1);
			jResp.put("ResultMsg", "No Call");
		}
		
		return jResp;
	}
	
	public JSONObject doActions(BElement view, String action, String remoteAddr, JSONArray jIds, BUser user, String linkData) {
		Integer aPos = Integer.valueOf(action);
		BElement el = view.getElementByName("ACTIONS").getElement(aPos);
		String mySql = "";
		String sucessCode = "1";
		JSONObject jResp = new JSONObject();
		JSONArray aResp = new JSONArray();
		
		if(el != null) {
			for(int i = 0; i < jIds.length(); i++) {
				JSONObject jId = jIds.getJSONObject(i);
				String value = jId.getString("id");

				String auditSql = user.insAudit(el.getAttribute("fnct"), value, "FUNCTION");
				String autoKeyID = db.executeAutoKey(auditSql);
			
				mySql = "SELECT " + el.getAttribute("fnct") + "('" + value + "', '" + user.getUserID();
				if(el.getAttribute("approval") != null) mySql += "', '" + el.getAttribute("approval");
				if(el.getAttribute("phase") != null) mySql += "', '" + el.getAttribute("phase");
				else mySql += "', '" + linkData;
				if(el.getAttribute("auditid") != null) mySql += "', '" + autoKeyID;
				mySql += "') ";

				if(el.getAttribute("from") != null) mySql += " " + el.getAttribute("from");
				log.info(mySql);
System.out.println("BASE 5050 : " + mySql);

				JSONObject jRs = new JSONObject();
				String exans = db.executeFunction(mySql);
				if(exans == null) {
					sucessCode = "0";
					jRs.put("ResultCode", 0);
					jRs.put("id", value);
					jRs.put("ResultMsg", db.getLastErrorMsg());
				} else {
					jRs.put("ResultCode", 1);
					jRs.put("id", value);
					jRs.put("ResultMsg", exans);
				}
				aResp.put(jRs);
			}
			jResp.put("ResultCode", sucessCode);
			jResp.put("Results", aResp);
		}
		
		return jResp;
	}
	
	public String getReport(HttpServletRequest request, String userId, String reportPath) {
		JSONObject jResp = new JSONObject();
		String body = BWebUtils.requestBody(request);
		if(body == null) body = "{}";
		
		String linkData = request.getParameter("linkdata");
		String viewKey = request.getParameter("view");
		BElement view = getView(viewKey);
		BUser user = users.get(userId);
System.out.println("BASE 4040 : " + linkData);

		if(!view.getName().equals("JASPER")) return "";

		BWebReport webReport = new BWebReport(db, view, user, request);
		if(linkData != null) webReport.setParams("filterid", linkData);
		String reportHtml = "<html><head></head><body>\n";
		reportHtml += webReport.getReport(db, user, linkData, request, reportPath, false);
		reportHtml += "\n</body></html>";

		return reportHtml;
	}
	
	public void getPdfReport(HttpServletRequest request, HttpServletResponse response, String userId, String reportPath) {
		JSONObject jResp = new JSONObject();
		String body = BWebUtils.requestBody(request);
		if(body == null) body = "{}";

		String linkData = request.getParameter("linkdata");
		String viewKey = request.getParameter("view");
		BElement view = getView(viewKey);
		BUser user = users.get(userId);
System.out.println("BASE 4040 : " + linkData);

		if(!view.getName().equals("JASPER")) return;

		BWebReport webReport = new BWebReport(db, view, user, request);
		if(linkData != null) webReport.setParams("filterid", linkData);
		webReport.getAppReport(db, user, request, response, reportPath, 0);
	}
	
	public JSONObject addAttendance(HttpServletRequest request, String userId) {
		JSONObject jResp = new JSONObject();
		String myOutput = null;

		BUser user = null;
		if(userId == null) {
			jResp.put("error", 101);
			jResp.put("message", "No user available");
			return jResp;
		}
		user = users.get(userId);
		BElement view = root.getElementByKey("1");
		if(view == null) {
			jResp.put("error", 102);
			jResp.put("message", "Dashboard not available");
			return jResp;
		}
		BElement eDashboard = view.getElementByName("DASHBOARD");
		if(eDashboard == null) {
			jResp.put("error", 103);
			jResp.put("message", "Dashboard not available");
			return jResp;
		}
		BElement eAttendance = eDashboard.getElementByName("ATTENDANCE");
		if(eDashboard == null) {
			jResp.put("error", 104);
			jResp.put("message", "Dashboard attendance not available");
			return jResp;
		}
		
		String body = BWebUtils.requestBody(request);
System.out.println("BASE 2020 : addAttendance : "  + body);
		if(body != null) {
			JSONObject jObj = new JSONObject(body);

			String mySql = "SELECT add_access_logs(" + user.getUserID() + "," + jObj.getString("log_type")
				+ ",'" + jObj.getString("log_in_out") + "', '" + request.getRemoteAddr() 
				+ "', '" + jObj.getBigDecimal("lat").toString() + "," + jObj.getBigDecimal("long").toString() + "'::point);";
System.out.println("BASE 2030 : " + mySql);
		
			myOutput = db.executeFunction(mySql);
		}
			
		if(myOutput == null) {
			jResp.put("error", 101);
			jResp.put("message", "Attendace not added");
		} else {
			String lWhere = "(log_time_out is null)";
			if(!myOutput.equals("0")) lWhere = "(access_log_id = " + myOutput + ")";
						
			BQuery alRs = new BQuery(db, eAttendance.getElementByName("ACCESSLOG"), lWhere, null, user, false);
			jResp.put("accesslog", alRs.getJSON());
			alRs.close();
		}
System.out.println("BASE 3120 : " + jResp.toString());
		
		return jResp;
	}
	
	public BElement getView(String viewKey) {
System.out.println("BASE 4040 : " + viewKey);
		
		List<BElement> views = new ArrayList<BElement>();
		List<String> viewKeys = new ArrayList<String>();
		String sv[] = viewKey.split(":");
		for(String svs : sv) viewKeys.add(svs);
		views.add(root.getElementByKey(sv[0]));
//System.out.println("BASE 4050 : " + views.get(0).toString());

		for(int i = 1; i < sv.length; i++) {
			int subNo = Integer.valueOf(sv[i]);
			views.add(views.get(i-1).getSub(subNo));
		}
		BElement view = views.get(views.size() - 1);
		
//System.out.println("BASE 4070 : " + view.toString());
		
		return view;
	}
	
	public JSONObject postData(BElement view, String remoteAddr, JSONObject jParams, BUser user, String keyData, String linkData) {
		List<String> viewData = new ArrayList<String>();
		Map<String, String[]> newParams = new HashMap<String, String[]>();
		for(String paramName : jParams.keySet()) {
			String[] pArray = new String[1];
			pArray[0] = jParams.getString(paramName);
			newParams.put(paramName, pArray);
		}
		
		JSONObject jRow = new JSONObject();
		String saveMsg = null;
		if(keyData == null) {
			String fWhere = view.getAttribute("keyfield") + " = null";
			BQuery rs = new BQuery(db, view, fWhere, null, user, false);
			rs.recAdd();
			if((view.getAttribute("linkfield") != null) && (linkData != null))
				rs.updateField(view.getAttribute("linkfield"), linkData);
			saveMsg = rs.updateFields(newParams, viewData, remoteAddr, linkData);
			jRow = rs.getRowJSON();
			rs.close();
		} else {
			String fWhere = view.getAttribute("keyfield") + "='" + keyData + "'";
			BQuery rs = new BQuery(db, view, fWhere, null, user, false);
			rs.moveFirst();
			
			rs.recEdit();
			if((view.getAttribute("linkfield") != null) && (linkData != null))
				rs.updateField(view.getAttribute("linkfield"), linkData);
			saveMsg = rs.updateFields(newParams, viewData, remoteAddr, linkData);
			jRow = rs.getRowJSON();
			rs.close();
		}
		
		JSONObject jResp = new JSONObject();
		if(saveMsg.equals("")) {
			jResp.put("ResultCode", 0);
			jResp.put("ResultDesc", "Data posted");
			
			jResp.put("data", jRow);
		} else {
			jResp.put("ResultCode", 2);
			jResp.put("ResultDesc", saveMsg);
		}
		
		return jResp;
	}
	
	public JSONObject changePassword(HttpServletRequest request, String userId) {
		JSONObject jResp = new JSONObject();
		String body = BWebUtils.requestBody(request);
		if(body == null) body = "{}";
		JSONObject jParams = new JSONObject(body);
System.out.println("BASE 5050 : " + jParams.toString());

		BUser user = users.get(userId);
		
		if(user == null) {
			jResp.put("ResultCode", 401);
			jResp.put("ResultMsg", "Unauthorised user");
		} else if(jParams.has("old_password") && jParams.has("new_password")) {
			String oldPassword = jParams.getString("old_password");
			String newPassword = jParams.getString("new_password");
			
			oldPassword = new String(Base64.getDecoder().decode(oldPassword));
			newPassword = new String(Base64.getDecoder().decode(newPassword));
			
			String updSql = "SELECT change_password('" + user.getUserID() + "','"
				+ oldPassword + "','" + newPassword + "')";
			String updMsg = db.executeFunction(updSql);
System.out.println("BASE 5050 : " + updSql);

			if(updMsg.equals("Password Error")) {
				jResp.put("ResultCode", 403);
				jResp.put("ResultMsg", "Password Error");
			} else {
				jResp.put("ResultCode", 0);
				jResp.put("ResultMsg", "Password changed sucesfully");
			}
		} else {
			jResp.put("ResultCode", 402);
			jResp.put("ResultMsg", "Supply valid password");
		}
		
		return jResp;
	}

	public JSONObject emailReset(HttpServletRequest request) {
		JSONObject jResp = new JSONObject();
		String body = BWebUtils.requestBody(request);
		if(body == null) body = "{}";
		JSONObject jParams = new JSONObject(body);

		if(jParams.has("request_email") && jParams.has("validation_code")) {
			String requestEmail = jParams.getString("request_email");
			String validationCode = jParams.getString("validation_code");
						
			String updSql = "SELECT add_sys_reset('" + requestEmail + "','"
				+ validationCode + "','" + request.getRemoteAddr() + "')";
			String updMsg = db.executeFunction(updSql);
System.out.println("BASE 5050 : " + updSql);

			if(updMsg.equals("Email not found")) {
				jResp.put("ResultCode", 403);
				jResp.put("ResultMsg", "Email not found");
			} else {
				jResp.put("ResultCode", 0);
				jResp.put("ResultMsg", "Password changed sucesfully");
			}
		} else {
			jResp.put("ResultCode", 402);
			jResp.put("ResultMsg", "Supply valid reset request");
		}
		
		return jResp;
	}



}

/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.io.StringReader;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONArray;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.baraza.xml.BXML;
import org.baraza.xml.BElement;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.utils.BWebUtils;
import org.baraza.utils.BDateFormat;

public class Bajax extends HttpServlet {
	Logger log = Logger.getLogger(Bajax.class.getName());

	BWeb web = null;
	BDB db = null;
	String xmlBase = null;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext context = config.getServletContext();
		String projectDir = context.getInitParameter("projectDir");
		String ps = System.getProperty("file.separator");
		xmlBase = context.getRealPath("WEB-INF") + ps + "configs" + ps;
		if(projectDir != null) xmlBase = projectDir + ps + "configs" + ps;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) { 
		web = new BWeb(getServletContext(), request);
		db = web.getDB();
		
		System.out.println("AJAX Reached : " + request.getServletPath() + " : " + request.getParameter("fnct"));
		
		BWebUtils.showParameters(request);
		
		String sp = request.getServletPath();
		String function = request.getParameter("ajaxfunction");			// function to execute
		String params = request.getParameter("ajaxparams");				// function params
		String from = request.getParameter("from");						// from function
		String fnct = request.getParameter("fnct");
		String id = request.getParameter("id");
		String ids = request.getParameter("ids");
		String startDate = request.getParameter("startdate");
		String startTime = request.getParameter("starttime");
		String endDate = request.getParameter("enddate");
		String endTime = request.getParameter("endtime");
		
		String resp = "";
		
		response.setContentType("application/json;charset=\"utf-8\"");
		if(sp.equals("/ajaxupdate")) {
			if("edit".equals(request.getParameter("oper"))) {
				resp = updateGrid(request);
				response.setContentType("text/html");
			} else if("form".equals(request.getParameter("oper"))) {
				resp = updateFormData(request);
			} else if("delete".equals(request.getParameter("oper"))) {
				resp = deleteFormData(request);
			} else if("formupdate".equals(fnct)) {
				BWebForms webForm = new BWebForms(db);
				resp = resp = webForm.updateForm(request.getParameter("entry_form_id"), request.getParameter("json"));
			} else if("formsubmit".equals(fnct)) {
				BWebForms webForm = new BWebForms(db);
				resp = webForm.submitForm(request.getParameter("entry_form_id"), request.getParameter("json"));
			}
		} else if(sp.equals("/ajaxinsert")) {
			resp = addFormData(request);
		} else if((function != null) && (params != null)) {
			resp = executeSQLFxn(function, params, from);
		} else if("caladd".equals(fnct)) {
			String keyId = request.getParameter("keyId");
			resp = calAdd(keyId, startDate, endDate);
		} else if("calresize".equals(fnct)) {
			resp = calResize(id, endDate, endTime);
		} else if("calmove".equals(fnct)) {
			resp = calMove(id, startDate, startTime, endDate, endTime);
		} else if("caldel".equals(fnct)) {
			resp = calDel(id);
		} else if("filter".equals(fnct)) {
			resp = web.getFilterWhere(request);
			response.setContentType("text/html");
		} else if("linkdata".equals(fnct)) {
			resp = setLinkData(request);
		} else if("operation".equals(fnct)) {
			resp = calOperation(id, ids, request);
		} else if("password".equals(fnct)) {
			resp = changePassword(request.getParameter("oldpass"), request.getParameter("newpass"));
		} else if("importprocess".equals(fnct)) {
			resp = importProcess();
		} else if("importclear".equals(fnct)) {
			resp = importClear();
		} else if("buy_product".equals(fnct)) {
			resp = buyProduct(id, request.getParameter("units"));
		} else if("renew_product".equals(fnct)) {
			resp = renewProduct();
		} else if("tableviewupdate".equals(fnct)) {
			resp = tableViewUpdate(request);
		} else if("jsinsert".equals(fnct)) {
			resp = jsGrid(fnct, request);
		} else if("jsupdate".equals(fnct)) {
			resp = jsGrid(fnct, request);
		} else if("jsfieldupdate".equals(fnct)) {
			resp = jsGrid(fnct, request);
		} else if("jsdelete".equals(fnct)) {
			resp = jsGrid(fnct, request);
		} else if("attendance".equals(fnct)) {
			resp = attendance(request);
		} else if("task".equals(fnct)) {
			resp = tasks(request);
		}
		
		try {
			PrintWriter out = response.getWriter(); 
			out.println(resp);
		} catch(IOException ex) {}
		
		web.close();			// close DB commections
	}
	
	public String updateGrid(HttpServletRequest request) {		
		boolean hasEdit = false;
		BElement view = web.getView();
		String upSql = "UPDATE " + view.getAttribute("updatetable") + " SET ";
		for(BElement el : view.getElements()) {
			if(el.getName().equals("EDITFIELD")) {
				if(hasEdit) upSql += ", ";
				upSql += el.getValue() + " = '" + request.getParameter(el.getValue()) + "'";
				hasEdit = true;
			}
		}
		
		String eResp = null;
		if(hasEdit) {
			String editKey = view.getAttribute("keyfield");
			String id = request.getParameter("KF");
			String autoKeyID = db.insAudit(view.getAttribute("updatetable"), id, "EDIT");
			
			if(view.getAttribute("auditid") != null) upSql += ", " + view.getAttribute("auditid") + " = " + autoKeyID;
			upSql += " WHERE " + editKey + " = '" + id + "'";
			
			eResp = db.executeQuery(upSql);
			
			System.out.println("BASE GRID UPDATE : " + upSql);
		}
		
		JSONObject jResp =  new JSONObject();
		if(eResp == null) {
			jResp.put("status", "OK");
		} else {
			jResp.put("status", "Error");
			jResp.put("msg", eResp);
		}

		return jResp.toString();
	}
	
	public String addFormData(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		
		String appXml = request.getParameter("app_xml");
		String appKey = request.getParameter("app_key");
		String formData = request.getParameter("form_data");
		if(appKey == null) {
			appKey = request.getParameter("view");
			if(appKey == null) appKey = "1:0";
		}
		
		String body = BWebUtils.requestBody(request);
		if(formData == null) formData = body;
		System.out.println("JSON +" + formData + "+");
		
		BXML xml = new BXML(xmlBase + appXml, false);
		
		jResp.put("error", 1);
		if(xml.getDocument() == null) return jResp.toString();
		BElement root = xml.getRoot();
		if(root == null) return jResp.toString();
		BElement view = root.getView(appKey);
		if(view == null) return jResp.toString();
		if(formData == null) return jResp.toString();
		if(!view.getAttribute("secured", "false").equals("true")) return jResp.toString();

		JSONArray jaFormData = new JSONArray(formData);
		
		BQuery rs = new BQuery(db, view, null, null, false);
		if(rs == null) return jResp.toString();
			
		Map<String, String[]> reqParams = new HashMap<String, String[]>();
		for (int j = 0; j < jaFormData.length(); j++) {
			JSONObject jVal = jaFormData.getJSONObject(j);
			if(jVal.has("name") && jVal.has("value")) {
				String keyStr = jVal.getString("name");
				System.out.println(keyStr + " : " + jVal.getString("value"));
				
				String[] pArray = new String[1];
				pArray[0] = jVal.getString("value");
				reqParams.put(keyStr, pArray);
			} else {
				for (String keyName : jVal.keySet()) {
					String[] pArray = new String[1];
					pArray[0] = jVal.get(keyName).toString();
					reqParams.put(keyName, pArray);
				}
			}
			
		}
		
		rs.recAdd();
		String saveMsg = rs.updateFields(reqParams, web.getViewData(), request.getRemoteAddr(), null);
System.out.println("SAVE : " + saveMsg);
		
		if(saveMsg.trim().equals("")) {
			jResp.put("error", 0);
			jResp.put("msg", view.getAttribute("save.msg", "Data updated"));
			jResp.put("data", rs.getRowJSON());

			String dataItem = rs.getKeyField();

			String jumpView = view.getAttribute("jumpview");
			BElement fView = view.getElementByName("FORMVIEW");
			if(jumpView != null) {
				String viewKey = jumpView;
				String mydv = "?view=" + viewKey + "&data=" + dataItem;

				jResp.put("jump", true);
				jResp.put("jumpview", viewKey);
				jResp.put("jumplink", mydv);
			} else if(fView != null) {
				String viewKey = appKey + ":0";
				String mydv = "?view=" + viewKey + "&data=" + dataItem;

				jResp.put("jump", true);
				jResp.put("jumpview", viewKey);
				jResp.put("jumplink", mydv);
			}
		} else {
			jResp.put("error", 2);
			jResp.put("error_msg", saveMsg);
		}
		rs.close();
		
		return jResp.toString();
	}
	
	public String updateFormData(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		
		String appXml = request.getParameter("app_xml");
		String appKey = request.getParameter("app_key");
		String formData = request.getParameter("form_data");
		
		String body = BWebUtils.requestBody(request);
		if(formData == null) formData = body;
		System.out.println("JSON +" + formData + "+");
		
		BXML xml = new BXML(xmlBase + appXml, false);
		
		jResp.put("error", 1);
		if(xml.getDocument() == null) return jResp.toString();
		BElement root = xml.getRoot();
		if(root == null) return jResp.toString();
		BElement view = root.getView(appKey);
		if(view == null) return jResp.toString();
		if(formData == null) return jResp.toString();
		if(!view.getAttribute("secured", "false").equals("true")) return jResp.toString();
		
		JSONArray jaFormData = new JSONArray(formData);
		for (int j = 0; j < jaFormData.length(); j++) {
			JSONObject jVal = jaFormData.getJSONObject(j);
			
			if(jVal.has("KF")) {
				String where = "(" + view.getAttribute("keyfield") + " = '" + jVal.getString("KF") + "')";
				BQuery rs = new BQuery(db, view, where, null);
				rs.moveFirst();
				rs.recEdit();
				
				Map<String, String[]> reqParams = new HashMap<String, String[]>();
				for (String keyName : jVal.keySet()) {
					if(!keyName.equals("KF")) {
						String[] pArray = new String[1];
						pArray[0] = jVal.get(keyName).toString();
						reqParams.put(keyName, pArray);
					}
				}
				
				String saveMsg = rs.updateFields(reqParams, web.getViewData(), request.getRemoteAddr(), "");
				System.out.println("SAVE : " + saveMsg);
				
				if(saveMsg.trim().equals("")) {
					jResp.put("error", 0);
					jResp.put("msg", view.getAttribute("save.msg", "Data updated"));
					jResp.put("data", rs.getRowJSON());
				} else {
					jResp.put("error", 2);
					jResp.put("error_msg", saveMsg);
				}
				rs.close();
			}
		}
		
		return jResp.toString();
	}

	public String deleteFormData(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		jResp.put("error", 1);
		jResp.put("msg", "Not deleted, check configuration");
		
		String appXml = request.getParameter("app_xml");
		String appKey = request.getParameter("app_key");
		String formData = request.getParameter("form_data");
		
		String body = BWebUtils.requestBody(request);
		if(formData == null) formData = body;
		System.out.println("JSON +" + formData + "+");
		
		BXML xml = new BXML(xmlBase + appXml, false);
		
		jResp.put("error", 1);
		if(xml.getDocument() == null) return jResp.toString();
		BElement root = xml.getRoot();
		if(root == null) return jResp.toString();
		BElement view = root.getView(appKey);
		if(view == null) return jResp.toString();
		if(formData == null) return jResp.toString();
		if(!view.getAttribute("secured", "false").equals("true")) return jResp.toString();
		
		String delSql = "DELETE FROM " + view.getAttribute("table")
			+ " WHERE (org_id = " + db.getUserOrgId()
			+ ") AND (" + view.getAttribute("keyfield") + " = '";
		
		JSONArray jaFormData = new JSONArray(formData);
		for (int j = 0; j < jaFormData.length(); j++) {
			JSONObject jVal = jaFormData.getJSONObject(j);
			
			if(jVal.has("KF")) {
				String dSql = delSql + jVal.getString("KF") + "')";
				System.out.println(dSql);
				
				String delErr = db.executeUpdate(dSql);
				
				if(delErr == null) {
					jResp.put("error", 0);
					jResp.put("msg", "Not deleted, check configuration");
				} else {
					jResp.put("error", 1);
					jResp.put("msg", delErr);
				}
			}
		}
		
		return jResp.toString();
	}

	public String calAdd(String keyId, String startDate, String endDate) {
		BElement view = web.getView();

		String sql = "INSERT INTO " + view.getAttribute("update") + " (org_id, " 
			+ view.getElement(3).getValue() + ", "
			+ view.getElement(4).getValue() + ", "
			+ view.getElement(6).getValue() + ", "
			+ view.getElement(0).getAttribute("keyfield") + ", ";
		if(view.getAttribute("linkfield") != null) sql += view.getAttribute("linkfield");
		sql += ") VALUES ('" + db.getUserOrgId().toString() + "', '"
			+ startDate.split("T")[0] + "', '"
			+ startDate.split("T")[1].split("\\.")[0] + "', '"
			+ endDate.split("T")[1].split("\\.")[0] + "', '"
			+ keyId + "'";
		if(view.getAttribute("linkfield") != null) sql += ",'" + web.getDataItem() + "'";
		sql += ")";
System.out.println(sql);
		String calId = db.executeAutoKey(sql);
System.out.println(calId);

		JSONObject jResp =  new JSONObject();
		jResp.put("status", "OK");
		jResp.put("cal_id", calId);

		return jResp.toString();
	}

	public String calResize(String id, String endDate, String endTime) {
		BElement view = web.getView();

		String sql = "UPDATE " + view.getAttribute("update") 
			+ " SET " + view.getElement(6).getValue() + " = '" 
			+ endDate.split("T")[1].split("\\.")[0] + "' "
			+ "WHERE " + view.getAttribute("keyfield") + " = " + id;
		System.out.println(sql);

		web.executeQuery(sql);

		JSONObject jResp =  new JSONObject();
		jResp.put("status", "OK");

		return jResp.toString();
	}

	public String calMove(String id, String startDate, String startTime, String endDate, String endTime) {
		BElement view = web.getView();

		String resp = "";
		if("".equals(endDate)) {
			resp = calResize(id, endDate, endTime);
		} else {
			String sql = "UPDATE " + view.getAttribute("update") 
			+ " SET " + view.getElement(3).getValue() + " = '"  + startDate.split("T")[0] 
			+ "', " + view.getElement(4).getValue() + " = '" + startDate.split("T")[1].split("\\.")[0]
			+ "', " + view.getElement(6).getValue() + " = '" + endDate.split("T")[1].split("\\.")[0] + "' "
			+ "WHERE " + view.getAttribute("keyfield") + " = " + id;
			System.out.println(sql);

			web.executeQuery(sql);
		}

		JSONObject jResp =  new JSONObject();
		jResp.put("status", "OK");
		resp = jResp.toString();

		return resp;
	}
	
	public String calDel(String id) {
		BElement view = web.getView();

		String sql = "DELETE FROM " + view.getAttribute("update") 
			+ " WHERE " + view.getAttribute("keyfield") + " = " + id;
		System.out.println(sql);

		web.executeQuery(sql);

		JSONObject jResp =  new JSONObject();
		jResp.put("status", "OK");

		return jResp.toString();
	}
	
	public String setLinkData(HttpServletRequest request) {
		JSONObject jResp = new JSONObject();
		
		HttpSession webSession = request.getSession(true);
		
		String viewKey = request.getParameter("view");
		String linkData = request.getParameter("linkdata");
		if(viewKey == null) {
			jResp.put("success", false);
			jResp.put("msg", "No view key supplied");
			return jResp.toString();
		} else if(linkData == null) {
			jResp.put("success", false);
			jResp.put("msg", "No link data supplied");
			return jResp.toString();
		}
		
		String linkSN = "L" + viewKey;
		webSession.setAttribute(linkSN, linkData);
		jResp.put("success", true);
		
		return jResp.toString();
	}

	public String executeSQLFxn(String fxn, String prms, String from) {
		JSONObject jResp = new JSONObject();
		
		String getUserID = web.getUserID();

		String query = "";
		if(from == null) query = "SELECT " + fxn + "('" + getUserID + "', '" + prms + "')";
		else query = "SELECT " + fxn + "('" + prms + "', '" + getUserID + "') from " + from;
		System.out.println("SQL function = " + query);

		String str = "";
		if(!prms.trim().equals("")) str = db.executeFunction(query);
		
		if(str == null) {
			jResp.put("success", false);
			jResp.put("msg", db.getLastErrorMsg());
		} else {
			jResp.put("success", true);
			jResp.put("msg", str);
		}

		return jResp.toString();
	}

	public String escapeSQL(String str){				
		String escaped = str.replaceAll("'","\'");						
		return escaped;
	}
	
	public String calOperation(String id, String ids, HttpServletRequest request) {
		String resp = web.setOperations(id, ids, request);
		
		return resp;
	}

	public String changePassword(String oldPass, String newPass) {
		JSONObject jResp =  new JSONObject();
				
		String fnct = web.getRoot().getAttribute("password");
		if(fnct == null) return "{\"success\": 0, \"message\": \"Cannot change Password\"}";
		
		oldPass = oldPass.replaceAll("'", "''");
		newPass = newPass.replaceAll("'", "''");
		
		String mySql = "SELECT " + fnct + "('" + web.getUserID() + "', '" + oldPass + "','" + newPass + "')";
		String myoutput = web.executeFunction(mySql);
		
		if(myoutput == null) {
			jResp.put("success", 0);
			jResp.put("message", "Old Password Is incorrect");
		} else {
			jResp.put("success", 1);
			jResp.put("message", "Password Changed Successfully");
		}
		
		return jResp.toString();
	}
	
	public String importProcess() {
		JSONObject jResp =  new JSONObject();
		
		String sqlProcess = web.getView().getAttribute("process");
		String linkData = web.getLinkData();

		String dbErr = "";
		String myOutput = null;
		if(sqlProcess != null) {
			String mySql = "SELECT " + sqlProcess + "('" + db.getUserOrg() + "', '" + db.getUserID() + "', '" + linkData + "')";
			System.out.println("Process Import : " + mySql);
			myOutput = db.executeFunction(mySql);
			
			if(myOutput == null) dbErr = db.getLastErrorMsg();
			
			System.out.println("Process Import finised : " + myOutput);
		}
		
		if(myOutput != null) {
			jResp.put("error", false);
			jResp.put("success", 1);
			jResp.put("message", myOutput);
		} else if("FILES".equals(web.getView().getName())) {
			jResp.put("error", false);
			jResp.put("success", 2);
			jResp.put("message", "Upload Successfull");
		} else {
			jResp.put("error", true);
			jResp.put("success", 0);
			jResp.put("message", "Processing has issues <br>" + dbErr);
		}
		
		return jResp.toString();
	}
	
	public String importClear() {
		JSONObject jResp =  new JSONObject();
		
		String tableName = web.getView().getAttribute("table");
		String linkField = web.getView().getAttribute("linkfield");
		String linkData = web.getLinkData();

		String myoutput = null;
		if(tableName != null) {
			String delSql = "DELETE FROM " + tableName + " WHERE (org_id = " + db.getUserOrg() + ")";
			if((linkField != null) && (linkData != null)) delSql += " AND (" + linkField + " = '" + linkData + "')";
			System.out.println("Process Clear : " + delSql);
			myoutput = db.executeQuery(delSql);
		}
		
		if(myoutput == null) {
			jResp.put("error", false);
			jResp.put("success", 1);
			jResp.put("message", "Cleared Successfully");
		} else {
			jResp.put("error", true);
			jResp.put("success", 0);
			jResp.put("message", "Processing has issues");
		}
		
		return jResp.toString();
	}
	
	public String renewProduct() {
		JSONObject jResp =  new JSONObject();
		
		String mysql = "SELECT COALESCE(sum(a.cr - a.dr), 0) FROM "
		+ "((SELECT COALESCE(sum(receipt_amount), 0) as cr, 0::real as dr FROM product_receipts "
		+ "WHERE (is_paid = true) AND (org_id = " + db.getUserOrg() + ")) "
		+ "UNION "
		+ "(SELECT 0::real as cr, COALESCE(sum(quantity * price), 0) as dr FROM productions "
		+ "WHERE (org_id = " + db.getUserOrg() + "))) as a";
		String bals = db.executeFunction(mysql);
		Float bal = Float.valueOf(bals);
		
		String rSql = "SELECT a.product_id, a.product_name, a.details, a.annual_cost, a.expiry_date, a.sum_quantity "
		+ "FROM vws_productions a "
		+ "WHERE (a.is_renewed = false) AND (a.org_id = " + db.getUserOrg() + ")";
		BQuery rRs = new BQuery(web.getDB(), rSql);

		rRs.moveFirst();
		String productId = rRs.getString("product_id");
		Float annualCost = rRs.getFloat("annual_cost");
		Integer quantity = rRs.getInt("sum_quantity");
		rRs.close();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		DecimalFormat df = new DecimalFormat("##########.#");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String calS = sdf.format(cal.getTime());
		
		if((annualCost * quantity) <= bal) {
			String updStr = "UPDATE productions SET is_renewed = true "
			+ "WHERE (is_renewed = false) AND (a.org_id = " + db.getUserOrg()
			+ ") AND (product_id = " + productId + ")";
			db.executeQuery(updStr);
			
			String insSql = "INSERT INTO productions(product_id, entity_id, org_id, quantity, price, expiry_date) VALUES ("
			+ productId + "," + db.getUserID() + "," + db.getUserOrg() + "," + quantity.toString() + "," 
			+ df.format(annualCost) + ", '" + calS + "')";
			db.executeQuery(insSql);
			
			jResp.put("success", 0);
			jResp.put("message", "Processing has issues");
		} else {
			jResp.put("success", 1);
			jResp.put("message", "Your balance is " + bals + " which not sufficent for purchase");
		}
		
		return jResp.toString();
	}
	
	public String buyProduct(String productId, String units) {
		JSONObject jResp =  new JSONObject();
		
		String mysql = "SELECT COALESCE(sum(a.cr - a.dr), 0) FROM "
		+ "((SELECT COALESCE(sum(receipt_amount), 0) as cr, 0::real as dr FROM product_receipts "
		+ "WHERE (is_paid = true) AND (org_id = " + db.getUserOrg() + ")) "
		+ "UNION "
		+ "(SELECT 0::real as cr, COALESCE(sum(quantity * price), 0) as dr FROM productions "
		+ "WHERE (org_id = " + db.getUserOrg() + "))) as a";
		String bals = db.executeFunction(mysql);
System.out.println("BASE 2020 : " + bals);
		Float bal = Float.valueOf(bals);
		
		mysql = "SELECT product_id, product_name, is_singular, align_expiry, is_montly_bill, "
		+ "montly_cost, is_annual_bill, annual_cost, details "
		+ "FROM products "
		+ "WHERE product_id = " + productId;
		BQuery rs = new BQuery(db, mysql);
		rs.moveFirst();
		
		mysql = "SELECT production_id, product_id, product_name, is_renewed, quantity, price, amount, expiry_date "
		+ "FROM vw_productions "
		+ "WHERE (is_renewed = false) AND (org_id = " + web.getOrgID() 
		+ ") AND (product_id = " + rs.getString("product_id") + ") "
		+ "ORDER BY production_id desc";
		BQuery rsa = new BQuery(db, mysql);
		
		Float annualCost = rs.getFloat("annual_cost");
		Float buyUnits = Float.valueOf(units);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		
		if(rs.getBoolean("align_expiry")) {
			if(rsa.moveFirst()) {
				Date expiryDate = rsa.getDate("expiry_date");
				long diff = cal.getTimeInMillis() - expiryDate.getTime();
				if(diff > 0) {
					diff = diff / (1000 * 60 * 60 * 24);
					annualCost = annualCost * (366 - diff) / 366;
					
					cal.setTime(expiryDate);
				}
				
				System.out.println("expiry date " + rsa.getDate("expiry_date"));
				System.out.println("expiry diff " + diff);
				System.out.println("expiry cost " + annualCost);
			}
		}
		DecimalFormat df = new DecimalFormat("##########.#");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String calS = sdf.format(cal.getTime());
		
		if((annualCost * buyUnits) <= bal) {
			String insSql = "INSERT INTO productions(product_id, entity_id, org_id, quantity, price, expiry_date) VALUES ("
			+ productId + "," + db.getUserID() + "," + db.getUserOrg() + "," + units + "," 
			+ df.format(annualCost) + ", '" + calS + "')";
			db.executeQuery(insSql);
			
			jResp.put("success", 0); 
			jResp.put("message", "Processing has issues");
		} else {
			jResp.put("success", 1); 
			jResp.put("message", "Your balance is " + bals + " which not sufficent for purchase");
		}

		rs.close();
		rsa.close();
		
		return jResp.toString();
	}
	
	public String tableViewUpdate(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		jResp.put("error", false);
		jResp.put("message", "Updated records");
		
		BElement view = web.getView();
		String jsonField = request.getParameter("jsonfield");

		JSONArray jFields = new JSONArray(jsonField);
		for (int i = 0; i < jFields.length(); i++) {
			JSONObject jField = jFields.getJSONObject(i);

			String upSql = "UPDATE " + view.getAttribute("updatetable") 
			+ " SET " + jField.getString("field_name") + " = '" + jField.getString("field_value") 
			+ "' WHERE " + view.getAttribute("keyfield") + " = '" + jField.getString("key_id") + "';";
System.out.println("BASE 1025 : " + upSql);
			web.executeQuery(upSql);
		}
		
		return jResp.toString();
	}
	
	public String jsGrid(String fnct, HttpServletRequest request) {
		JSONObject jResp = new JSONObject();
		
		BElement view = web.getView();
		if(request.getParameter("viewno") == null) {
			jResp.put("error", true);
			jResp.put("error_msg", "No view no");
			return jResp.toString();
		}
		Integer viewNo = Integer.valueOf(request.getParameter("viewno"));
		BElement SubView = view.getElement(viewNo);
		
		System.out.println("viewno = " + viewNo);
		System.out.println(SubView);

		JSONObject jRowData = new JSONObject();
		String rowData = request.getParameter("jsrowdata");
		if(rowData != null) jRowData = new JSONObject(rowData);
		
		String keyField = request.getParameter("keyfield");
		Map<String, String[]> reqParams = new HashMap<String, String[]>();
		for (String keyStr : jRowData.keySet()) {
			System.out.println(keyStr + " : " + jRowData.get(keyStr).toString());
			
			String[] pArray = new String[1];
			pArray[0] = jRowData.get(keyStr).toString();
			reqParams.put(keyStr, pArray);
			if(keyStr.equals("keyfield")) keyField = jRowData.getString(keyStr);
		}
		
		String linkData = null;
		int vds = web.getViewData().size();
		if(vds > 2) linkData = web.getViewData().get(vds - 1);
		
		if("jsinsert".equals(fnct)) {
			BQuery rs = new BQuery(db, SubView, null, null, false);
			rs.recAdd();
			if(linkData != null && SubView.getAttribute("linkfield") != null) rs.updateField(SubView.getAttribute("linkfield"), linkData); 
			String saveMsg = rs.updateFields(reqParams, web.getViewData(), request.getRemoteAddr(), linkData);
			if("".equals(saveMsg)) {
				jResp = rs.getRowJSON();
				jResp.put("error", false);
			} else {
				jResp.put("error", true);
				jResp.put("error_msg", saveMsg);
			}
			rs.close();
		} else if("jsupdate".equals(fnct)) {
			String whereSql = SubView.getAttribute("keyfield") + " = '" + keyField + "'";
			BQuery rs = new BQuery(db, SubView, whereSql, null, false);
			rs.moveFirst();
			rs.recEdit();
			String saveMsg = rs.updateFields(reqParams, web.getViewData(), request.getRemoteAddr(), "");
			if("".equals(saveMsg)) {
				rs.refresh();
				if(rs.moveFirst()) {
					jResp = rs.getRowJSON();
					jResp.put("error", false);
				} else {
					jResp.put("error", true);
					jResp.put("error_msg", "No Data for row");
				}
			} else {
				jResp.put("error", true);
				jResp.put("error_msg", saveMsg);
			}
			rs.close();
		} else if("jsfieldupdate".equals(fnct)) {
System.out.println("BASE 2010");
			String fieldName = request.getParameter("fieldname");
			String dataValue = request.getParameter("fieldvalue");
			if((keyField != null) && (fieldName != null)) {
				BElement el = SubView.getElement(fieldName);
				if(el.getName().equals("TEXTDECIMAL")) {
					dataValue = dataValue.replace(",", "");
				} else if(el.getName().equals("TEXTDATE")) {
					dataValue = BDateFormat.parseDate(dataValue, el.getAttribute("dbformat"), db.getDBType());
				} else if(el.getName().equals("TEXTTIMESTAMP")) {
					dataValue = BDateFormat.parseTimeStamp(dataValue);
				} else if(el.getName().equals("SPINTIME")) {
					dataValue = BDateFormat.parseTime(dataValue, el.getAttribute("type", "1"));
				}

				String whereSql = SubView.getAttribute("keyfield") + " = '" + keyField + "'";
				BQuery rs = new BQuery(db, SubView, whereSql, null, false);
				rs.moveFirst();
				rs.recEdit();
				String saveMsg = rs.updateField(fieldName, dataValue);
				saveMsg += rs.recSave();
				rs.refresh();
				if("".equals(saveMsg)) {
					if(rs.moveFirst()) {
						jResp = rs.getRowJSON();
						jResp.put("error", false);
					} else {
						jResp.put("error", true);
						jResp.put("error_msg", "No Data for row");
					}
				} else {
					if(rs.moveFirst()) jResp = rs.getRowJSON();
					
					jResp.put("error", true);
					jResp.put("error_msg", saveMsg);
				}
				rs.close();
			}
		} else if("jsdelete".equals(fnct)) {
			String whereSql = SubView.getAttribute("keyfield") + " = '" + keyField + "'";
			BQuery rs = new BQuery(db, SubView, whereSql, null, false);
			rs.moveFirst();
			String saveMsg = rs.recDelete();
			if(saveMsg == null) {
				jResp.put("error", false);
			} else {
				rs.refresh();
				if(rs.moveFirst()) {
					jResp = rs.getRowJSON();
					jResp.put("error", true);
					jResp.put("error_msg", saveMsg);
				}
			}
			rs.close();
		}
		
		return jResp.toString();
	}
	
	public String attendance(HttpServletRequest request) {
		JSONObject jResp = new JSONObject();
		String myOutput = null;
System.out.println("BASE 2020 : ");

		String jsonField = request.getParameter("json");
		if(jsonField != null) {
			JSONObject jObj = new JSONObject(jsonField);

			String mySql = "SELECT add_access_logs(" + db.getUserID() + "," + jObj.getString("log_type")
				+ ",'" + jObj.getString("log_in_out") + "', '" + request.getRemoteAddr() 
				+ "', '" + jObj.getBigDecimal("lat").toString() + "," + jObj.getBigDecimal("long").toString() + "'::point);";
System.out.println("BASE 2030 : " + mySql);
		
			myOutput = db.executeFunction(mySql);
		}
			
		if(myOutput == null) {
			jResp.put("error", 101);
			jResp.put("message", "Attendnace not added");
		} else {
			String lWhere = "(log_time_out is null)";
			if(!myOutput.equals("0")) lWhere = "(access_log_id = " + myOutput + ")";
System.out.println("BASE 2040 : " + lWhere);
			
			jResp.put("error", 0);
			BQuery alRs = new BQuery(db, web.getView().getElementByName("ATTENDANCE").getElementByName("ACCESSLOG"), lWhere, null);
			if(alRs.moveFirst()) jResp = alRs.getRowJSON();
			alRs.close();
		}
//System.out.println("BASE 3120 : " + jResp.toString());
		
		return jResp.toString();
	}
	
	public String tasks(HttpServletRequest request) {
		JSONObject jResp = new JSONObject();
		String myOutput = null;

		String jsonField = request.getParameter("json");
System.out.println("BASE 2120 : " + jsonField);
		if(jsonField != null) {
			JSONObject jObj =  new JSONObject(jsonField);
			
			String mySql = null;
			if("start".equals(jObj.getString("task"))) {
				mySql = "SELECT add_timesheet(" + jObj.getString("task_name")
					+ ",true, '" + jObj.getString("task_narrative") + "');";
			} else {
				mySql = "SELECT add_timesheet(" + jObj.getString("timesheet_id") + ",false, '');";
			}
System.out.println("BASE 2130 : " + mySql);

			myOutput = db.executeFunction(mySql);
		}
		
		if(myOutput == null) {
			jResp.put("success", 0);
			jResp.put("message", "Task not added");
		} else {
			BQuery alRs = new BQuery(db, web.getView().getElementByName("TASK").getElementByName("TIMESHEET"), null, null);
			jResp.put("timesheet", alRs.getJSON());
			alRs.close();
		}
//System.out.println("BASE 2140 : " + jResp.toString());
		
		return jResp.toString();
	}

	
}

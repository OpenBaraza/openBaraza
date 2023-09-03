package org.baraza.hcm;

import java.util.Map;
import java.util.LinkedHashMap;
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

public class BResume extends HttpServlet {
	BDB db = null;
	BUser user = null;
	String orgId = "0";
	String userID = "0";
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		db = new BDB("java:/comp/env/jdbc/database");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		ServletContext context = getServletContext();
		BWeb web = new BWeb(context, request);
		
		if(!db.isValid()) db.reconnect("java:/comp/env/jdbc/database");
		db.setOrgID(web.getRoot().getAttribute("org"));
		db.setUser(request.getRemoteAddr(), request.getRemoteUser());
		
		user = web.getUser();
		if(user != null) {
			orgId = user.getUserOrg();
			userID = user.getUserID();
		}
		if(orgId == null) orgId = "0";
		if(userID == null) userID = "-1";
		
		JSONObject jResp =  new JSONObject();
		
		String fnct = request.getParameter("fnct");

		if ("getApplicant".equals(fnct)) {
			jResp = getApplicant(request);
		} else if ("updateApplicant".equals(fnct)) {
			jResp = updateApplicant(request);
		} else if ("getResume".equals(fnct)) {
			jResp = getResume(request);
		}

		else if("addAddress".equals(fnct)) {
			jResp = addAddress(request);
		} else if("updateAddress".equals(fnct)) {
			jResp = updateAddress(request);
		} else if("deleteAddress".equals(fnct)) {
			jResp = deleteAddress(request);
		}

		else if("addEducation".equals(fnct)) {
			jResp = addEducation(request);
		} else if("updateEducation".equals(fnct)) {
			jResp = updateEducation(request);
		} else if("deleteEducation".equals(fnct)) {
			jResp = deleteEducation(request);
		}

		else if("addSkill".equals(fnct)) {
			jResp = addSkill(request);
		} else if("updateSkill".equals(fnct)) {
			jResp = updateSkill(request);
		} else if("deleteSkill".equals(fnct)) {
			jResp = deleteSkill(request);
		}

		else if("addEmployment".equals(fnct)) {
			jResp = addEmployment(request);
		} else if("updateEmployment".equals(fnct)) {
			jResp = updateEmployment(request);
		} else if("deleteEmployment".equals(fnct)) {
			jResp = deleteEmployment(request);
		}

		else if("addProject".equals(fnct)) {
			jResp = addProject(request);
		} else if("updateProject".equals(fnct)) {
			jResp = updateProject(request);
		} else if("deleteProject".equals(fnct)) {
			jResp = deleteProject(request);
		}

		else if("addReferee".equals(fnct)) {
			jResp = addReferee(request);
		} else if("updateReferee".equals(fnct)) {
			jResp = updateReferee(request);
		} else if("deleteReferee".equals(fnct)) {
			jResp = deleteReferee(request);
		}
		
		response.setContentType("application/json;charset=\"utf-8\"");
		try {
			PrintWriter out = response.getWriter();
			out.println(jResp.toString());
		} catch(IOException ex) {}

		web.close();
	}


	public JSONObject getApplicant(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();

		System.out.println("userID: "+userID+" orgId: "+orgId);

		String mySql = "SELECT entity_id, person_title, surname, first_name, applicant_email, applicant_phone, date_of_birth, gender, marital_status, nationality, identity_card, language, currency_id FROM applicants "
			+ "WHERE entity_id = " + userID;
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("applicant_id", rs.getString("entity_id"));
			jData.put("title", rs.getString("person_title"));
			jData.put("surname", rs.getString("surname"));
			jData.put("othername", rs.getString("first_name"));
			jData.put("email", rs.getString("applicant_email"));
			jData.put("phone", rs.getString("applicant_phone"));
			jData.put("dob", rs.getString("date_of_birth"));
			jData.put("gender", rs.getString("gender"));
			jData.put("marital-status", rs.getString("marital_status"));
			jData.put("nationality", rs.getString("nationality"));
			jData.put("id-number", rs.getString("identity_card"));
			jData.put("language", rs.getString("language"));
			jData.put("currency", rs.getString("currency_id"));

			aData.put(jData);
		}
		rs.close();
		jResp.put("applicant", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}


	public JSONObject updateApplicant(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("person_title", request.getParameter("title"));
		mData.put("surname", request.getParameter("surname"));
		mData.put("first_name", request.getParameter("othername"));
		mData.put("applicant_email", request.getParameter("email"));
		mData.put("applicant_phone", request.getParameter("phone"));
		mData.put("date_of_birth", request.getParameter("dob"));
		mData.put("gender", request.getParameter("gender"));
		mData.put("marital_status", request.getParameter("marital-status"));
		mData.put("nationality", request.getParameter("nationality"));
		mData.put("identity_card", request.getParameter("id-number"));
		mData.put("language", request.getParameter("language"));
		mData.put("currency_id", request.getParameter("currency"));
		
		String updSql = "UPDATE applicants SET person_title = ?, surname = ?, first_name = ?, applicant_email = ?, applicant_phone = ?, date_of_birth = ?, gender = ?, marital_status = ?, nationality = ?, identity_card = ?, language = ?, currency_id = ? "
			+ "WHERE entity_id = " + userID;
		db.saveRec(updSql, mData);
	
		return getApplicant(request);
	}

	public JSONObject getResume(HttpServletRequest request) {
		String applicant_id = request.getParameter("applicant_id");
		JSONObject jResp =  new JSONObject();

		if ( applicant_id != null && !applicant_id.isEmpty() ){
			userID = applicant_id;
		}

		jResp.put("applicant", getApplicant(request).getJSONArray("applicant") );
		jResp.put("address", getAddress(request).getJSONArray("address") );
		jResp.put("education", getEducation(request).getJSONArray("education") );
		jResp.put("employment", getEmployment(request).getJSONArray("employment") );
		jResp.put("skills", getSkill(request).getJSONArray("skills") );
		jResp.put("projects", getProject(request).getJSONArray("projects") );
		jResp.put("referees", getReferee(request).getJSONArray("referees") );
		
		System.out.println(jResp.toString());

		return jResp;
	}


	public JSONObject addAddress(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		mData.put("table_id", userID);
		mData.put("table_name", "entitys");
		mData.put("sys_country_id", request.getParameter("address-country"));
		mData.put("postal_code", request.getParameter("address-code"));
		mData.put("post_office_box", request.getParameter("address-box"));
		mData.put("town", request.getParameter("address-town"));
		mData.put("street", request.getParameter("address-street"));
		mData.put("premises", request.getParameter("address-premises"));
		
		String inSql = "INSERT INTO address (org_id, table_id, table_name, sys_country_id, postal_code, post_office_box, town, street, premises) VALUES (?,?,?,?,?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getAddress(request);
	}
	
	public JSONObject updateAddress(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("sys_country_id", request.getParameter("address-country"));
		mData.put("postal_code", request.getParameter("address-code"));
		mData.put("post_office_box", request.getParameter("address-box"));
		mData.put("town", request.getParameter("address-town"));
		mData.put("street", request.getParameter("address-street"));
		mData.put("premises", request.getParameter("address-premises"));
		String addressId = request.getParameter("address_id");
		
		String updSql = "UPDATE address SET sys_country_id = ?, postal_code = ?, post_office_box = ?, town = ?, street = ?, premises = ? "
			+ "WHERE address_id = " + addressId;
		db.saveRec(updSql, mData);
	
		return getAddress(request);
	}

	public JSONObject deleteAddress(HttpServletRequest request) {		
		String addressId = request.getParameter("address_id");
		String delSql = "DELETE FROM address WHERE address_id = " + addressId;
		db.executeFunction(delSql);
	
		return getAddress(request);
	}
	
	public JSONObject getAddress(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT address_id, sys_country_id, postal_code, post_office_box, town, street, premises FROM address "
			+ "WHERE (org_id = " + orgId + ") AND (table_id = " + userID + ") AND (table_name = 'entitys')";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("address_id", rs.getString("address_id"));
			jData.put("address-country", rs.getString("sys_country_id"));
			jData.put("address-code", rs.getString("postal_code"));
			jData.put("address-box", rs.getString("post_office_box"));
			jData.put("address-town", rs.getString("town"));
			jData.put("address-street", rs.getString("street"));
			jData.put("address-premises", rs.getString("premises"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("address", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject addEducation(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		mData.put("entity_id", userID);
		mData.put("education_class_id", request.getParameter("edu-level"));
		mData.put("name_of_school", request.getParameter("institution"));
		mData.put("date_from", request.getParameter("edu-from"));
		mData.put("date_to", request.getParameter("edu-to"));
		mData.put("examination_taken", request.getParameter("certification"));
		mData.put("grades_obtained", request.getParameter("grades"));
		mData.put("details", request.getParameter("educationDetails"));
		
		String inSql = "INSERT INTO education (org_id, entity_id, education_class_id, name_of_school, date_from, date_to, examination_taken, grades_obtained, details) VALUES (?,?,?,?,?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getEducation(request);
	}
	
	public JSONObject updateEducation(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("education_class_id", request.getParameter("edu-level"));
		mData.put("name_of_school", request.getParameter("institution"));
		mData.put("date_from", request.getParameter("edu-from"));
		mData.put("date_to", request.getParameter("edu-to"));
		mData.put("examination_taken", request.getParameter("certification"));
		mData.put("grades_obtained", request.getParameter("grades"));
		mData.put("details", request.getParameter("educationDetails"));
		String educationId = request.getParameter("education_id");
		
		String updSql = "UPDATE education SET education_class_id = ?, name_of_school = ?, date_from = ?, date_to = ?, examination_taken = ?, grades_obtained = ?, details = ? "
			+ "WHERE education_id = " + educationId;
		db.saveRec(updSql, mData);
	
		return getEducation(request);
	}

	public JSONObject deleteEducation(HttpServletRequest request) {
		String educationId = request.getParameter("education_id");
		String delSql = "DELETE FROM education WHERE education_id = " + educationId;
		db.executeFunction(delSql);
	
		return getEducation(request);
	}
	
	public JSONObject getEducation(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT education_id, education_class_id, name_of_school, date_from, date_to, examination_taken, grades_obtained, details FROM education "
			+ "WHERE (org_id = " + orgId + ") AND (entity_id = " + userID + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("education_id", rs.getString("education_id"));
			jData.put("edu-level", rs.getString("education_class_id"));
			jData.put("institution", rs.getString("name_of_school"));
			jData.put("edu-from", rs.getString("date_from"));
			jData.put("edu-to", rs.getString("date_to"));
			jData.put("certification", rs.getString("examination_taken"));
			jData.put("grades", rs.getString("grades_obtained"));
			jData.put("educationDetails", rs.getString("details"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("education", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject addSkill(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		mData.put("entity_id", userID);
		mData.put("skill_type_id", request.getParameter("skill-name"));
		mData.put("skill_level_id", request.getParameter("skill-level"));
		mData.put("details", request.getParameter("skill-details"));
		
		String inSql = "INSERT INTO skills (org_id, entity_id, skill_type_id, skill_level_id, details) VALUES (?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getSkill(request);
	}
	
	public JSONObject updateSkill(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("skill_type_id", request.getParameter("skill-name"));
		mData.put("skill_level_id", request.getParameter("skill-level"));
		mData.put("details", request.getParameter("skill-details"));
		String skillId = request.getParameter("skill_id");
		
		String updSql = "UPDATE skills SET skill_type_id = ?, skill_level_id = ?, details = ? "
			+ "WHERE skill_id = " + skillId;
		db.saveRec(updSql, mData);
	
		return getSkill(request);
	}

	public JSONObject deleteSkill(HttpServletRequest request) {		
		String skillId = request.getParameter("skill_id");
		String delSql = "DELETE FROM skills WHERE skill_id = " + skillId;
		db.executeFunction(delSql);
	
		return getSkill(request);
	}
	
	public JSONObject getSkill(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT skill_id, skill_type_id, skill_level_id, details FROM skills "
			+ "WHERE (org_id = " + orgId + ") AND (entity_id = " + userID + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("skill_id", rs.getString("skill_id"));
			jData.put("skill-name", rs.getString("skill_type_id"));
			jData.put("skill-level", rs.getString("skill_level_id"));
			jData.put("skill-details", rs.getString("details"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("skills", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}


	public JSONObject addEmployment(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		mData.put("entity_id", userID);
		mData.put("date_from", request.getParameter("emp-from"));
		mData.put("date_to", request.getParameter("emp-to"));
		mData.put("employers_name", request.getParameter("employer"));
		mData.put("position_held", request.getParameter("position"));
		mData.put("details", request.getParameter("employmentDetails"));
		
		String inSql = "INSERT INTO employment (org_id, entity_id, date_from, date_to, employers_name, position_held, details) VALUES (?,?,?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getEmployment(request);
	}
	
	public JSONObject updateEmployment(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("date_from", request.getParameter("emp-from"));
		mData.put("date_to", request.getParameter("emp-to"));
		mData.put("employers_name", request.getParameter("employer"));
		mData.put("position_held", request.getParameter("position"));
		mData.put("details", request.getParameter("employmentDetails"));
		String employmentId = request.getParameter("employment_id");
		
		String updSql = "UPDATE employment SET date_from = ?, date_to = ?, employers_name = ?, position_held = ?, details = ? "
			+ "WHERE employment_id = " + employmentId;
		db.saveRec(updSql, mData);
	
		return getEmployment(request);
	}

	public JSONObject deleteEmployment(HttpServletRequest request) {		
		String employmentId = request.getParameter("employment_id");
		String delSql = "DELETE FROM employment WHERE employment_id = " + employmentId;
		db.executeFunction(delSql);
	
		return getEmployment(request);
	}
	
	public JSONObject getEmployment(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT employment_id, date_from, date_to, employers_name, position_held, details FROM employment "
			+ "WHERE (org_id = " + orgId + ") AND (entity_id = " + userID + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("employment_id", rs.getString("employment_id"));
			jData.put("emp-from", rs.getString("date_from"));
			jData.put("emp-to", rs.getString("date_to"));
			jData.put("employer", rs.getString("employers_name"));
			jData.put("position", rs.getString("position_held"));
			jData.put("employmentDetails", rs.getString("details"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("employment", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}

	public JSONObject addProject(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		mData.put("entity_id", userID);
		mData.put("cv_project_name", request.getParameter("project-name"));
		mData.put("cv_project_date", request.getParameter("project-date"));
		mData.put("details", request.getParameter("projectDetails"));
		
		String inSql = "INSERT INTO cv_projects (org_id, entity_id, cv_project_name, cv_project_date, details) VALUES (?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getProject(request);
	}
	
	public JSONObject updateProject(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("cv_project_name", request.getParameter("project-name"));
		mData.put("cv_project_date", request.getParameter("project-date"));
		mData.put("details", request.getParameter("projectDetails"));
		String projectId = request.getParameter("project_id");
		
		String updSql = "UPDATE cv_projects SET cv_project_name = ?, cv_project_date = ?, details = ? "
			+ "WHERE cv_projectid = " + projectId;
		db.saveRec(updSql, mData);
	
		return getProject(request);
	}

	public JSONObject deleteProject(HttpServletRequest request) {		
		String projectId = request.getParameter("project_id");
		String delSql = "DELETE FROM cv_projects WHERE cv_projectid = " + projectId;
		db.executeFunction(delSql);
	
		return getProject(request);
	}
	
	public JSONObject getProject(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT cv_projectid, cv_project_name, cv_project_date, details FROM cv_projects "
			+ "WHERE (org_id = " + orgId + ") AND (entity_id = " + userID + ")";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("project_id", rs.getString("cv_projectid"));
			jData.put("project-name", rs.getString("cv_project_name"));
			jData.put("project-date", rs.getString("cv_project_date"));
			jData.put("projectDetails", rs.getString("details"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("projects", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}


	public JSONObject addReferee(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("org_id", orgId);
		mData.put("table_id", userID);
		mData.put("table_name", "referees");
		mData.put("address_name", request.getParameter("referee-name"));
		mData.put("company_name", request.getParameter("referee-company"));
		mData.put("position_held", request.getParameter("referee-position"));
		mData.put("phone_number", request.getParameter("referee-phone"));
		mData.put("email", request.getParameter("referee-email"));
		mData.put("sys_country_id", request.getParameter("referee-country"));
		mData.put("town", request.getParameter("referee-town"));
		
		String inSql = "INSERT INTO address (org_id, table_id, table_name, address_name, company_name, position_held, phone_number, email, sys_country_id, town) VALUES (?,?,?,?,?,?,?,?,?,?)";
		String keyFieldId = db.saveRec(inSql, mData);
	
		return getReferee(request);
	}
	
	public JSONObject updateReferee(HttpServletRequest request) {
		Map<String, String> mData = new LinkedHashMap<String, String>();
		mData.put("address_name", request.getParameter("referee-name"));
		mData.put("company_name", request.getParameter("referee-company"));
		mData.put("position_held", request.getParameter("referee-position"));
		mData.put("phone_number", request.getParameter("referee-phone"));
		mData.put("email", request.getParameter("referee-email"));
		mData.put("sys_country_id", request.getParameter("referee-country"));
		mData.put("town", request.getParameter("referee-town"));
		String refereeId = request.getParameter("referee_id");
		
		String updSql = "UPDATE address SET address_name = ?, company_name = ?, position_held = ?, phone_number = ?, email = ?, sys_country_id = ?, town = ? "
			+ "WHERE address_id = " + refereeId;
		db.saveRec(updSql, mData);
	
		return getReferee(request);
	}

	public JSONObject deleteReferee(HttpServletRequest request) {		
		String refereeId = request.getParameter("referee_id");
		String delSql = "DELETE FROM address WHERE address_id = " + refereeId;
		db.executeFunction(delSql);
	
		return getReferee(request);
	}
	
	public JSONObject getReferee(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		String mySql = "SELECT address_id, address_name, company_name, position_held, phone_number, email, sys_country_id, town FROM address "
			+ "WHERE (org_id = " + orgId + ") AND (table_id = " + userID + ") AND (table_name = 'referees')";
		BQuery rs = new BQuery(db, mySql);
		
		JSONArray aData = new JSONArray();
		while(rs.moveNext()) {
			JSONObject jData =  new JSONObject();
			jData.put("referee_id", rs.getString("address_id"));
			jData.put("referee-name", rs.getString("address_name"));
			jData.put("referee-company", rs.getString("company_name"));
			jData.put("referee-position", rs.getString("position_held"));
			jData.put("referee-phone", rs.getString("phone_number"));
			jData.put("referee-email", rs.getString("email"));
			jData.put("referee-country", rs.getString("sys_country_id"));
			jData.put("referee-town", rs.getString("town"));
			aData.put(jData);
		}
		rs.close();
		jResp.put("referees", aData);
		
		System.out.println(jResp.toString());

		return jResp;
	}
}

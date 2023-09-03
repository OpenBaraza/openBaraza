package org.baraza.hcm;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.sql.Date;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

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

import org.baraza.utils.BWebUtils;
import org.baraza.xml.BElement;
import org.baraza.DB.BUser;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;

public class BRecruitment extends HttpServlet {

	BDB db = null;
	BUser user = null;
	
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
		
		if(!db.isValid()) db.reconnect("java:/comp/env/jdbc/database");

		String tag = request.getParameter("tag");
		if ("opportunity_count".equals(tag)){
			jResp =  getOpportunityCount(request);
		} else if ("search_email".equals(tag)){
			jResp = searchEmail(request);
		} else if ("search_intern_email".equals(tag)){
			jResp = searchInternEmail(request);
		} else if ("search_opportunity_email".equals(tag)){
			jResp = searchOpportunityEmail(request);
		} else if ("apply_job".equals(tag)){
			jResp = applyJob(request);
		} else if ("apply_internship".equals(tag)){
			jResp = applyInternship(request);
		} else if ("apply_opportunity".equals(tag)){
			jResp = applyOpportunity(request);
		}

		response.setContentType("application/json;charset=\"utf-8\"");
		try {
			PrintWriter out = response.getWriter();
			out.println(jResp.toString());
		} catch(IOException ex) {

			System.out.print("The errror is "+ex);
		}
	}

	public JSONObject searchEmail(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		
		String email = request.getParameter("email");
		if(email == null) email = "";
		email = email.toLowerCase().trim();
		
		String applicationId = null;
		String intakeId = request.getParameter("intake_id");
			
		String mySql = "SELECT entity_id FROM entitys " 
			+ "WHERE lower(trim(user_name)) = '" + email + "'";
		String entityId = db.executeFunction(mySql);
		
		if (entityId != null) {
			mySql = "SELECT application_id FROM applications "
				+ "WHERE (entity_id = " + entityId + ") "
				+ "AND (intake_id = " + intakeId + ")";
			applicationId = db.executeFunction(mySql);
		}

		if (entityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Email not found");
		} else if (applicationId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Application already done, check your email for confirmation code");
			jResp.put("entityId", entityId);
		} else {
			jResp.put("success", 2);
			jResp.put("message", "Email found, make application");
			jResp.put("entityId", entityId);
		}

		return jResp;
	}

	public JSONObject searchInternEmail(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		
		String email = request.getParameter("email");
		if(email == null) email = "";
		email = email.toLowerCase().trim();
		
		String internId = null;
		String internshipId = request.getParameter("internship_id");
			
		String mySql = "SELECT entity_id FROM entitys " 
			+ "WHERE lower(trim(user_name)) = '" + email + "'";
		String entityId = db.executeFunction(mySql);
		
		if (entityId != null) {
			mySql = "SELECT intern_id FROM interns "
				+ "WHERE (entity_id = " + entityId + ") "
				+ "AND (internship_id = " + internshipId + ")";
			internId = db.executeFunction(mySql);
		}

		if (entityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Email not found");
		} else if (internId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Application already done, check your email for confirmation code");
			jResp.put("entityId", entityId);
		} else {
			jResp.put("success", 2);
			jResp.put("message", "Email found, make application");
			jResp.put("entityId", entityId);
		}

		return jResp;
	}

	public JSONObject searchOpportunityEmail(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();

		String email = request.getParameter("email");
		if(email == null) email = "";
		email = email.toLowerCase().trim();

		String internId = null;
		String opportunityId = request.getParameter("opportunity_id");

		String mySql = "SELECT entity_id FROM entitys "
			+ "WHERE lower(trim(user_name)) = '" + email + "'";
		String entityId = db.executeFunction(mySql);

		if (entityId != null) {
			mySql = "SELECT submission_id FROM submissions "
				+ "WHERE (entity_id = " + entityId + ") "
				+ "AND (opportunity_id = " + opportunityId + ")";
			internId = db.executeFunction(mySql);
		}

		if (entityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Email not found");
		} else if (internId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Application already done, check your email for confirmation code");
			jResp.put("entityId", entityId);
		} else {
			jResp.put("success", 2);
			jResp.put("message", "Email found, make application");
			jResp.put("entityId", entityId);
		}

		return jResp;
	}

	public JSONObject applyJob(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		
		jResp.put("success", 0);
		jResp.put("message", "Send input parameters");
		
		String email = request.getParameter("email");
		String entityId = request.getParameter("entity_id");
		String intakeId = request.getParameter("intake_id");
		if(email == null) return jResp;
		if(entityId == null) return jResp;
		if(intakeId == null) return jResp;
			
		String mySql = "SELECT entity_id FROM entitys " 
			+ "WHERE lower(trim(user_name)) = '" + email + "'";
		String dbEntityId = db.executeFunction(mySql);
				
		mySql = "SELECT application_id FROM applications "
			+ "WHERE (entity_id = " + dbEntityId + ") "
			+ "AND (intake_id = " + intakeId + ")";
		String applicationId = db.executeFunction(mySql);
		
		mySql = "SELECT org_id FROM intake WHERE intake_id = " + intakeId;
		String orgId = db.executeFunction(mySql);

		if (dbEntityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (!entityId.equals(dbEntityId)) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (applicationId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Application already done");
		} else {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			mySql = "INSERT INTO applications (org_id, intake_id, entity_id, confirmation_code) "
				+ "VALUES (" + orgId + ", " + intakeId + ", " + entityId + ", '" + uuid + "')";
			System.out.println("BASE 7050 : " + mySql);
			applicationId = db.executeAutoKey(mySql);
			
			mySql = "SELECT sys_email_id FROM sys_emails WHERE (use_type = 35) AND (org_id = " + orgId + ")";
			String emailId = db.executeFunction(mySql);
			
			if(emailId != null) {
				mySql = "INSERT INTO sys_emailed (sys_email_id, org_id, table_id, table_name, email_type) "
					+ "VALUES (" + emailId + ", " + orgId + ", " + applicationId + ", 'applications', 35)";
				db.executeAutoKey(mySql);
			}
		
			jResp.put("success", 1);
			jResp.put("message", "Application has been made and a confirmation email sent");
			jResp.put("applicationId", applicationId);
		}

		return jResp;
	}
	
	public JSONObject applyInternship(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		
		jResp.put("success", 0);
		jResp.put("message", "Send input parameters");
		
		String email = request.getParameter("email");
		String entityId = request.getParameter("entity_id");
		String internshipId = request.getParameter("internship_id");
		if(email == null) return jResp;
		if(entityId == null) return jResp;
		if(internshipId == null) return jResp;
			
		String mySql = "SELECT entity_id FROM entitys " 
			+ "WHERE lower(trim(user_name)) = '" + email + "'";
		String dbEntityId = db.executeFunction(mySql);
				
		mySql = "SELECT intern_id FROM interns "
			+ "WHERE (entity_id = " + dbEntityId + ") "
			+ "AND (internship_id = " + internshipId + ")";
		String internId = db.executeFunction(mySql);
		
		mySql = "SELECT org_id FROM internships WHERE internship_id = " + internshipId;
		String orgId = db.executeFunction(mySql);

		if (dbEntityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (!entityId.equals(dbEntityId)) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (internId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Application already done");
		} else {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			mySql = "INSERT INTO interns (org_id, internship_id, entity_id, confirmation_code) "
				+ "VALUES (" + orgId + ", " + internshipId + ", " + entityId + ", '" + uuid + "')";
			System.out.println("BASE 7050 : " + mySql);
			internId = db.executeAutoKey(mySql);
			
			mySql = "SELECT sys_email_id FROM sys_emails WHERE (use_type = 37) AND (org_id = " + orgId + ")";
			String emailId = db.executeFunction(mySql);
			
			if(emailId != null) {
				mySql = "INSERT INTO sys_emailed (sys_email_id, org_id, table_id, table_name, email_type) "
					+ "VALUES (" + emailId + ", " + orgId + ", " + internId + ", 'interns', 37)";
				db.executeAutoKey(mySql);
			}
		
			jResp.put("success", 1);
			jResp.put("message", "Application has been made and a confirmation email sent");
			jResp.put("internId", internId);
		}

		return jResp;
	}

	public JSONObject applyOpportunity(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		
		jResp.put("success", 0);
		jResp.put("message", "Send input parameters");
		
		String email = request.getParameter("email");
		String entityId = request.getParameter("entity_id");
		String opportunityId = request.getParameter("opportunity_id");
		String applicationPricing = request.getParameter("application_pricing");
		String applicantComments = request.getParameter("applicant_comments");
		if(email == null) return jResp;
		if(entityId == null) return jResp;
		if(opportunityId == null) return jResp;
			
		String mySql = "SELECT entity_id FROM entitys " 
			+ "WHERE lower(trim(user_name)) = '" + email + "'";
		String dbEntityId = db.executeFunction(mySql);
				
		mySql = "SELECT submission_id FROM submissions "
			+ "WHERE (entity_id = " + dbEntityId + ") "
			+ "AND (opportunity_id = " + opportunityId + ")";
		String submissionId = db.executeFunction(mySql);
		
		mySql = "SELECT org_id FROM opportunities WHERE opportunity_id = " + opportunityId;
		String orgId = db.executeFunction(mySql);

		if (dbEntityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (!entityId.equals(dbEntityId)) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (submissionId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Application already done");
		} else {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			mySql = "INSERT INTO submissions (org_id, opportunity_id, entity_id, confirmation_code, "
				+ "application_pricing, applicant_comments) "
				+ "VALUES (?,?,?,?,?,?)";
			System.out.println("BASE 7050 : " + mySql);

			Map<String, String> mData = new LinkedHashMap<String, String>();
			mData.put("org_id", orgId);
			mData.put("opportunity_id", opportunityId);
			mData.put("entity_id", entityId);
			mData.put("confirmation_code", uuid);
			mData.put("application_pricing", applicationPricing);
			mData.put("applicant_comments", applicantComments);
			submissionId = db.saveRec(mySql, mData);
			
			mySql = "SELECT sys_email_id FROM sys_emails WHERE (use_type = 38) AND (org_id = " + orgId + ")";
			String emailId = db.executeFunction(mySql);
			
			if(emailId != null) {
				mySql = "INSERT INTO sys_emailed (sys_email_id, org_id, table_id, table_name, email_type) "
					+ "VALUES (" + emailId + ", " + orgId + ", " + submissionId + ", 'applications', 38)";
				db.executeAutoKey(mySql);
			}
		
			jResp.put("success", 1);
			jResp.put("message", "Application has been made and a confirmation email sent");
			jResp.put("submissionId", submissionId);
		}

		return jResp;
	}

	public JSONObject getOpportunityCount(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();

		String cSql = "SELECT count(intake_id) FROM vw_intake "
			+ "WHERE (hcm_post = true) AND (closing_date >= current_date)";
		jResp.put("jobs", Integer.valueOf(db.executeFunction(cSql)));

		cSql = "SELECT count(intake_id) FROM vw_client_intake "
			+ "WHERE (hcm_post = true) AND (closing_date >= current_date)";
		jResp.put("client_jobs", Integer.valueOf(db.executeFunction(cSql)));

		cSql = "SELECT count(internship_id) FROM vw_internships "
			+ "WHERE (hcm_post = true) AND (closing_date >= current_date)";
		jResp.put("internships", Integer.valueOf(db.executeFunction(cSql)));

		cSql = "SELECT count(opportunity_id) FROM vw_opportunities "
			+ "WHERE (hcm_post = true) AND (closing_date >= current_date) AND (opportunity_type_id = 1)";
		jResp.put("consultancies", Integer.valueOf(db.executeFunction(cSql)));

		cSql = "SELECT count(opportunity_id) FROM vw_opportunities "
			+ "WHERE (hcm_post = true) AND (closing_date >= current_date) AND (opportunity_type_id = 2)";
		jResp.put("part_time_jobs", Integer.valueOf(db.executeFunction(cSql)));

		cSql = "SELECT count(opportunity_id) FROM vw_opportunities "
			+ "WHERE (hcm_post = true) AND (closing_date >= current_date) AND (opportunity_type_id = 3)";
		jResp.put("volunteer_posts", Integer.valueOf(db.executeFunction(cSql)));

		return jResp;
	}
}

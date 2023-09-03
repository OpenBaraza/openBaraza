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

public class BTraining extends HttpServlet {

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
		if ("search_training_email".equals(tag)){
			jResp = searchTrainingEmail(request);
		} else if ("apply_training".equals(tag)){
			jResp = applyTraining(request);
		} else if ("search_course_email".equals(tag)){
			jResp = searchCourseEmail(request);
		} else if ("apply_course".equals(tag)){
			jResp = applyCourse(request);
		}

		response.setContentType("application/json;charset=\"utf-8\"");
		try {
			PrintWriter out = response.getWriter();
			out.println(jResp.toString());
		} catch(IOException ex) {

			System.out.print("The errror is "+ex);
		}
	}

	public JSONObject searchTrainingEmail(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		
		String email = request.getParameter("email");
		if(email == null) email = "";
		email = email.toLowerCase().trim();
		
		String trainingStudentId = null;
		String trainingId = request.getParameter("training_id");
			
		String mySql = "SELECT entity_id FROM entitys " 
			+ "WHERE lower(trim(primary_email)) = '" + email + "'";
		String entityId = db.executeFunction(mySql);
		
		if (entityId != null) {
			mySql = "SELECT training_student_id FROM training_students "
				+ "WHERE (entity_id = " + entityId + ") "
				+ "AND (training_id = " + trainingId + ")";
			trainingStudentId = db.executeFunction(mySql);
		}

		if (entityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Email not found");
		} else if (trainingStudentId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Training application already done, check your email for confirmation");
			jResp.put("entityId", entityId);
		} else {
			jResp.put("success", 2);
			jResp.put("message", "Email found, make application");
			jResp.put("entityId", entityId);
		}

		return jResp;
	}

	public JSONObject applyTraining(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();
		
		jResp.put("success", 0);
		jResp.put("message", "Send input parameters");
		
		String email = request.getParameter("email");
		String entityId = request.getParameter("entity_id");
		String trainingId = request.getParameter("training_id");
		if(email == null) return jResp;
		if(entityId == null) return jResp;
		if(trainingId == null) return jResp;
			
		String mySql = "SELECT entity_id FROM entitys " 
			+ "WHERE lower(trim(primary_email)) = '" + email + "'";
		String dbEntityId = db.executeFunction(mySql);
				
		mySql = "SELECT training_student_id FROM training_students "
			+ "WHERE (entity_id = " + dbEntityId + ") "
			+ "AND (training_id = " + trainingId + ")";
		String trainingStudentId = db.executeFunction(mySql);
		
		mySql = "SELECT org_id FROM trainings WHERE training_id = " + trainingId;
		String orgId = db.executeFunction(mySql);

		if (dbEntityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (!entityId.equals(dbEntityId)) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (trainingStudentId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Application already done");
		} else {
			mySql = "INSERT INTO training_students (org_id, training_id, entity_id) "
				+ "VALUES (" + orgId + ", " + trainingId + ", " + entityId + ")";
			System.out.println("BASE 7050 : " + mySql);
			trainingStudentId = db.executeAutoKey(mySql);
		
			jResp.put("success", 1);
			jResp.put("message", "Application has been made and a confirmation email sent");
			jResp.put("trainingStudentId", trainingStudentId);
		}

		return jResp;
	}
	
	public JSONObject searchCourseEmail(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();

		String email = request.getParameter("email");
		if(email == null) email = "";
		email = email.toLowerCase().trim();

		String trainingRequestId = null;
		String trainingCourseId = request.getParameter("training_course_id");

		String mySql = "SELECT entity_id FROM entitys "
			+ "WHERE lower(trim(primary_email)) = '" + email + "'";
		String entityId = db.executeFunction(mySql);

		if (entityId != null) {
			mySql = "SELECT training_request_id FROM training_requests "
				+ "WHERE (entity_id = " + entityId + ") "
				+ "AND (training_course_id = " + trainingCourseId + ")";
			trainingRequestId = db.executeFunction(mySql);
		}

		if (entityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Email not found");
		} else if (trainingRequestId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Course request already done, check your email for confirmation");
			jResp.put("entityId", entityId);
		} else {
			jResp.put("success", 2);
			jResp.put("message", "Email found, make application");
			jResp.put("entityId", entityId);
		}

		return jResp;
	}

	public JSONObject applyCourse(HttpServletRequest request) {
		JSONObject jResp =  new JSONObject();

		jResp.put("success", 0);
		jResp.put("message", "Send input parameters");

		String email = request.getParameter("email");
		String entityId = request.getParameter("entity_id");
		String trainingCourseId = request.getParameter("training_course_id");
		if(email == null) return jResp;
		if(entityId == null) return jResp;
		if(trainingCourseId == null) return jResp;

		String mySql = "SELECT entity_id FROM entitys "
			+ "WHERE lower(trim(primary_email)) = '" + email + "'";
		String dbEntityId = db.executeFunction(mySql);

		mySql = "SELECT training_request_id FROM training_requests "
			+ "WHERE (entity_id = " + dbEntityId + ") "
			+ "AND (training_course_id = " + trainingCourseId + ")";
		String trainingRequestId = db.executeFunction(mySql);

		mySql = "SELECT org_id FROM training_courses WHERE training_course_id = " + trainingCourseId;
		String orgId = db.executeFunction(mySql);

		if (dbEntityId == null) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (!entityId.equals(dbEntityId)) {
			jResp.put("success", 0);
			jResp.put("message", "Confirm entity ID and email");
		} else if (trainingRequestId != null) {
			jResp.put("success", 1);
			jResp.put("message", "Application already done");
		} else {
			mySql = "INSERT INTO training_requests (org_id, training_course_id, entity_id) "
				+ "VALUES (" + orgId + ", " + trainingCourseId + ", " + entityId + ")";
			System.out.println("BASE 7050 : " + mySql);
			trainingRequestId = db.executeAutoKey(mySql);

			jResp.put("success", 1);
			jResp.put("message", "Training request has been made and a confirmation email sent");
			jResp.put("trainingRequestId", trainingRequestId);
		}

		return jResp;
	}

}

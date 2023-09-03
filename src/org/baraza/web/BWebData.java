/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2020.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.util.logging.Logger;

import java.util.Map;
import java.util.LinkedHashMap;
import java.io.PrintWriter;
import java.io.IOException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.utils.BWebUtils;
import org.baraza.xml.BXML;
import org.baraza.xml.BElement;
import org.baraza.DB.BTranslations;
import org.baraza.DB.BWebBody;
import org.baraza.DB.BUser;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;

public class BWebData {
	Logger log = Logger.getLogger(BWebData.class.getName());
	BTranslations translations = null;
	BElement root = null;
	BElement view = null;

	BDB db = null;
	BUser user = null;
	String userID = "0";
	String orgId = "0";
	String bmcId = "0";
	
	/* Initialize the class with only db handle */
	public BWebData(String dbConfig, HttpServletRequest request) {
		db = new BDB(dbConfig);
		db.setUser(request.getRemoteAddr(), request.getRemoteUser());
		user = db.getUser();
		if(user != null) {
			userID = user.getUserID();
			orgId = user.getUserOrg();
		}
		if(orgId == null) orgId = "0";
	}

	/* Initialize the class with a application xml */
	public BWebData(ServletContext context, HttpServletRequest request, String xmlCnf) {
		String dbConfig = "java:/comp/env/jdbc/database";
		
		String ps = System.getProperty("file.separator");
		String xmlFile = context.getRealPath("WEB-INF") + ps + "configs" + ps + xmlCnf;
		String projectDir = context.getInitParameter("projectDir");
		if(projectDir != null) xmlFile = projectDir + ps + "configs" + ps + xmlCnf;		

		BXML xml = new BXML(xmlFile, false);
		if(xml.getDocument() == null) {
			log.severe("XML loading file error");
		} else {
			root = xml.getRoot();
			db = new BDB(dbConfig);

			if(context.getAttribute("translations") !=  null) 
				translations = (BTranslations) context.getAttribute("translations");
		}

	}

	/* generate a web form */
	public String getForm(HttpServletRequest request, String viewKey) {
		view = root.getView(viewKey);
		String formHtml = getForm(request);
		return formHtml;
	}

	/* generate a web form */
	public String getForm(HttpServletRequest request) {
		BWebBody webBody = new BWebBody(db, view, null, null, translations);
		String formHtml = webBody.getForm(true, null, request);
		webBody.close();
		return formHtml;
	}

	/* Generate a modal form */
	public String getModalForm(HttpServletRequest request, String viewKey) {
		view = root.getView(viewKey);
		//System.out.println(view);

		String title = view.getAttribute("title");
		String formId = view.getAttribute("form.id");
		String saveButton = view.getAttribute("save.button");

		String modalFormHtml = "	<div class='modal fade' id='modal_" + formId + "' tabindex='-1' role='dialog' aria-labelledby='modalTitle' aria-hidden='true'>"
		+ "	  <div class='modal-dialog modal-dialog-centered' role='document'>"
		+ "	    <div class='modal-content'>"
		+ "	      <div class='modal-header'>"
		+ "	        <button type='button' class='close' data-dismiss='modal' aria-label='Close'>"
		+ "	          <span aria-hidden='true'>&times;</span>"
		+ "	        </button>"
		+ "	        <h3 class='modal-title' id='modalTitle' style='color: #000'>" + title + "</h3>"
		+ "	      </div>"
		+ "	      <div class='modal-body'>"
		+ "	        <form id='frm_" + formId + "'>"
		+ getForm(request)
		+ "	        </form>"
		+ "	      </div>"
		+ "	      <div class='modal-footer'>"
		+ "	        <button type='button' class='btn btn-secondary' data-dismiss='modal'>Cancel</button>"
		+ "	        <button type='button' class='btn btn-primary' id='btn_" + formId + "'>" + saveButton + "</button>"
		+ "	      </div>"
		+ "	    </div>"
		+ "	  </div>"
		+ "	</div>";

		return modalFormHtml;
	}

	/* Calling functions getBmcWizard header */
	public String getBmcWizardHeader() {
		String sResp = "";
		String mySql = "SELECT canvas_area_id, canvas_area_type_id, canvas_area_name, canvas_area_details "
			+ "FROM canvas_areas "
			+ "WHERE (canvas_area_type_id = 5) ORDER BY canvas_area_id";
		BQuery rs = new BQuery(db, mySql);
		
		int steps = 2;
		while(rs.moveNext()) {
			String areaName = rs.getString("canvas_area_name");
			sResp += "<div class='m-wizard__step m-wizard__step--current' m-wizard-target='m_wizard_form_step_" + steps + "'>\n"
				+ "	<div class='m-wizard__step-info'>\n"
				+ "		<a href='#' class='m-wizard__step-number'>\n"
				+ "			<span><span>" + steps + "</span></span>\n"			 
				+ "		</a>\n"
				+ "		<div class='m-wizard__step-line'>\n"
				+ "			<span></span>\n"
				+ "		</div>\n"
				+ "		<div class='m-wizard__step-label steps'>" + areaName + "</div>\n"
				+ "	</div>\n"
				+ "</div>\n";
			steps++;
		}

		return sResp;
	}
	
	/* Calling functions getBmcWizard data */
	public String getBmcWizard() {
		String sResp = "";
		String mySql = "SELECT canvas_area_id, canvas_area_type_id, canvas_area_name, canvas_area_details "
			+ "FROM canvas_areas "
			+ "WHERE (canvas_area_type_id = 5) ORDER BY canvas_area_id";
		BQuery rs = new BQuery(db, mySql);
		
		int steps = 2;
		while(rs.moveNext()) {
			String areaName = rs.getString("canvas_area_name");
			sResp += "<div class='m-wizard__form-step' id='m_wizard_form_step_" + steps + "'>\n"
				+ "	<div class='m-form__section m-form__section--first'>\n"
				+ "		<div class='m-form__heading'>\n"
				+ "			<h3 class='m-form__heading-title'>" + areaName + "</h3>\n"
				+ "		</div>\n"
				+ "		<label class='col-form-label'>" + rs.getString("canvas_area_details") + "</label>\n"
				+ "		<div class='form-group m-form__group row'>\n"
				+ "			<div class='col-xl-9 col-lg-9'>\n"
				+ "				<textarea rows='14' type='text' name='" + areaName.replace(" ", "").trim()
				+ "' id='" + areaName.replace(" ", "_").trim()
				+ "' class='form-control m-input m-textarea' placeholder='' value=''></textarea>\n"
				+ "			</div>\n"
				+ "		</div>\n"
				+ "	</div>\n"
				+ "</div>\n";
			steps++;
		}
		rs.close();

		return sResp;
	}

	/* Calling functions getBmcWizard header */
	public String getVpcWizardHeader() {
		String sResp = "";
		String mySql = "SELECT canvas_area_id, canvas_area_type_id, canvas_area_name, canvas_area_details "
			+ "FROM canvas_areas "
			+ "WHERE (canvas_area_type_id = 1) ORDER BY canvas_area_id";
		BQuery rs = new BQuery(db, mySql);
		
		int steps = 2;
		while(rs.moveNext()) {
			String areaName = rs.getString("canvas_area_name");
			sResp += "<div class='m-wizard__step m-wizard__step--current' m-wizard-target='m_wizard_form_step_" + steps + "'>\n"
				+ "	<div class='m-wizard__step-info'>\n"
				+ "		<a href='#' class='m-wizard__step-number'>\n"
				+ "			<span><span>" + steps + "</span></span>\n"			 
				+ "		</a>\n"
				+ "		<div class='m-wizard__step-line'>\n"
				+ "			<span></span>\n"
				+ "		</div>\n"
				+ "		<div class='m-wizard__step-label steps'>" + areaName + "</div>\n"
				+ "	</div>\n"
				+ "</div>\n";
			steps++;
		}

		return sResp;
	}
	
	/* Calling functions getVpcWizard data */
	public String getVpcWizard() {
		String sResp = "";
		String mySql = "SELECT canvas_area_id, canvas_area_type_id, canvas_area_name, canvas_area_details "
			+ "FROM canvas_areas "
			+ "WHERE (canvas_area_type_id = 1) ORDER BY canvas_area_id";
		BQuery rs = new BQuery(db, mySql);
		
		int steps = 2;
		while(rs.moveNext()) {
			String areaName = rs.getString("canvas_area_name");
			sResp += "<div class='m-wizard__form-step' id='m_wizard_form_step_" + steps + "'>\n"
				+ "	<div class='m-form__section m-form__section--first'>\n"
				+ "		<div class='m-form__heading'>\n"
				+ "			<h3 class='m-form__heading-title'>" + areaName + "</h3>\n"
				+ "		</div>\n"
				+ "		<label class='col-form-label'>" + rs.getString("canvas_area_details") + "</label>\n"
				+ "		<div class='form-group m-form__group row'>\n"
				+ "			<div class='col-xl-9 col-lg-9'>\n"
				+ "				<textarea rows='14' type='text' name='" + areaName.replace(" ", "").trim()
				+ "' id='" + areaName.replace(" ", "_").trim()
				+ "' class='form-control m-input m-textarea' placeholder='' value=''></textarea>\n"
				+ "			</div>\n"
				+ "		</div>\n"
				+ "	</div>\n"
				+ "</div>\n";
			steps++;
		}
		rs.close();

		return sResp;
	}
	
	public void close() {
		if(db != null) db.close();
	}
	
}


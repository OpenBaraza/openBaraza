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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;

import jakarta.servlet.http.HttpServletRequest;

import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.DB.BWebBody;
import org.baraza.DB.BTranslations;
import org.baraza.xml.BElement;
import org.baraza.utils.BWebUtils;
import org.baraza.utils.BJSONUnquoted;

public class BAccordion {
	BDB db;
	BElement view;
	BTranslations translations = null;
	String accordionJs = "";

	public BAccordion(BDB db, BElement view, BTranslations translations) {
		this.db = db;
		this.view = view;
		this.translations = translations;
	} 

	public String getAccordion(HttpServletRequest request, String linkData, String formLinkData, List<String> viewData) {
		String body = "\t<div class='panel-group accordion' id='accordion1'>\n";
		int vds = viewData.size();
		
		accordionJs = "";
		Integer ac = Integer.valueOf("0");
		for(BElement vw : view.getElements()) {
			boolean isDisplay = false;
			if((vw.getName().equals("FORM")) || (vw.getName().equals("GRID"))) isDisplay = true;
			
			if(isDisplay) {
				body += "\t\t<div class='panel panel-default'>\n"
				+ "\t\t\t<div class='panel-heading'>\n"
				+ "\t\t\t\t<h4 class='panel-title'>\n"
				+ "\t\t\t\t\t<a class='accordion-toggle' data-toggle='collapse' data-parent='#accordion1' "
				+ "href='#collapse_" + ac.toString() + "'>" + vw.getAttribute("name") + "</a>\n"
				+ "\t\t\t\t</h4>\n"
				+ "\t\t\t</div>\n"
				+ "\t\t\t<div id='collapse_" + ac.toString() + "' class='panel-collapse " 
				+ vw.getAttribute("collapse", "collapse") + "'>\n"
				+ "\t\t\t\t<div class='panel-body'>\n";
			}
			
			String whereSql = null;
			if(vw.getName().equals("FORM")) {
				if((linkData != null) && (vds > 2)) {
					if("!new!".equals(linkData)) whereSql = vw.getAttribute("keyfield") + " = null";
					else whereSql = vw.getAttribute("keyfield") + " = '" + linkData + "'";
				}
				
				BWebBody webbody = new BWebBody(db, vw, whereSql, null, translations);
				body += webbody.getForm(false, formLinkData, request);
				webbody.close();
			} else if(vw.getName().equals("GRID") && !"!new!".equals(linkData)) {
				if(linkData != null && vw.getAttribute("linkfield") != null) 
					whereSql = vw.getAttribute("linkfield") + " = '" + linkData + "'";
				
				body += "<div class='row'>"
				+ "	<div class='col-md-12 column'>"
				+ "		<div id='sub_table" + ac.toString() + "'></div>"
				+ "	</div>"
				+ "</div>\n";
				
				accordionJs += getGrid(vw, whereSql, ac);
			}
			if(isDisplay) {
				body += "\t\t\t\t</div>\n";
				body += "\t\t\t</div>\n";
				body += "\t\t</div>\n";
			}
			
			ac++;
		}
		body += "\t</div>\n";
		
		return body;
	}
	
	public String getGrid(BElement vw, String whereSql, Integer ac) {
		StringBuilder myhtml = new StringBuilder();
		
		String fieldId = ac.toString();
		
		// JSON data set
		BQuery rs = new BQuery(db, vw, whereSql, null);
		myhtml.append("var db_" + fieldId + "_table = " + rs.getJSON() + ";\n\n");
		rs.close();
		
		JSONObject jDefault = new JSONObject();
		jDefault.put("id", 0);
		
		JSONObject jShd = new JSONObject();
		jShd.put("data", new BJSONUnquoted("db_" + fieldId + "_table"));
		jShd.put("minHeight", vw.getAttribute("th", "120"));
		jShd.put("layout", "fitColumns");
		
		Map<String, String> jsTables = new HashMap<String, String>();
		JSONArray jsColModel = new JSONArray();
		for(BElement el : vw.getElements()) {
			if(BWebUtils.canDisplayField(el.getName())) {
				JSONObject jsColEl = new JSONObject();
				String fld_name = el.getValue();
				String fld_title = el.getAttribute("title", "");
				String fld_size = el.getAttribute("w");
				String fld_type = el.getName();
			
				jsColEl.put("title", fld_title);
				jsColEl.put("field", fld_name);
				if(fld_size != null) jsColEl.put("minWidth", Integer.valueOf(fld_size));
				jsColEl.put("headerSort", false);
				
				if(fld_type.equals("TEXTDATE")) {
					jsColEl.put("formatter", "datetime");
					JSONObject jColFormat = new JSONObject();
					jColFormat.put("inputFormat", "YYYY-MM-DD");
					jColFormat.put("outputFormat", "DD/MM/YYYY");
					jsColEl.put("formatterParams", jColFormat);
				} else if(fld_type.equals("SPINTIME")) {
					jsColEl.put("formatter", "datetime");
					JSONObject jColFormat = new JSONObject();
					jColFormat.put("inputFormat", "hh:mm");
					jColFormat.put("outputFormat", "hh:mm");
					jsColEl.put("formatterParams", jColFormat);
				} else if(fld_type.equals("TEXTAREA")) {
					jsColEl.put("formatter", "textarea");
				} else if(fld_type.equals("CHECKBOX")) {
					jsColEl.put("formatter", "tickCross");
				} else if(fld_type.equals("WEBLINK")) {
					jsColEl.put("formatter", "link");
					JSONObject jColFormat = new JSONObject();
					jColFormat.put("label", el.getAttribute("label", "Click"));
					jsColEl.put("formatterParams", jColFormat);
				} if(fld_type.equals("COMBOBOX")) {
					String whereCmbSql = el.getAttribute("where");
					String whereOrgSql = db.getSqlOrgWhere(el.getAttribute("noorg"));
					String whereUserSql = db.getSqlUserWhere(el.getAttribute("user"));
					if(whereCmbSql != null) whereCmbSql = " WHERE " + whereCmbSql;
					if(whereOrgSql != null && whereUserSql != null) whereOrgSql = whereOrgSql + " AND " + whereUserSql;
					if(whereOrgSql != null) {
						if(whereCmbSql == null) whereCmbSql = " WHERE " + whereOrgSql;
						else whereCmbSql = whereCmbSql + " AND " + whereOrgSql;
					}
					
					boolean extraDetails = false;
					String sql = "SELECT " + el.getAttribute("lpkey", el.getValue()) + " as Id, ";
					if(el.getAttribute("cmb_fnct") == null) sql += el.getAttribute("lpfield") + " as Name ";
					else sql += el.getAttribute("cmb_fnct") + " as Name ";
					for(String attributeName : el.getAttributeNames()) {
						if(attributeName.startsWith("select.")) {
							sql += ", " + el.getAttribute(attributeName);
							extraDetails = true;
						}
					}
					sql += " FROM " + el.getAttribute("lptable");
					if(whereCmbSql != null) sql += whereCmbSql;
					sql += " ORDER BY " + el.getAttribute("orderby", el.getAttribute("lpfield"));
					
					JSONObject jComboData = new JSONObject();
					JSONObject jComboExtra = new JSONObject();
					BQuery rsc = new BQuery(db, sql, false);
					while(rsc.moveNext()) {
						jComboData.put(rsc.getString("Id"), rsc.getString("Name"));
						for(String attributeName : el.getAttributeNames()) {
							if(attributeName.startsWith("select.")) {
								String selectDetail = el.getAttribute(attributeName);
								if(rsc.getString(selectDetail) != null) {
									jComboExtra.put(rsc.getString("Id"), rsc.getString(selectDetail));
								}
							}
						}
					}
					rsc.close();
					
					myhtml.append("var db_" + fieldId + "_" + fld_name + " = " + jComboData.toString() + ";\n");
					if(extraDetails) myhtml.append("var db_e" + fieldId + "_" + fld_name + " = " + jComboExtra.toString() + ";\n");
					
					jsColEl.put("formatter", "lookup");
					jsColEl.put("formatterParams", new BJSONUnquoted("db_" + fieldId + "_" + fld_name));
				}
							
				// Change the formater
				if(el.getAttribute("formatter") != null) jsColEl.put("formatter", el.getAttribute("formatter"));
				
				// Change alignment
				if(el.getAttribute("align") != null) jsColEl.put("align", el.getAttribute("align"));
				
				// footer calculation
				if(el.getAttribute("bottomcalc") != null) {
					jsColEl.put("bottomCalc", el.getAttribute("bottomcalc"));
					if(el.getAttribute("formatter") != null) {
						jsColEl.put("bottomCalcFormatter", el.getAttribute("formatter"));
						JSONObject jColFormater = new JSONObject();
						jColFormater.put("decimal", ".");
						jsColEl.put("bottomCalcFormatterParams", jColFormater);
					}
				}
				
				// add javaScript post edit function
				if(el.getAttribute("jsfnct") != null) {
					jsColEl.put("cellEdited", new BJSONUnquoted(el.getAttribute("jsfnct")));
				}
				
				// Editing options
				if(vw.getAttribute("edit", "true").equals("true")) {
				
					if(el.getAttribute("required") != null) jsColEl.put("validator", "required");
				
					if(el.getAttribute("edit", "true").equals("false")) {
						// No edit allowed
					} else if(el.getAttribute("readonly", "false").equals("true")) {
						// The field is read only
					} else if(fld_type.equals("TEXTFIELD")) {
						jsColEl.put("editor", "input");
					} else if(fld_type.equals("TEXTNUMBER")) {
						jsColEl.put("editor", "input");
					} else if(fld_type.equals("TEXTDECIMAL")) {
						jsColEl.put("editor", "input");
					} else if(fld_type.equals("TEXTDATE")) {
						jsColEl.put("editor", new BJSONUnquoted("dateEditor"));
					} else if(fld_type.equals("SPINTIME")) {
						jsColEl.put("editor", new BJSONUnquoted("timeEditor"));
					} else if(fld_type.equals("TEXTAREA")) {
						jsColEl.put("editor", "textarea");
					} else if(fld_type.equals("CHECKBOX")) {
						jsColEl.put("editor", "tick");
					} else if(fld_type.equals("FUNCTION")) {
					} else if(fld_type.equals("COMBOBOX")) {
						jsColEl.put("editor", "autocomplete");
						JSONObject jColData = new JSONObject();
						jColData.put("values", new BJSONUnquoted("db_" + fieldId + "_" + fld_name));
						jColData.put("showListOnEmpty", true);
						jsColEl.put("editorParams", jColData);
					}
				}

				// default field value
				String defaultValue = el.getAttribute("default");
				String defaultFnct = el.getAttribute("default_fnct");
				String defaultOrgFnct = el.getAttribute("default_org_fnct");
				String defaultUser = el.getAttribute("default_user");
				if(defaultValue != null) {
					jDefault.put(fld_name, defaultValue);
				} else if(defaultFnct != null) {
					if(defaultFnct.indexOf("(") > 1) defaultValue = db.executeFunction("SELECT " + defaultFnct + ", '" + db.getUserID() + "')");
					else defaultValue = db.executeFunction("SELECT " + defaultFnct + "('" + db.getUserID() + "')");
					jDefault.put(fld_name, defaultValue);
				} else if(defaultOrgFnct != null) {
					if(defaultOrgFnct.indexOf("(") > 1) defaultValue = db.executeFunction("SELECT " + defaultOrgFnct + ", " + db.getUserOrg() + ")");
					else defaultValue = db.executeFunction("SELECT " + defaultOrgFnct + "(" + db.getUserOrg() + ")");
					jDefault.put(fld_name, defaultValue);
				} else if(defaultUser != null) {
					defaultValue = db.getUserID();
					jDefault.put(fld_name, defaultValue);
				}
				
				jsColModel.put(jsColEl);
			}
		}

		if(vw.getAttribute("new", "true").equals("true")) {
			JSONObject jsColElKf = new JSONObject();
			jsColElKf.put("headerSort", false);
			jsColElKf.put("formatter", new BJSONUnquoted("cellFnctIcon"));
			jsColElKf.put("align", "center");
			jsColElKf.put("minWidth", 35);
			jsColElKf.put("titleFormatter", new BJSONUnquoted("menuTitleFormatter"));
			jsColElKf.put("cellClick", new BJSONUnquoted("function(e, cell) { controlCellClick(e, cell, " + fieldId + "); }"));
			
			jsColElKf.put("headerClick", new BJSONUnquoted("function(e, column) { headerClick(e, column, db_" + fieldId + "_default); }"));
			
			String del = vw.getAttribute("del");
			if(del == null) del = vw.getAttribute("delete", "true");
			if(del.equals("false")) jsColElKf.put("deleteButton", false);
			
			jsColModel.put(jsColElKf);
		}

		// Add the the columns on the JSON structure
		jShd.put("columns", jsColModel);
		
		jShd.put("rowClick", new BJSONUnquoted("function(e, row){}"));

		// add javaScript post edit cell function
		String jsFnct = vw.getAttribute("jsfnct", "editCell");
		jShd.put("cellEdited", new BJSONUnquoted("function(cell){ " + jsFnct + "(cell, " + fieldId + "); }"));
		
		myhtml.append("var db_" + fieldId + "_default = " + jDefault.toString() + ";\n\n");

		myhtml.append("var tablo" + fieldId + " = new Tabulator('#sub_table" + fieldId + "',\n");
		myhtml.append(jShd.toString() + "\n);");
		
		//System.out.println("BASE 2050 : " + myhtml.toString());

		return myhtml.toString();
	}

	public String getAccordionJs() { return accordionJs; }
}

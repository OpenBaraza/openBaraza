/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.json.JSONArray;
import org.json.JSONObject;

import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.DB.BUser;
import org.baraza.xml.BElement;
import org.baraza.xml.BXML;

public class BAnalyticBody  {

	BElement elAnalytics = null;
	BDB db = null;
	BWebDashboard tiles = null;

	public BAnalyticBody(BWeb web, String viewKey) {
		db = web.getDB();
		BElement root = web.getRoot();

		tiles = new BWebDashboard(db);

		String viewKeys[] = viewKey.split(":");
		elAnalytics = root.getElementByKey(viewKeys[0]);

		//System.out.println(elAnalytics.toString());
	}

	public String getBodyTile() {
		StringBuffer body = new StringBuffer();

		for(BElement el : elAnalytics.getElements()) {
			if(el.getName().equals("TILE")) {
				body.append(tiles.getTile(el));
			}
		}

		return body.toString();
	}

	public String getBodyPie() {
		StringBuffer body = new StringBuffer();

		for(BElement el : elAnalytics.getElements()) {
			if(el.getName().equals("PIECHART")) {
				body.append("<div class='col-lg-3 col-md-3 col-sm-6 col-xs-12'>");
				body.append("	<div class='dashboard-stat2'>");
				body.append("		<div class='donut-title'>" + el.getAttribute("title") + "</div>");
				body.append("		<div id='analytic_pie_" + el.getAttribute("key") + "' class='resize-pie-chart'></div>");
				body.append("	</div>");
				body.append("</div>\n");
			}
		}

		return body.toString();
	}

	public String getBodyLine() {
		StringBuffer body = new StringBuffer();

		for(BElement el : elAnalytics.getElements()) {
			if(el.getName().equals("LINECHART")) {
				if(el.getAttribute("half") == null) body.append("<div class='col-lg-12 col-md-12 col-sm-12 col-xs-12'>");
				else body.append("<div class='col-lg-6 col-md-6 col-sm-6 col-xs-12'>");
				body.append("	<div class='dashboard-stat2'>");
				body.append("		<div class='donut-title'>" + el.getAttribute("title") + "</div>");
				body.append("		<div id='analytic_line_k" + el.getAttribute("key") + "'></div>");
				body.append("	</div>");
				body.append("</div>\n");
			}
		}

		return body.toString();
	}

	public String getBodyJsPie() {
		StringBuffer body = new StringBuffer();

		for(BElement el : elAnalytics.getElements()) {
			if(el.getName().equals("PIECHART")) {
				body.append("pieChart" + el.getAttribute("key") + "();\n");
				body.append("function pieChart" + el.getAttribute("key") + "() {");
				body.append("let pieChartUrl = baseAnalyticsUrl + '" + el.getAttribute("key") + "'; ");
				body.append("pieAnalytics(pieChartUrl, 'analytic_pie_" + el.getAttribute("key") + "', analyticPieColor);");
				body.append("}\n");
			}
		}

		return body.toString();
	}

	public String getBodyJsLine() {
		StringBuffer body = new StringBuffer();

		for(BElement el : elAnalytics.getElements()) {
			if(el.getName().equals("LINECHART")) {
				String chatLabel = "";
				String chatValues = "";
				for(BElement elf : el.getElements()) {
					if(elf.getAttribute("type", "label").equals("value")) {
						chatValues += ", '" + elf.getValue() + "'";
					} else {
						chatLabel = elf.getValue();
					}
				}

				String objId = "analytic_line_k" + el.getAttribute("key");
				String objName = "analytic_line_f" + el.getAttribute("key");

				body.append(objName + "();\n");

				body.append("function " + objName + "(){");
				body.append("	let elementID = '" + objId + "';");
				body.append("	let labels = ['Not Completed','Completed'];");
				body.append("	let ykeys = ['a','b'];");
				body.append("	let xkey = 'y';");
				body.append("	let lineChartUrl = baseAnalyticsUrl + '" + el.getAttribute("key") + "'; ");
				body.append("	get" + objName + "(lineChartUrl, elementID, labels, ykeys, xkey);");
				body.append("}\n");

				body.append("function get" + objName + "(url, elemID, labels, ykeys, xkey){");
				body.append("	let lineSettings = {elemID : elemID, labels : labels, ykeys : ykeys, xkey : xkey,};");
				body.append("	lineChartProcessor('line_data', url, lineSettings, '" + chatLabel + "'" + chatValues + ");");
				body.append("}\n");
			}
		}

		return body.toString();
	}

}

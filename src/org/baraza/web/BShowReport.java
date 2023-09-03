/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.baraza.xml.BElement;
import org.baraza.reports.BWebReport;
import org.baraza.DB.BUser;
import org.baraza.DB.BDB;

public class BShowReport extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		ServletContext context = getServletContext();
		BWeb web = new BWeb(context, request);
		BDB db = web.getDB();
		BUser user = web.getUser();
		
		String ps = System.getProperty("file.separator");
		String reportPath = context.getRealPath("reports") + ps;
		String projectDir = context.getInitParameter("projectDir");
		if(projectDir != null) reportPath = projectDir + ps + "reports" + ps;

		String reportType = request.getParameter("report");
		if(reportType == null) reportType = "pdf";

		BWebReport webReport =  new BWebReport(db, web.getView(), user, request);
		if(reportType.equals("pdf")) webReport.getReport(db, db.getUser(), request, response, 0);
		if(reportType.equals("excel")) webReport.getReport(db, db.getUser(), request, response, 1);
		if(reportType.equals("doc")) webReport.getReport(db, db.getUser(), request, response, 2);
		if(reportType.equals("direct")) {
			BElement view = web.getView();
			String reportName = view.getAttribute("reportfile");
			webReport.getDirectReport(db, db.getUser(), request, response, reportPath, reportName);
		}

		web.close();
	}
}


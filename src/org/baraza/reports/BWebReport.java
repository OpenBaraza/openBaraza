/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.reports;

import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import org.baraza.xml.BElement;
import org.baraza.DB.BDB;
import org.baraza.DB.BUser;

public class BWebReport  {
	Logger log = Logger.getLogger(BWebReport.class.getName());
	String name, reportfile, fileName, filterkey, filtervalue;
	String fileSql = null;
	boolean showpdf = false;
	String userFilter, groupFilter;
	String userId, groupId;
	String linkField;
	String orgTable = null;
	boolean showDoc = false;
	Map<String, Object> parameters;
	BElement actions = null;

	public BWebReport() {
		parameters = new HashMap<String, Object>();
	}

	public BWebReport(BDB db, BElement view, BUser user, HttpServletRequest request) {
		userId = user.getUserID();
		groupId = user.getGroupID();
		String orgId = user.getUserOrg();

		parameters = new HashMap<String, Object>();
		parameters.put("ReportTitle", name);

		name = view.getAttribute("name");
		reportfile = view.getAttribute("reportfile");
		String reportCfg = view.getAttribute("report.cfg");
		fileName = view.getAttribute("file.name", "report");
		fileSql = view.getAttribute("file.sql");
		userFilter = view.getAttribute("user", "entityid");
		groupFilter = view.getAttribute("group");
		filterkey = view.getAttribute("filterkey");
		filtervalue = request.getParameter("filtervalue");
		orgTable = view.getAttribute("org.table");
		showDoc = false;
		if(view.getAttribute("doc", "false").equals("true")) showDoc = true;

		if(reportCfg != null) {
			String rSql = "SELECT get_config_value(" + orgId + "," + reportCfg + ")";
			System.out.println(rSql);
			rSql = db.executeFunction(rSql);
			if(rSql != null) reportfile = rSql;
			System.out.println(reportfile);
		}

		if(view.getElementByName("ACTIONS") != null) actions = view.getElementByName("ACTIONS");

		linkField = view.getAttribute("linkfield", "filterid");
	}

	public String getReport(BDB db, BUser user, String linkValue, HttpServletRequest request, String reportPath, boolean footerButtons) {
		StringBuffer sbuffer = new StringBuffer();
		try {
			File reportFile = new File(reportPath + reportfile);
			if (!reportFile.exists()) {
				System.out.println("Report access error");
				sbuffer.append("REPORT ACCESS ERROR");
				return sbuffer.toString();
			}
			JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());
			
			HttpSession session = request.getSession(true);
			session.setAttribute("reportfile", reportFile.getAbsolutePath());
			session.setAttribute("reportname", name);

			parameters.put("reportpath", reportFile.getParent() + "/");
			parameters.put("SUBREPORT_DIR", reportFile.getParent() + "/");

			parameters.put("orgid", db.getOrgID());
			parameters.put("orgwhere", user.getOrgWhere(orgTable));
			parameters.put("organd", user.getOrgAnd(orgTable));
			parameters.put("deptand", user.getDeptSql(orgTable));
			parameters.put("depland", user.getDeplSql(orgTable));

			if(user.getLogoPath() == null) {
				parameters.put("LOGO_PATH", reportFile.getParent() + "/");
			} else {
				parameters.put("LOGO_PATH", user.getLogoPath());
			}
			
			//System.out.println(user.getDeptSql(orgTable));
			//System.out.println(user.getDeplSql(orgTable));

			session.setAttribute("userfield", "");
			session.setAttribute("groupfield", "");
			if ((userFilter != null) && (userId != null)) {
				log.info(userFilter + " | " + userId);
				parameters.put(userFilter, userId);
				parameters.put("entityname", user.getUserName());
				session.setAttribute("userfield", userFilter);
				session.setAttribute("uservalue", userId);
			}
			if ((groupFilter != null) && (groupId != null)) {
				parameters.put(groupFilter, groupId);
				session.setAttribute("groupfield", groupFilter);
				session.setAttribute("groupvalue", groupId);
			}
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, db.getDB());
			session.setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);

			int pageIndex = 0;
			int lastPageIndex = 0;
			if (jasperPrint.getPages() != null)
				lastPageIndex = jasperPrint.getPages().size() - 1;
	
			if(request.getParameter("page") != null)
				pageIndex = Integer.parseInt(request.getParameter("page"));
			if(request.getParameter("reportmove") != null) {
				String reportmove = request.getParameter("reportmove");
				if(reportmove.equals("<<")) pageIndex = 0;;
				if(reportmove.equals("<")) pageIndex--;
				if(reportmove.equals(">")) pageIndex++;
				if(reportmove.equals(">>")) pageIndex = lastPageIndex;
			}

			if (pageIndex < 0) pageIndex = 0;
			if (pageIndex > lastPageIndex) pageIndex = lastPageIndex;
			
			StringBuffer rbuffer = new StringBuffer();
			
			HtmlExporter exporterHTML = new HtmlExporter();
			exporterHTML.setExporterInput(new SimpleExporterInput(jasperPrint));
			SimpleHtmlExporterOutput exporterOutput = new SimpleHtmlExporterOutput(rbuffer);
			exporterOutput.setImageHandler(new WebHtmlResourceHandler("image?image={0}"));
			exporterHTML.setExporterOutput(exporterOutput);
			
			SimpleHtmlExporterConfiguration exporterConfig = new SimpleHtmlExporterConfiguration();
			exporterConfig.setHtmlHeader("");
			exporterConfig.setHtmlFooter("");
			exporterConfig.setBetweenPagesHtml("");
			exporterHTML.setConfiguration(exporterConfig);

			SimpleHtmlReportConfiguration reportConfig = new SimpleHtmlReportConfiguration();
			reportConfig.setWhitePageBackground(false);
			reportConfig.setPageIndex(Integer.valueOf(pageIndex));
			exporterHTML.setConfiguration(reportConfig);
			
			exporterHTML.exportReport();
			
			sbuffer.append("<div id='reports'>\n");
			sbuffer.append(rbuffer);
			sbuffer.append("\n</div>\n");

			if(footerButtons) {
				sbuffer.append("<div id='reportfooter'>\n");
				sbuffer.append("<table style='width: 597px; border-collapse: collapse'><tr>\n");
				sbuffer.append("<td width='55'><button class='i_pdf_document icon' name='reportexport' type='submit' value='pdf'>Pdf</button></td>\n");
				sbuffer.append("<td width='55'><button class='i_triangle_double_left icon' name='reportmove' type='submit' value='<<'>First</button></td>\n");
				sbuffer.append("<td width='55'><button class='i_triangle_left icon' name='reportmove' type='submit' value='<'>Previous</button></td>\n");
				sbuffer.append("<td width='155'>Page :" + Integer.toString(pageIndex+1) + " of " + Integer.toString(lastPageIndex+1) + "</td>\n");
				sbuffer.append("<input name='page' type='hidden' value='" + Integer.toString(pageIndex) + "'/>\n");

				sbuffer.append("<td width='55'><button class='i_triangle_right icon' name='reportmove' type='submit' value='>'>Next</button></td>\n");
				sbuffer.append("<td width='55'><button class='i_triangle_double_right icon' name='reportmove' type='submit' value='>>'>Last</button></td>\n");
				sbuffer.append("<td width='55'><button class='i_excel_document icon' name='reportexport' type='submit' value='excel'>Excel</button></td>\n");
				if(showDoc) {
					sbuffer.append("<td width='55'><button class='i_doc_document icon' name='reportexport' type='submit' value='doc'>Doc</button></td>\n");
				}
				if((actions != null) && (linkValue != null)) {
					Integer i = 0;
					for(BElement el : actions.getElements()) {
						boolean hasAccess = checkAccess(user, el.getAttribute("role"), el.getAttribute("access"));
						if(hasAccess) {
							sbuffer.append("<td width='55'><button class='i_doc_document icon' name='action_report' type='submit' ");
							sbuffer.append("value='" + i + "'>" + el.getValue() + "</button></td>\n");
							i++;
						}
					}
					session.setAttribute("link_value", linkValue);
				}
				sbuffer.append("</tr><table>");
				sbuffer.append("\n</div>\n");
			}
		} catch (JRException ex) {
			System.out.println("Jasper exception : " + ex);
		}

		return sbuffer.toString();
	}

	public void getReport(BDB db, BUser user, HttpServletRequest request, HttpServletResponse response, int reportType) {
		try {
			HttpSession session = request.getSession(true);
			reportfile = (String)session.getAttribute("reportfile");
			name = (String)session.getAttribute("reportname");

			File reportFile = new File(reportfile);
			if (!reportFile.exists()) {
				log.severe("Report access error : " + reportfile);
				return;
			}
			JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());

			parameters.put("reportpath", reportFile.getParent() + "/");
			parameters.put("SUBREPORT_DIR", reportFile.getParent() + "/");

			if(user.getLogoPath() == null) {
				parameters.put("LOGO_PATH", reportFile.getParent() + "/");
			} else {
				parameters.put("LOGO_PATH", user.getLogoPath());
			}

			parameters.put("orgid", db.getOrgID());
			parameters.put("orgwhere", user.getOrgWhere(orgTable));
			parameters.put("organd", user.getOrgAnd(orgTable));
			parameters.put("entityid", user.getUserID());
			parameters.put("entityname", user.getUserName());
			parameters.put("deptand", user.getDeptSql(orgTable));
			parameters.put("depland", user.getDeplSql(orgTable));

			// set the session parameters
			setParams(session);
		
			String linkField = (String)session.getAttribute("linkfield");
			String linkValue = (String)session.getAttribute("linkvalue");
			if ((linkField != null) && (linkValue != null)) {
				parameters.put(linkField, linkValue);
				log.info(linkField + " | " + linkValue);
			}
			userId = (String)session.getAttribute("uservalue");
			if ((userFilter != null) && (userId != null)) {
				parameters.put(userFilter, userId);
				log.info(userFilter + " | " + userId);
			}
			groupId = (String)session.getAttribute("groupvalue");
			if ((groupFilter != null) && (groupId != null)) {
				parameters.put(groupFilter, groupId);
			}
			
			if((fileSql != null) && (parameters.size() > 0)) {
				fileName = db.executeFunction(fileSql + parameters.get("filterid"));
				if(fileName == null) fileName = "report";
				fileName = fileName.replaceAll(" ", "_");
			}

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, db.getDB());
			if(reportType == 0) {
				response.setCharacterEncoding("ISO-8859-1");
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".pdf");
			
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				exporter.exportReport();
			} else if(reportType == 1) {
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
				
				configuration.setOnePagePerSheet(false);
				configuration.setDetectCellType(true);
				configuration.setCollapseRowSpan(false);
				exporter.setConfiguration(configuration);
				exporter.exportReport();
			} else if(reportType == 2) {
				response.setContentType("application/vnd.ms-word");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".docx");
				
				JRDocxExporter exporter = new JRDocxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				
				SimpleDocxReportConfiguration config = new SimpleDocxReportConfiguration();
				exporter.setConfiguration(config);
				
				exporter.exportReport();
			}
		} catch (JRException ex) {
			log.severe("jasper exception " + ex);
		} catch (IOException ex) {
			log.severe("Web Print Writer Error : " + ex);
		}
	}
	
	public void getAppReport(BDB db, BUser user, HttpServletRequest request, HttpServletResponse response, String reportPath, int reportType) {
		try {
		
			File reportFile = new File(reportPath + reportfile);
			if (!reportFile.exists()) {
				log.severe("Report access error : " + reportfile);
				return;
			}
			JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());

			parameters.put("reportpath", reportFile.getParent() + "/");
			parameters.put("SUBREPORT_DIR", reportFile.getParent() + "/");

			if(user.getLogoPath() == null) {
				parameters.put("LOGO_PATH", reportFile.getParent() + "/");
			} else {
				parameters.put("LOGO_PATH", user.getLogoPath());
			}

			parameters.put("orgid", db.getOrgID());
			parameters.put("orgwhere", user.getOrgWhere(orgTable));
			parameters.put("organd", user.getOrgAnd(orgTable));
			parameters.put(userFilter, user.getUserID());
			parameters.put("entityname", user.getUserName());
			parameters.put("deptand", user.getDeptSql(orgTable));
			parameters.put("depland", user.getDeplSql(orgTable));
		
			String linkData = request.getParameter("linkdata");
			if ((linkField != null) && (linkData != null)) {
				parameters.put(linkField, linkData);
				log.info(linkField + " | " + linkData);
			}
			if (groupFilter != null) {
				parameters.put(groupFilter, user.getGroupID());
			}
			
			if((fileSql != null) && (parameters.size() > 0)) {
				fileName = db.executeFunction(fileSql + parameters.get("filterid"));
				if(fileName == null) fileName = "report";
				fileName = fileName.replaceAll(" ", "_");
			}

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, db.getDB());
			if(reportType == 0) {
				response.setCharacterEncoding("ISO-8859-1");
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".pdf");
			
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				exporter.exportReport();
			} else if(reportType == 1) {
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
				
				configuration.setOnePagePerSheet(false);
				configuration.setDetectCellType(true);
				configuration.setCollapseRowSpan(false);
				exporter.setConfiguration(configuration);
				exporter.exportReport();
			} else if(reportType == 2) {
				response.setContentType("application/vnd.ms-word");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".docx");
				
				JRDocxExporter exporter = new JRDocxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				
				SimpleDocxReportConfiguration config = new SimpleDocxReportConfiguration();
				exporter.setConfiguration(config);
				
				exporter.exportReport();
			}
		} catch (JRException ex) {
			log.severe("jasper exception " + ex);
		} catch (IOException ex) {
			log.severe("Web Print Writer Error : " + ex);
		}
	}

	public void getDirectReport(BDB db, BUser user, HttpServletRequest request, HttpServletResponse response, String reportPath, String reportName) {
		try {
			reportfile = reportPath + reportName;

			File reportFile = new File(reportfile);
			if (!reportFile.exists()) {
				log.info("Report access error : " + reportfile);
				return;
			}
			JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());

			parameters.put("reportpath", reportPath);
			parameters.put("SUBREPORT_DIR", reportPath);

			if(user.getLogoPath() == null) {
				parameters.put("LOGO_PATH", reportFile.getParent() + "/");
			} else {
				parameters.put("LOGO_PATH", user.getLogoPath());
			}

			parameters.put("orgid", db.getOrgID());
			parameters.put("orgwhere", user.getOrgWhere(orgTable));
			parameters.put("organd", user.getOrgAnd(orgTable));
			parameters.put("entityid", user.getUserID());
			parameters.put("entityname", user.getUserName());
			parameters.put("deptand", user.getDeptSql(orgTable));
			parameters.put("depland", user.getDeplSql(orgTable));

			String reportFilters = request.getParameter("reportfilters");
			String reportFilter[] = reportFilters.split(",");
			for(int i = 0; i < reportFilter.length; i++) {
				String filterValue = request.getParameter(reportFilter[i]);
				parameters.put(reportFilter[i], filterValue);
			}
		
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, db.getDB());
			byte[] pdfdata = JasperExportManager.exportReportToPdf(jasperPrint);

			response.setCharacterEncoding("ISO-8859-1");
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=report.pdf");
			response.setContentLength(pdfdata.length);
			response.getOutputStream().write(pdfdata);
			response.getOutputStream().flush();
		} catch (JRException ex) {
			log.severe("jasper exception " + ex);
		} catch (IOException ex) {
			log.severe("Web Print Writer Error : " + ex);
		}
	}
	
	public void	setParams(HttpSession session) {
		if(session.getAttribute("reportfilters") != null) {
			List<String> reportFilters = (List<String>)session.getAttribute("reportfilters");
			for(String reportFilter : reportFilters) {
				String filterValue = (String)session.getAttribute(reportFilter);
				parameters.put(reportFilter, filterValue);
				log.info("Filter = " + reportFilter + " key = " + filterValue);
			}
		}
	}
	
	public void	setParams(String filterName, String filterValue) {
    	parameters.put(filterName, filterValue);
		log.info("Filter = " + filterName + " key = " + filterValue);
	}

	public void	setParams(Map<String, String> params) {
    	parameters.putAll(params);
		log.info("Param filter Done Filter.");
	}
	
	public boolean checkAccess(BUser user, String role, String access) {
		if(user == null) return true;
		boolean hasAccess = user.checkAccess(role, access);
		return hasAccess;
	}
}

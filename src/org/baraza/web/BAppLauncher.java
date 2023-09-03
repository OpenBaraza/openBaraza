/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.io.PrintWriter;
import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.baraza.xml.BElement;

public class BAppLauncher extends HttpServlet {

	String appURL = null;
	String dbURL = null;
	String appDB = null;
	String appScheme = null;
	String encryptionKey = null;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		appURL = config.getInitParameter("app_url");
		appScheme = config.getInitParameter("app_scheme");
		dbURL = config.getInitParameter("db_url");
		appDB = config.getInitParameter("app_db");
		encryptionKey = config.getInitParameter("encryptionkey");
System.out.println("APP DB : " + appURL + " : " + appDB);
    }

	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String jnlp = request.getParameter("jnlp");

		try {
			PrintWriter out = response.getWriter();
			if(jnlp == null) {
				response.setContentType("text/html;charset=\"utf-8\"");
				out.println(getApplet(request));
			} else {
				response.setContentType("application/x-java-jnlp-file");
				response.setHeader("Content-Disposition", "attachment; filename=app.jnlp");
				out.println(getApp(request));
			}
		} catch(IOException ex) {
			System.out.println("ERROR : Cannot get writer from response : " + ex);
		}		
	}

	public String getApplet(HttpServletRequest request) {
		String rurl = request.getRequestURI();
		int locDot = rurl.indexOf(".");
		String appBase = rurl.substring(0, locDot);
		String appMap = rurl.substring(locDot + 1, rurl.length()) + ".xml";
		String scPath = request.getServletPath().replace("/", "");
		
		if(encryptionKey != null) {
			if(encryptionKey.length()>0) appMap = rurl.substring(locDot + 1, rurl.length()) + ".cph";
			else encryptionKey = null;
		}
		
		String localPort = ":" + request.getServerPort();
		if(appScheme != null) localPort = "";
		if(appScheme == null) appScheme = request.getScheme();
		String appPath = appScheme + "://" + request.getLocalAddr() + localPort + appBase + "/projects/";
		String archive = appScheme + "://" + request.getLocalAddr() + localPort + appBase + "/baraza.jar";
		String dbPath = "jdbc:postgresql://" + request.getLocalAddr() + "/" + appDB;
		if(appURL != null) {
			appPath = appScheme + "://" + appURL + localPort + appBase + "/projects/";
			archive = appScheme + "://" + appURL + localPort + appBase + "/baraza.jar";
			dbPath = "jdbc:postgresql://" + dbURL + "/" + appDB;
		}
		String mode = request.getParameter("mode");
		if(mode == null) mode = "run";

		String appStr = "<html>\n";
		appStr += "<head>\n<title>baraza project</title>\n</head>\n";
		appStr += "<body>\n";
		appStr += "	<applet code='org.baraza.Baraza.class' archive='" + archive + "' width='940' height='590'>\n";
		appStr += "		<param name='config' value='" + appPath + "'/>\n";
		appStr += "		<param name='mode' value='" + mode + "'/>\n";
		appStr += "		<param name='dbpath' value='" + dbPath + "'/>\n";
		appStr += "		<param name='configfile' value='" + appMap + "'/>\n";
		if(encryptionKey != null) appStr += "		<param name='encryptionkey' value='" + encryptionKey + "'/>\n";
		appStr += "		<param name='permissions' value='all-permissions'/>\n";
		appStr += "	</applet>\n";
		appStr += "	<hr width='100%'>\n";
		appStr += "	<a href='" + scPath + "?jnlp=yes'>launch the application</a>\n";
		appStr += "	<p><a href='http://www.openbaraza.org' target='_blank'>An open baraza project</a> |\n";
		appStr += "	<b><a href='http://www.dewcis.com' target='_blank'>Developed by Dew CIS Solutions Ltd - kenya</a></b></p>\n";
		appStr += "</body>\n";
		appStr += "</html>\n";

		return appStr;
	}

public String getApp(HttpServletRequest request) {
		String rurl = request.getRequestURI();
		int locDot = rurl.indexOf(".");
		String appBase = rurl.substring(0, locDot);
		String appMap = rurl.substring(locDot + 1, rurl.length()) + ".xml";

		if(encryptionKey != null) {
			if(encryptionKey.length()>0) appMap = rurl.substring(locDot + 1, rurl.length()) + ".cph";
			else encryptionKey = null;
		}

		String localPort = ":" + request.getServerPort();
		if(appScheme != null) localPort = "";
		String codeBase = "http://" + request.getLocalAddr() + localPort + appBase;
		String appPath = codeBase + "/projects/";
		String dbPath = "jdbc:postgresql://" + request.getLocalAddr() + "/" + appDB;
		if(appURL != null) {
			if(appScheme == null) appScheme = request.getScheme();
			codeBase = appScheme + "://" + appURL + localPort + appBase;
			appPath = codeBase + "/projects/";
			dbPath = "jdbc:postgresql://" + dbURL + "/" + appDB;
		}
		String mode = request.getParameter("mode");
		if(mode == null) mode = "run";

		String appStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		appStr += "<jnlp codebase=\"" + codeBase + "\">\n";
		appStr += "	<information>\n";
		appStr += "		<title>Baraza Project</title>\n";
		appStr += "		<vendor>Dew Cis Solutions Ltd</vendor>\n";
		appStr += "		<description>The Dew Cis Solutions, Baraza Project</description>\n";
		appStr += "		<icon href=\"resources/baraza_logo.jpg\"/>\n";
		appStr += "	</information>\n";

		appStr += "	<resources>\n";
		appStr += "		<j2se version=\"1.7+\" initial-heap-size=\"64m\" max-heap-size=\"128m\"/>\n";
		appStr += "		<jar href=\"baraza.jar\"/>\n";
		appStr += "		<jar href=\"lib/activation-1.1.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/barbecue-1.5-beta1.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-beanutils-1.9.4.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-collections-3.2.2.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-collections4-4.2.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-codec-1.13.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-digester-2.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-logging-1.1.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-lang-2.6.jar\"/>\n";
		appStr += "		<jar href=\"lib/groovy-all-2.4.16.jar\"/>\n";
		appStr += "		<jar href=\"lib/itext-2.1.7.js8.jar\"/>\n";
		appStr += "		<jar href=\"lib/jasperreports-6.16.0.jar\"/>\n";
		appStr += "		<jar href=\"lib/jasperreports-fonts-6.16.0.jar\"/>\n";
		appStr += "		<jar href=\"lib/jasperreports-metadata-6.16.0.jar\"/>\n";
		appStr += "		<jar href=\"lib/jcommon-1.0.23.jar\"/>\n";
		appStr += "		<jar href=\"lib/jfreechart-1.0.19.jar\"/>\n";
		appStr += "		<jar href=\"lib/java-jwt-3.3.0.jar\"/>\n";
		appStr += "		<jar href=\"lib/mail.jar\"/>\n";
		appStr += "		<jar href=\"lib/poi-4.1.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/postgresql-42.2.18.jar\"/>\n";
		appStr += "		<jar href=\"lib/server/tomcat-embed-core.jar\"/>\n";
		appStr += "	</resources>\n";

		appStr += "	<application-desc main-class=\"org.baraza.Baraza\">\n";
		appStr += "		<argument>" + mode + "</argument>\n";
		appStr += "		<argument>" + appPath + "</argument>\n";
		appStr += "		<argument>" + dbPath + "</argument>\n";
		appStr += "		<argument>" + appMap + "</argument>\n";
		if(encryptionKey != null) appStr += "<argument>" + encryptionKey + "</argument>\n";
		appStr += "	</application-desc>\n";
		appStr += "	<security>\n";
		appStr += "		<all-permissions/>\n";
		appStr += "	</security>\n";
		appStr += "</jnlp>\n";

		return appStr;
	}

	public String getApp2(HttpServletRequest request) {
		String rurl = request.getRequestURI();
		int locDot = rurl.indexOf(".");
		String appBase = rurl.substring(0, locDot);
		String appMap = rurl.substring(locDot + 1, rurl.length()) + ".xml";
		
		if(encryptionKey != null) {
			if(encryptionKey.length()>0) appMap = rurl.substring(locDot + 1, rurl.length()) + ".cph";
			else encryptionKey = null;
		}

		String localPort = ":" + request.getServerPort();
		if(appScheme != null) localPort = "";
		String codeBase = "http://" + request.getLocalAddr() + localPort + appBase; 
		String appPath = codeBase + "/projects/"; 
		String dbPath = "jdbc:postgresql://" + request.getLocalAddr() + "/" + appDB;
		if(appURL != null) {
			if(appScheme == null) appScheme = request.getScheme();
			codeBase = appScheme + "://" + appURL + localPort + appBase; 
			appPath = codeBase + "/projects/"; 
			dbPath = "jdbc:postgresql://" + dbURL + "/" + appDB;
		}
		String mode = request.getParameter("mode");
		if(mode == null) mode = "run";

		String appStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		appStr += "<jnlp codebase=\"" + codeBase + "\">\n";
		appStr += "	<information>\n";
		appStr += "		<title>Baraza Project</title>\n";
		appStr += "		<vendor>Dew Cis Solutions Ltd</vendor>\n";
		appStr += "		<description>The Dew Cis Solutions, Baraza Project</description>\n";
		appStr += "		<icon href=\"resources/baraza_logo.jpg\"/>\n";
		appStr += "	</information>\n";

		appStr += "	<resources>\n";
		appStr += "		<j2se version=\"1.7+\" initial-heap-size=\"64m\" max-heap-size=\"128m\"/>\n";
		appStr += "		<jar href=\"baraza.jar\"/>\n";
		appStr += "		<jar href=\"lib/javax.activation-1.2.0.jar\"/>\n";
		appStr += "		<jar href=\"lib/barbecue-1.5-beta1.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-beanutils-1.9.4.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-collections-3.2.2.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-collections4-4.2.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-codec-1.13.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-digester-2.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-logging-1.1.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-lang-2.6.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-math3-3.6.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/commons-math-1.0.jar\"/>\n";
		appStr += "		<jar href=\"lib/groovy-3.0.7.jar\"/>\n";
		appStr += "		<jar href=\"lib/itext-2.1.7.js9.jar\"/>\n";
		appStr += "		<jar href=\"lib/jasperreports-6.18.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/jasperreports-fonts-6.18.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/jasperreports-metadata-6.18.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/jcommon-1.0.23.jar\"/>\n";
		appStr += "		<jar href=\"lib/jfreechart-1.0.19.jar\"/>\n";
		appStr += "		<jar href=\"lib/java-jwt-3.19.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/javax.mail-1.6.2.jar\"/>\n";
		appStr += "		<jar href=\"lib/poi-4.1.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/poi-ooxml-4.1.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/poi-ooxml-schemas-4.1.1.jar\"/>\n";
		appStr += "		<jar href=\"lib/postgresql-42.4.0.jar\"/>\n";
		appStr += "		<jar href=\"lib/server/tomcat-embed-core.jar\"/>\n";
		appStr += "	</resources>\n";

		appStr += "	<application-desc main-class=\"org.baraza.Baraza\">\n";
		appStr += "		<argument>" + mode + "</argument>\n";
		appStr += "		<argument>" + appPath + "</argument>\n";
		appStr += "		<argument>" + dbPath + "</argument>\n";
		appStr += "		<argument>" + appMap + "</argument>\n";
		if(encryptionKey != null) appStr += "		<argument>" + encryptionKey + "</argument>\n";
		appStr += "	</application-desc>\n";
		appStr += "	<security>\n";
		appStr += "		<all-permissions/>\n";
		appStr += "	</security>\n";
		appStr += "</jnlp>\n";

		return appStr;
	}

}

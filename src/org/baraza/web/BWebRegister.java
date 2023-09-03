/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;

import org.baraza.DB.BDB;
import org.baraza.DB.BTranslations;

public class BWebRegister extends HttpServlet {

	BDB db = null;
	BTranslations translations;
 
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		System.out.println("Baraza loading initialization parameters ..... ");

		ServletContext servletContext = getServletContext();
		
		String dbconfig = "java:/comp/env/jdbc/database";
		db = new BDB(dbconfig);

		translations = new BTranslations(db);

		servletContext.setAttribute("translations", translations);
	}
	
	public void destroy() {
		db.close();
	}

}


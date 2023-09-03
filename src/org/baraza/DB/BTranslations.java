/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.DB;

import java.util.Map;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;

public class BTranslations {

	BDB db = null;
	Map<Integer, String> languages;
	Map<Integer, Map<Integer, Map<String, String>>> orgLang;

	public BTranslations(BDB db) {
		this.db = db;
		loadTranslations();
	}
	
	public void loadTranslations() {
		languages = new HashMap<Integer, String>();
		orgLang = new HashMap<Integer, Map<Integer, Map<String, String>>>();
		
		String sql = "SELECT sys_language_id, sys_language_name FROM sys_languages ORDER BY sys_language_id";
		BQuery rsL = new BQuery(db, sql);
		while (rsL.moveNext()) languages.put(rsL.getInt("sys_language_id"), rsL.getString("sys_language_name"));
		rsL.close();
		
		sql = "SELECT org_id FROM orgs WHERE is_active = true ORDER BY org_id";
		BQuery rsQ = new BQuery(db, sql);
		while (rsQ.moveNext()) {
			Integer orgId = rsQ.getInt("org_id");
			Map<Integer, Map<String, String>> langs = new HashMap<Integer, Map<String, String>>();
			for(Integer langId : languages.keySet()) {
				Map<String, String> lang = new HashMap<String, String>();
				sql = "SELECT reference, title FROM sys_translations "
					+ "WHERE sys_language_id = " + langId.toString() + " AND org_id = " + orgId;
				BQuery rsT = new BQuery(db, sql);
				while (rsT.moveNext()) lang.put(rsT.getString("reference"), rsT.getString("title"));
				langs.put(langId, lang);
				rsT.close();
			}
			orgLang.put(orgId, langs);
		}
		rsQ.close();
	}
	
	public String getTitle(Integer orgId, Integer languageId, String reference, String altTitle) {
		if(orgId == null) return altTitle;
		if(languageId == null) return altTitle;
		
		// check if org exists the reload the page
		if(orgLang.get(orgId) == null) loadTranslations();
		
		String title = orgLang.get(orgId).get(languageId).get(reference);
		if(title == null) title = altTitle;
		return title;
	}
	
	public Map<Integer, String> getLanguages() { return languages; }

}


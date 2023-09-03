/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.DB;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;
import java.sql.Types;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.commons.text.StringEscapeUtils;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.xml.BXML;
import org.baraza.xml.BElement;

public class BJSONQuery extends BQuery {

	boolean selectAll = false;

	public BJSONQuery(BDB db, BElement view, String wheresql, String orderby, Integer pageStart, Integer pageSize) {
		super(db, view, wheresql, orderby, false, pageStart, pageSize);
	}

	public String getJSONData(String viewKey, boolean sfield) {
		JSONArray myJa = new JSONArray();

		String dispStr = "";
		String colWidths = null;
		String groupTable = null;

		boolean hasAction = false;
		boolean hasSubs = false;
		boolean hasTitle = false;
		boolean hasFilter = false;
		int colNums = 0;

		String filterName = view.getAttribute("filter", "filterid");

		for(BElement el : view.getElements()) {
			if(el.getName().equals("ACTIONS")) hasAction = true;
			if(el.getName().equals("GRID") || el.getName().equals("FORM") || el.getName().equals("JASPER")) hasSubs = true;
			if(el.getName().equals("FILES") || el.getName().equals("DIARY") || el.getName().equals("CROSSTAB")) hasSubs = true;
			if(el.getName().equals("DIARYEDIT")) hasSubs = true;
			if(el.getName().equals("COLFIELD") || el.getName().equals("TITLEFIELD")) hasTitle = true;
			if(el.getName().equals("FILTERGRID")) hasFilter = true;
		}

		try {
			rs.beforeFirst();
			int row = 0;
			boolean plain = false;
			String titlefield = "";

			String[] colspanfield = new String[colNums];
			for(int k=0; k<colNums; k++) colspanfield[k] = "";

			while (rs.next()) {
				JSONObject myJo = new JSONObject();
				String mydn = "";
				String mydv = "";

				row++;
				int col = 0;

				dispStr = "";
				for(BElement el : view.getElements()) {
					mydn = el.getValue();//"C" + String.valueOf(col);

					if(!el.getValue().equals(""))  {
						String cellData = formatData(el);
						if(el.getAttribute("raw") == null) cellData = StringEscapeUtils.escapeHtml4(cellData);
						
						if(sfield) dispStr += ", " + cellData;
						if (el.getName().equals("COLFIELD")) {
							myJo.put(mydn, cellData);
						} else if(el.getName().equals("TITLEFIELD")) {
							myJo.put(mydn, cellData);
						} else if(el.getName().equals("EDITFIELD")) {
							myJo.put(mydn, cellData);
						} else if(el.getName().equals("ACTION")) {
							String myAction = el.getAttribute("action");
							if(myAction == null) myAction = el.getAttribute("title");

							mydv = "<input type='hidden' name='actionkey' value='" + cellData + "'/>\n";
							mydv += "<button type='submit' name='actionprocess' value='" + cellData + "' class='i_cog icon small'/>\n";
							mydv += myAction + "</button>";
							myJo.put(mydn, mydv);
						} else if(el.getName().equals("SEARCH")) {
							String js = el.getAttribute("js", "updateForm");

							mydv = "<input type='button' VALUE='Select' ";
							mydv += "onClick=\"" + js + "('" + getString(keyField) + "', '";
							mydv += cellData + "')\">";
							myJo.put(mydn, mydv);
						} else if(el.getName().equals("WEBDAV")) {
							mydv = "<a href='webdavfiles?view=" + viewKey + "&filename=" + cellData;
							mydv += "' target='_blank'>View</a>";
							myJo.put(mydn, mydv);
						} else if(el.getName().equals("PICTURE")) {
							String mypic = getString(el.getValue());
							mydv = "";
							if(mypic != null) {
								mydv = "<div><img src='";
								mydv += el.getAttribute("pictures") + "?access=" + el.getAttribute("access");
								mydv += "&picture=" + mypic + "'></div>\n";
							}
							myJo.put(mydn, mydv);
						} else if(el.getAttribute("details", "false").equals("true")){
							mydv = "<a href='?view=" + viewKey + ":" + getSelectKey() + "&data=" + rs.getString(keyField) + "' ";
							if(el.getAttribute("hint") != null) mydv = " title='" + el.getAttribute("hint") +  "'";
							mydv += ">" + cellData + "</a>";

							myJo.put(mydn, mydv);
						} else if(el.getName().equals("BROWSER")) {
							if(el.getAttribute("path") != null) mydv = "<a href='" + el.getAttribute("path");
							else mydv += "<a href='form.jsp";
							mydv += "?action=" +  el.getAttribute("action");
							mydv += "&actionvalue=" + cellData;

							if(el.getAttribute("disabled") != null) mydv += "&disabled=yes";
							if(el.getAttribute("blankpage") != null) mydv += "&blankpage=yes' target='_blank'";
							else mydv += "'";

							if(el.getAttribute("hint") != null) mydv += " title='" + getString(el.getAttribute("hint")) +  "'";
							mydv += "><img src='assets/images/form.png'></a>";
							myJo.put(mydn, mydv);
						} else if(el.getName().equals("WEBLINK")) {
							mydv = "<a href='#";
							if(el.getAttribute("weblink") != null) mydv = "<a href='" + rs.getString(el.getAttribute("weblink"));
							else if(el.getAttribute("path") != null) mydv = "<a href='" + el.getAttribute("path");

							if(el.getAttribute("data") != null) mydv += "?data=" + rs.getString(el.getAttribute("data"));

							if(el.getAttribute("class") != null) mydv += "' class='" + el.getAttribute("class");

							if(el.getAttribute("blankpage") == null) mydv += "'>";
							else mydv += "' target='_blank'>";

							mydv += cellData + "</a>";
							myJo.put(mydn, mydv);
						} else if(el.getName().equals("BUTTON") && (keyField != null)) {
							String jsFunction = el.getAttribute("js.function", "");
							mydv = "<button onclick=" + jsFunction + "('" + rs.getString(keyField) + "') type='button'>";
							mydv += cellData + "</button>";
							myJo.put(mydn, mydv);
						} else if(el.getName().equals("TEXTLINK")) {
							String sk = getSelectKey();
							mydv = cellData;
							if(sk != null) {
								mydv = "<a href='?view=" + viewKey + ":" + sk + "&data=" + rs.getString(keyField) + "'>";
								mydv += cellData + "</a>";
							}
							myJo.put(mydn, mydv);
						} else {
							myJo.put(mydn, cellData);
						}
						col++;
					}
				}

				if(keyField != null) {
					String sk = getSelectKey();

					if(sk != null) {
						mydv = "?view=" + viewKey + ":" + sk + "&data=" + rs.getString(keyField);
						if(hasFilter) mydv += "&gridfilter=true";
						mydn = "CL";
						myJo.put(mydn, mydv);
					}
				}

				if(keyField != null) {
					myJo.put("KF", rs.getString(keyField));
				}

				if(view.getName().equals("FILES")) {
					mydv = "";
					if((view.getAttribute("edit", "true").equals("true"))) {
						mydv += "<a href='delbarazafiles?view=" + viewKey + "&fileid=" + getString(keyField);
						mydv += "' onclick=\"return confirm('Are you sure you want delete the file?');\"";
						mydv += " target='_blank'>Delete</a>";
						mydn = "C" + String.valueOf(col++);
						myJo.put(mydn, mydv);
					}
					mydv += "\n<a href='barazafiles?view=" + viewKey + "&fileid=" + getString(keyField);
					mydv += "' target='_blank'>View</a>";
					mydn = "C" + String.valueOf(col++);
					myJo.put(mydn, mydv);
				}

				myJa.put(myJo);

				if((tableLimit > 0) && (tableLimit < row)) break;
			}
		} catch(SQLException ex) {
			log.severe("Web data body reading error : " + ex);
		}

		return myJa.toString();
	}

	public String getSelectKey() {

		Integer i = 0;
		for(BElement sview : view.getElements()) {
			String sviewName = sview.getName();
			if(sviewName.equals("ACCORDION") || sviewName.equals("DIARY") || sviewName.equals("DIARYEDIT") || sviewName.equals("FILES") || sviewName.equals("FORM") || sviewName.equals("GRID") || sviewName.equals("JASPER") || sviewName.equals("FORMVIEW") || sviewName.equals("CROSSTAB")) {
				String viewFilter = sview.getAttribute("viewfilter");

				if(viewFilter == null) {
					return i.toString();
				} else {
					String viewFilters[] = viewFilter.split(",");
					boolean show = true;
					for(String vfs : viewFilters) {
						String vsp[] = vfs.split("=");
						if(!vsp[1].equals(getString(vsp[0]))) show = false;
					}
					if(show) return i.toString();
				}
				i++;
			}
		}

		return null;
	}

	public void setSelectAll() {
		selectAll = true;
	}

}

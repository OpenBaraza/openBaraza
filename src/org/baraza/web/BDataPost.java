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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.IOException;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.xml.BXML;
import org.baraza.xml.BElement;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.DB.BUser;
import org.baraza.utils.BWebUtils;
import org.baraza.utils.Bio;
import org.baraza.utils.BWebdav;

@MultipartConfig(fileSizeThreshold = 1024 * 1024,
	maxFileSize = 1024 * 1024 * 5,
	maxRequestSize = 1024 * 1024 * 5 * 5)
public class BDataPost extends HttpServlet {
	Logger log = Logger.getLogger(BDataPost.class.getName());

	BDB db = null;
	BUser user = null;
	BElement root = null;
	BElement view = null;
	HttpSession webSession = null;

	List<BElement> views;
	List<String> viewKeys;
	List<String> viewData;
	Map<String, String> params;

	String viewKey = null;
	String dataItem = null;
	String userID = null;
	Integer orgId = null;
	Integer languageId = null;
	String saveMsg = "";

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		String dbConfig = "java:/comp/env/jdbc/database";
		db = new BDB(dbConfig);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		ServletContext context = getServletContext();
		init(request, context);

		System.out.println("Delete function ");

		JSONObject jResp = new JSONObject();
		jResp = deleteForm(request);

		try {
			response.setContentType("application/json;charset=\"utf-8\"");
			PrintWriter out = response.getWriter();
			out.println(jResp.toString());
		} catch(IOException ex) {}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		ServletContext context = getServletContext();
		init(request, context);

		JSONObject jResp = new JSONObject();
		
		//System.out.println("BASE ContentType " + request.getContentType());
		//BWebUtils.showParameters(request);
		
		if(isMultipartContent(request)) {
			jResp = updateMultiPart(request, context);
		} else {
			jResp = updateForm(request);
		}

		try {
			response.setContentType("application/json;charset=\"utf-8\"");
			PrintWriter out = response.getWriter();
			out.println(jResp.toString());
		} catch(IOException ex) {}
	}

	public void init(HttpServletRequest request, ServletContext context) {
		BXML xml = new BXML(context, request, false);
		if(xml.getDocument() == null) {
		} else {
			root = xml.getRoot();
			db.setOrgID(root.getAttribute("org"));
			BElement configs = root.getElementByName("CONFIGS");
			if(configs != null) {
				BElement audit = configs.getElementByName("AUDIT");
				if(audit != null) db.setFullAudit(audit);
			}
		}
		if(!db.isValid()) {
			db.reconnect("java:/comp/env/jdbc/database");
			db.setOrgID(root.getAttribute("org"));
		}

		// Get web session
		webSession = request.getSession(true);

		// login the user
		setUser(request.getRemoteAddr(), request.getRemoteUser());

		views = new ArrayList<BElement>();
		viewKeys = new ArrayList<String>();
		viewData = new ArrayList<String>();
		params = new HashMap<String, String>();

		dataItem = request.getParameter("data");
		if(BWebUtils.checkInjection(dataItem)) dataItem = "";

		viewKey = request.getParameter("view");
		if((viewKey == null) && (webSession.getAttribute("viewkey") != null))
			viewKey = (String)webSession.getAttribute("viewkey");
//System.out.println("BASE VIEWKEY : " + viewKey);

		String sv[] = viewKey.split(":");
		for(String svs : sv) viewKeys.add(svs);
		views.add(root.getElementByKey(sv[0]));
		viewData.add("");
	
		String dk = viewKeys.get(0) + ":";
		for(int i = 1; i < sv.length; i++) {
			dk += viewKeys.get(i);
			String sItems = (String)webSession.getAttribute("d" + dk);
			if(sItems == null) sItems = "";
//System.out.println("BASE sItems : " + sItems);

			int subNo = Integer.valueOf(sv[i]);
			views.add(views.get(i-1).getSub(subNo));

			viewData.add(sItems);
			dk += ":";
		}
		view = views.get(views.size() - 1);
	}

	public void setUser(String userIP, String userName) {

		if(db != null) {
			if(userName == null) {
				user = new BUser(db, userIP, "root", true);
			} else if(root.getAttribute("auth.table") != null) {
				String authTable = root.getAttribute("auth.table");
				String authId = root.getAttribute("auth.id");
				String authName = root.getAttribute("auth.name");
				user = new BUser(db, userIP, userName, true);
				user.setUser(db, authTable, authId, authName, userName);
			} else {
				user = new BUser(db, userIP, userName);
			}

			if(user != null) {
				userID = user.getUserID();
				orgId = user.getUserOrgId();
				languageId = user.getLanguageId();
			}
		}
	}

	public boolean isMultipartContent(HttpServletRequest request) {
		boolean multiPartContent = false;
		if (request.getContentType() != null) {
			if(request.getContentType().toLowerCase().indexOf("multipart/form-data") > -1 ) {
				multiPartContent = true;
			}
		}
		return multiPartContent;
	}

	public JSONObject updateMultiPart(HttpServletRequest request, ServletContext context) {
		JSONObject jResp = new JSONObject();
		
		try {
			System.out.println("BASE multipart update");
			Map<String, String[]> reqParams = new HashMap<String, String[]>();
			reqParams.putAll(request.getParameterMap());

//			System.out.println("BASE Parts " + request.getParts().size());
//			System.out.println("BASE Params " + reqParams.size());

			for (Part part : request.getParts()) {
//				System.out.println("BASE Part " + part.getName() + " : " + part.getSize());
				if((part.getContentType() != null) && (part.getSize() > 0)) {
//					System.out.println("BASE Part " + part.getName());
					String pictureFile = savePicture(part, context);
					if(pictureFile != null) {
						String[] pArray = new String[1];
						pArray[0] = pictureFile;
						reqParams.put(part.getName(), pArray);
					}
				}
			}

			jResp = updateFormData(request, reqParams);
		} catch(IOException ex) {
			log.severe("IO Error : " + ex);
		} catch(ServletException ex) {
			log.severe("IO Error : " + ex);
		}
		
		return jResp;
	}

	public String savePicture(Part part, ServletContext context) {
		String pictureFile = null;

		String repository = context.getInitParameter("repository_url");
		String username = context.getInitParameter("rep_username");
		String password = context.getInitParameter("rep_password");
System.out.println("repository : " + repository);
		BWebdav webdav = new BWebdav(repository, username, password);

		String contentType = part.getContentType();
		String fieldName = part.getName();
		String fileName = part.getSubmittedFileName();
		long fs = part.getSize();

		long maxfs = 4194304;
		if(view != null) {
			BElement el = view.getElement(fieldName);
			if(el != null) maxfs = (Long.valueOf(el.getAttribute("maxfilesize", "4194304"))).longValue();
		}

		String ext = null;
		int i = fileName.lastIndexOf('.');
		if(i>0 && i<fileName.length()-1) ext = fileName.substring(i+1).toLowerCase();
		if(ext == null) ext = "NAI";
		String pictureName = db.executeFunction("SELECT nextval('picture_id_seq')") + "pic." + ext;

		try {
			String[] imageTypes = {"BMP", "GIF", "JFIF", "JPEG", "JPG", "PNG", "TIF", "TIFF"};
			ext = ext.toUpperCase().trim();

			if(Arrays.binarySearch(imageTypes, ext) >= 0) {
				if(fs < maxfs) {
					webdav.saveFile(part.getInputStream(), pictureName);
					pictureFile = pictureName;
				}
			}
		}  catch(IOException ex) {
			log.severe("File saving failed Exception " + ex);
		}

		return pictureFile;
	}

	public JSONObject updateForm(HttpServletRequest request) {
		System.out.println("BASE parameter form update");
		Map<String, String[]> reqParams = new HashMap<String, String[]>();
		reqParams.putAll(request.getParameterMap());
		
		System.out.println("BASE Params " + reqParams.size());
		
		JSONObject jResp = updateFormData(request, reqParams);
		
		return jResp;
	}

	public JSONObject updateFormData(HttpServletRequest request, Map<String, String[]> reqParams) {
		String linkData = null;
		String formlink = null;
		int vds = viewKeys.size();
		saveMsg = "";
		JSONObject jResp = new JSONObject();

		if(vds > 2) {
			linkData = viewData.get(vds - 1);
			if(linkData.equals("!new!")) formlink = view.getAttribute("keyfield") + " = null";
			else formlink = view.getAttribute("keyfield") + " = '" + linkData + "'";
		}

		if(view.getName().equals("FORM")) {
			BQuery qForm = new BQuery(db, view, formlink, null, user, false);
			if(view.getAttribute("foredit") != null) {
				qForm.movePos(1);
				qForm.recEdit();
			} else if(vds < 3) {
				qForm.recAdd();
			} else if(linkData.equals("!new!")) {
				qForm.recAdd();
				if(view.getAttribute("linkfield") != null) {
					qForm.updateField(view.getAttribute("linkfield"), viewData.get(vds - 2));

					System.out.println("BASE 1012 " + viewData.get(vds - 2));
				}
			} else {
				qForm.movePos(1);
				qForm.recEdit();
			}

			Map<String, String> inputParams = new HashMap<String, String>();
			if(view.getAttribute("inputparams") != null) {
				String paramArr[] = view.getAttribute("inputparams").toLowerCase().split(",");
				for(String param : paramArr) {
					String pItems[] = param.split("=");
					if(pItems.length == 2) {
						qForm.updateField(pItems[0].trim(), params.get(pItems[1].trim()));
						//System.out.println("BASE 1010 " + pItems[0].trim() + " : " + params.get(pItems[1].trim()));
					}
				}
			}

			saveMsg = qForm.updateFields(reqParams, viewData, request.getRemoteAddr(), linkData);

			if("".equals(saveMsg)) {
				String jumpView = view.getAttribute("jumpview");
				BElement fView = view.getElementByName("FORMVIEW");
				dataItem = qForm.getKeyField();
				viewData.set(vds - 1, dataItem);

				// Create an allowance to excecute a function after new or updateCombo
				String postFnct = view.getAttribute("post_fnct");
				if(postFnct != null) {
					String upsql = "SELECT " + postFnct + "('" + dataItem + "') ";
					if(db.getDBType()==2) upsql += " FROM dual";
					db.executeQuery(upsql);
				}

				saveMsg = view.getAttribute("save.msg", "The record has been updated.");
				jResp.put("success", true);
				jResp.put("msg", saveMsg);
				if(jumpView != null) {
					viewKey = jumpView;
					String mydv = "?view=" + viewKey + "&data=" + dataItem;
					
					jResp.put("jump", true);
					jResp.put("jumpview", viewKey);
					jResp.put("jumplink", mydv);
					
					webSession.setAttribute("viewkey", jumpView);
					webSession.setAttribute("loadviewkey", jumpView);
					webSession.setAttribute("loaddata", dataItem);
				} else if(fView != null) {
					view = fView;
					views.add(fView);
					viewData.add(dataItem);
					viewKeys.add("0");
					viewKey += ":0";
					
					String mydv = "?view=" + viewKey + "&data=" + dataItem;
		
					jResp.put("jump", true);
					jResp.put("jumpview", viewKey);
					jResp.put("jumplink", mydv);
				} else {
					jResp.put("jump", false);
					if(vds > 2) {
						dataItem = viewData.get(vds - 2);
						view = views.get(vds - 2);

						views.remove(vds - 1);
						viewData.remove(vds - 1);
						viewKeys.remove(vds - 1);

						viewKey = viewKey.substring(0, viewKey.lastIndexOf(":"));
						webSession.setAttribute("viewkey", viewKey);
						String mydv = "?view=" + viewKey + "&data=" + dataItem;
						
						jResp.put("jump", true);
						jResp.put("jumpview", viewKey);
						jResp.put("jumplink", mydv);
					}
				}
			} else {
				jResp.put("success", false);
				jResp.put("msg", saveMsg);
			}
			qForm.close();
		} else if(view.getName().equals("ACCORDION")) {
			BElement accdView = view.getElement(0);
System.out.println("Reached ACCORDION " + vds + " : " + formlink);

			BQuery qAccd = new BQuery(db, accdView, formlink, null, user, false);

			if(vds < 2) {
				qAccd.recAdd();
			} else if(linkData == null) {
				if(qAccd.moveFirst()) qAccd.recEdit();
			} else if(linkData.equals("!new!")) {
				qAccd.recAdd();
				if(accdView.getAttribute("linkfield") != null)
					qAccd.updateField(accdView.getAttribute("linkfield"), viewData.get(vds - 2));
			} else {
				if(qAccd.moveFirst()) qAccd.recEdit();
			}

			saveMsg = qAccd.updateFields(reqParams, viewData, request.getRemoteAddr(), linkData);
			if("".equals(saveMsg)) {
				dataItem = qAccd.getKeyField();
				viewData.set(vds - 1, dataItem);
				String mydv = "?view=" + viewKey + "&data=" + dataItem;
				saveMsg = view.getAttribute("save.msg", "The record has been updated.");

				jResp.put("success", true);
				jResp.put("msg", saveMsg);
				jResp.put("jump", true);
				jResp.put("jumpview", viewKey);
				jResp.put("jumplink", mydv);
			} else {
				jResp.put("success", false);
				jResp.put("msg", saveMsg);
			}
			
			// Set the jump point
			dataItem = qAccd.getKeyField();
			viewData.set(vds - 1, dataItem);
			webSession.setAttribute("loaddata", dataItem);

			qAccd.close();
		}
		
		return jResp;
	}

	public JSONObject deleteForm(HttpServletRequest request) {
		JSONObject jResp = new JSONObject();

		String linkData = null;
		String formlink = null;
		int vds = viewKeys.size();
		saveMsg = "";

		BElement fView = view;
		if(view.getName().equals("ACCORDION")) fView = view.getElementByName("FORM");
		if(fView == null) {
			jResp.put("success", false);
			jResp.put("msg", "No VIEW");
			return jResp;
		}

		if(vds > 2) {
			linkData = viewData.get(vds - 1);
			if(!linkData.equals("!new!"))
				formlink = fView.getAttribute("keyfield") + " = '" + linkData + "'";
		}

		if(fView.getName().equals("FORM") && (!linkData.equals("!new!"))) {
			BQuery qForm = new BQuery(db, fView, formlink, null);
			qForm.movePos(1);
			qForm.recDelete();
			qForm.close();

			if(vds > 2) {
				dataItem = viewData.get(vds - 2);
				view = views.get(vds - 2);

				views.remove(vds - 1);
				viewData.remove(vds - 1);
				viewKeys.remove(vds - 1);

				viewKey = viewKey.substring(0, viewKey.lastIndexOf(":"));
				webSession.setAttribute("viewkey", viewKey);
				String mydv = "?view=" + viewKey + "&data=" + dataItem;

				jResp.put("jump", true);
				jResp.put("jumpview", viewKey);
				jResp.put("jumplink", mydv);
			} else {
				dataItem = "!new!";
			}

			jResp.put("success", true);
			jResp.put("msg", "Record deleted");
		} else {
			jResp.put("success", true);
			jResp.put("msg", "No record deleted");
		}

		return jResp;
	}

}


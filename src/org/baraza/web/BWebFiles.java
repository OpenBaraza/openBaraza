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
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.xml.BElement;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.DB.BImportVector;
import org.baraza.utils.BWebUtils;
import org.baraza.utils.BWebdav;

@MultipartConfig(fileSizeThreshold = 1024 * 1024,
	maxFileSize = 1024 * 1024 * 5,
	maxRequestSize = 1024 * 1024 * 5 * 5)
public class BWebFiles extends HttpServlet {
	Logger log = Logger.getLogger(BWebFiles.class.getName());
	
	BWeb web = null;
	BWebdav webdav = null;
	BElement view = null;
	BDB db = null;
	BQuery query = null;
	String fileTable = null;
	String linkField = null;
	String linkValue = null;
	String userField = null;
	String folder = null;
	long maxfs = 0;

	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		log.info("Start File handler");
		
		String sp = request.getServletPath();
		configure(request);
		
		log.info("Configured");
		
		String resp = null;
		if(view.getName().equals("GRID")) {
			resp = importFile(request);
		} if(view.getName().equals("FILES")) {
			if(sp.equals("/barazafiles")) getFile(request, response);
			if(sp.equals("/webdavfiles")) getWebDavFile(request, response);
			if(sp.equals("/putbarazafiles")) resp = receiveFile(request);
			if(sp.equals("/delbarazafiles")) delFile(request, response);
		}
		
		log.info("Selected");
		
		if(resp != null) {
			response.setContentType("application/json;charset=\"utf-8\"");
			try { 
				PrintWriter out = response.getWriter();
				out.println(resp);
			} catch(IOException ex) {}
		}
			
		// Close initialisations
		query.close();
		web.close();
	}

	public void configure(HttpServletRequest request) {
		ServletContext context = getServletContext();
		web = new BWeb(context, request);
		
		String webPath = request.getScheme() + "://" + request.getLocalAddr() + ":" + request.getLocalPort();
System.out.println("BASE localport : " + request.getLocalPort());
System.out.println("BASE serverport : " + request.getServerPort());

		String repositoryBase = context.getInitParameter("repository_base");
		if(repositoryBase != null) webPath = repositoryBase;

		String repositoryFolder = context.getInitParameter("repository_folder");
		if(repositoryFolder == null) repositoryFolder = "";
		else repositoryFolder +=  "/";

		String username = context.getInitParameter("rep_username");
		String password = context.getInitParameter("rep_password");

		view = web.getView();
System.out.println("BASE 1020 : " + view);
		maxfs = (Long.valueOf(view.getAttribute("maxfilesize", "16777216"))).longValue(); 
		fileTable = view.getAttribute("filetable");
		linkField = view.getAttribute("linkfield");
		userField = view.getAttribute("user");
		linkValue = web.getDataItem();

		if(view.getName().equals("FILES")) {
			String repository = webPath + view.getAttribute("repository") + repositoryFolder;
			folder = view.getAttribute("folder");

			if(username == null) username = view.getAttribute("username");
			if(password == null) password = view.getAttribute("rep_password");
			if(repository != null) webdav = new BWebdav(repository, username, password);

System.out.println("BASE 1020 : " + repository);
		}

		db = web.getDB();
		query = new BQuery(db, view, null, null, false);

		System.out.println(linkValue);
	}
	
	
	public String importFile(HttpServletRequest request) {
		String resp = "{\"success\": 0, \"message\": \"Upload Failed\"}";
		JSONObject jShd = new JSONObject();
		
		int yourMaxMemorySize = 262144;
		
		ServletContext sc = getServletContext();
		String tmpPath = sc.getRealPath("/WEB-INF/tmp");
		File yourTempDirectory = new File(tmpPath);
		String fileName = null;

		try {
			String orgID = db.getOrgID();
			String userOrg = db.getUserOrg();
			
			Map<String, String> inParams = new HashMap<String, String>();
			Map<String,String[]> paramMap = request.getParameterMap();
			for(String paramName : paramMap.keySet()) inParams.put(paramName, paramMap.get(paramName)[0]);

			for (Part part : request.getParts()) {
				if((part.getContentType() != null) && (part.getSize() > 0)) {
					String contentType = part.getContentType();
					String fieldName = part.getName();
					fileName = BWebUtils.getFileName(part.getHeader("content-disposition"));
					long fs = part.getSize();
					
					if(fs < maxfs) {
System.out.println("BASE IMPORT 1410 : " + fileName);
						String importType = view.getAttribute("import");
						BImportVector iv = new BImportVector(view);
						
						if(importType.equals("excel")) {
							String worksheet = view.getAttribute("worksheet", "0");
							Integer firstRow = Integer.valueOf(view.getAttribute("firstrow", "0"));
							iv.getExcelData(part.getInputStream(), fileName, worksheet, firstRow);
						} else if(importType.equals("text")) {
							iv.getTextData(part.getInputStream());
						} else if(importType.equals("record")) {
							iv.getRecordData(part.getInputStream());
						}
						query.importData(iv.getData(), inParams);
						iv.close();
						
						jShd.put("success", 1);
						jShd.put("name", fileName);
						jShd.put("size", part.getSize());
						jShd.put("message", "Process File");
						resp = jShd.toString();
					}
				}
			}
		} catch(ServletException ex) {
			log.severe("IO Error : " + ex);
		} catch(IOException ex) {
			log.severe("File saving failed IO Exception " + ex);
		}  catch(Exception ex) {
			log.severe("File saving failed Exception " + ex);
		}
		
System.out.println("BASE IMPORT 1420 : " + resp);

		return resp;
	}

	public String receiveFile(HttpServletRequest request) {
		String resp = "{\"success\": 0, \"message\": \"Upload Failed\"}";

		int yourMaxMemorySize = 262144;

		try {
			String narrative = request.getParameter("narrative");
			for (Part part : request.getParts()) {
				String contentType = part.getContentType();
				String fieldName = part.getName();
				String fileName = BWebUtils.getFileName(part.getHeader("content-disposition"));
				long fs = part.getSize();

				if((part.getContentType() != null) && (part.getSize() > 0) && (fs < maxfs)) {
System.out.println("BASE 1410 : " + fileName);
					String orgID = db.getOrgID();
					String userOrg = db.getUserOrg();
					String userID = db.getUserID();
System.out.println("BASE 1420 : " + orgID + " : " + userOrg + " : " + userID);
System.out.println("BASE 1405 : " + narrative);
					query.recAdd();
					query.updateField("file_name", fileName);
					if(linkField != null) query.updateField(linkField, linkValue);
					if(userField != null) query.updateField(userField, userID);
					if(fileTable != null) query.updateField("table_name", fileTable);
					if(contentType != null) query.updateField("file_type", contentType);
					if(narrative != null) query.updateField("narrative", narrative);
					if(orgID != null) query.updateField(orgID, userOrg);
					query.updateField("file_size", String.valueOf(fs));
System.out.println("BASE 1415 : ");

					String ext = ".dat";
					if(fileName.lastIndexOf(".") > 0) ext = fileName.substring(fileName.lastIndexOf("."));
					if(ext == null) ext = ".dat";
					if(ext.equals(".jsp")) return resp;

					query.recSave();
					String folder_name = "";
					if(folder != null) {
						folder_name = query.getString(folder) + "/";
System.out.println("BASE 1420 : " + folder_name);
						if(!webdav.fileExists(folder_name)) webdav.createDir(folder_name);
						webdav.listDir(folder_name);
					}
					String wdfn = folder_name + query.getKeyField() + "ob" + ext;

System.out.println("BASE 1440 : " + wdfn);

					webdav.saveFile(part.getInputStream(), wdfn);
				}
			}
			resp = "{\"success\": 1, ";
			if(narrative != null) resp += "\"narrative\": \"" + narrative + "\", ";
			resp += "\"message\": \"Upload Sucessful\"}";
		} catch(IOException ex) {
			log.severe("IO Error : " + ex);
		} catch(ServletException ex) {
			log.severe("IO Error : " + ex);
		}  catch(Exception ex) {
			log.severe("File saving failed Exception " + ex);
		}

		return resp;
	}

	public void getFile(HttpServletRequest request, HttpServletResponse response) {
		String wdfn = request.getParameter("fileid");

		if(wdfn != null) {
			query.filter(query.getKeyFieldName() + " = '" + wdfn + "'", null);
			query.moveFirst();
			String folder_name = "";
			if(folder != null) 	folder_name = query.getString(folder) + "/";
			String fileName = query.readField("file_name");
			String ext = ".dat";
			if(fileName.lastIndexOf(".")>0) ext = fileName.substring(fileName.lastIndexOf("."));
			if(ext == null) ext = ".dat";
			wdfn = folder_name + wdfn + "ob" + ext;
System.out.println("BASE 1520 : " + wdfn);

			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			if(webdav.fileExists(wdfn)) {
				try {
					InputStream in = webdav.getFile(wdfn);
					OutputStream out = response.getOutputStream();

					int size = in.available();
					byte[] content = new byte[1024];
					int c;
					while ((c = in.read(content)) != -1) out.write(content, 0, c);

					out.close();
					in.close();
				} catch (IOException ex) {
					System.out.println("IO Error : " + ex);
				}
			}
		}
	}

	public void getWebDavFile(HttpServletRequest request, HttpServletResponse response) {
		String wdfn = request.getParameter("filename");

		if(wdfn != null) {
			System.out.println("BASE 1010 : " + wdfn);

			int lifn = wdfn.lastIndexOf("/");
			String repository = wdfn.substring(0, lifn - 1);
			String fileName = wdfn.substring(lifn, wdfn.length());
System.out.println("Repository : "  + repository);
System.out.println("File : "  + fileName);
			String username = view.getAttribute("username");
			String password = view.getAttribute("password");
			webdav = new BWebdav(repository, username, password);

			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			try {
				InputStream in = webdav.getFile(wdfn);
				OutputStream out = response.getOutputStream();

				int size = in.available();
				byte[] content = new byte[size];
				int c;
				while ((c = in.read(content)) != -1) out.write(content);

				out.close();
				in.close();
			} catch (IOException ex) {
				System.out.println("IO Error : " + ex);
			}
		}
	}

	public String delFile(HttpServletRequest request, HttpServletResponse response) {
		String resp = "{\"success\": 0, \"message\": \"Delete Failed\"}";
		
		String wdfn = request.getParameter("fileid");
		System.out.println("BASE 1010 : " + wdfn);

		if(wdfn != null) {
			query.filter(query.getKeyFieldName() + " = '" + wdfn + "'", null);
			query.moveFirst();
			String folder_name = "";
			if(folder != null) 	folder_name = query.getString(folder) + "/";
			String fileName = query.readField("file_name");
			String ext = ".dat";
			if(fileName.lastIndexOf(".")>0) ext = fileName.substring(fileName.lastIndexOf("."));
			if(ext == null) ext = ".dat";
			wdfn = folder_name + wdfn + "ob" + ext;

			query.recDelete();
			webdav.delFile(wdfn);
			
			resp = "{\"success\": 1, \"message\": \"Delete Sucessful\"}";
		}
		
		try {
			String referer = request.getHeader("Referer");
System.out.println("Referer : "  + referer);
			response.sendRedirect(referer);
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}
		
		return resp;
	}

}


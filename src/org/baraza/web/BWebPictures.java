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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONArray;

import jakarta.servlet.http.Part;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;

import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;
import org.baraza.utils.BWebUtils;
import org.baraza.utils.BWebdav;

public class BWebPictures extends HttpServlet {
	Logger log = Logger.getLogger(BWebPictures.class.getName());
	BDB db = null;
	BWebdav webdav = null;
	String photo_access;

	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String dbconfig = "java:/comp/env/jdbc/database";
		db = new BDB(dbconfig);

		ServletContext config = this.getServletContext();
		photo_access = config.getInitParameter("photo_access");
		if(photo_access == null) photo_access = "";
		String repository = config.getInitParameter("repository_url");
		String username = config.getInitParameter("rep_username");
		String password = config.getInitParameter("rep_password");
//System.out.println("repository : " + repository);
		webdav = new BWebdav(repository, username, password);
		
		/* These URL used as
			barazapictures?access=ob&picture=11pic.jpeg
			delbarazapictures?access=ob&picture=11pic.jpeg
			putbarazapictures		Multipart post with photo segment, you get photo name in json
		*/
		
		String sp = request.getServletPath();
		if(sp.equals("/barazapictures")) showPhoto(request, response);
		if(sp.equals("/delbarazapictures")) delPhoto(request, response);
		if(sp.equals("/putbarazapictures")) putPictures(request, response);

		db.close();
	}

	public void showPhoto(HttpServletRequest request, HttpServletResponse response) {
		String pictureFile = request.getParameter("picture");
		String access = request.getParameter("access");
//System.out.println("Picture : " + pictureFile + " " + access);
		InputStream in = webdav.getFile(pictureFile);

		int dot = pictureFile.lastIndexOf(".");
        String ext = pictureFile.substring(dot + 1);

		if((photo_access.equals(access)) && (in != null)) {
			try {
				response.setContentType("image/" + ext);  
				OutputStream out = response.getOutputStream();

				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int c = 0;
				while ((c = in.read(buffer)) != -1) out.write(buffer, 0, c);

				in.close();
				out.flush(); 
			} catch(IOException ex) {
				log.severe("IO Error : " + ex);
			}
		}
	}

	public void delPhoto(HttpServletRequest request, HttpServletResponse response) {
		String pictureFile = request.getParameter("picture");
		String access = request.getParameter("access");

		if(photo_access.equals(access))
			webdav.delFile(pictureFile);
	}
	
	public void putPictures(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("Add a baraza picture");
		JSONObject jResp =  new JSONObject();
		
		ServletContext context = this.getServletContext();
		String ps = System.getProperty("file.separator");
		String tmpPath = context.getRealPath("WEB-INF" + ps + "tmp");

		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String,String[]> paramMap = request.getParameterMap();
		for(String paramName : paramMap.keySet()) reqParams.put(paramName, paramMap.get(paramName)[0]);

		try {
			for (Part part : request.getParts()) {
				if((part.getContentType() != null) && (part.getSize() > 0)) {
					String pictureFile = savePicture(part);
					if(pictureFile != null) {
						reqParams.put(part.getName(), pictureFile);
						jResp.put("picture_name", pictureFile);
						jResp.put("error", false);
					} else {
						jResp.put("error", true);
					}
				}
			}
		} catch(IOException ex) {
			log.severe("IO Error : " + ex);
		} catch(ServletException ex) {
			log.severe("IO Error : " + ex);
		}
		
		try {
			PrintWriter out = response.getWriter(); 
			out.println(jResp.toString());
		} catch(IOException ex) {}
		
	}
	
	public String savePicture(Part part) {
		String pictureFile = null;

		String contentType = part.getContentType();
		String fieldName = part.getName();
		String fileName = BWebUtils.getFileName(part.getHeader("content-disposition"));
		long fs = part.getSize();

		long maxfs = 4194304;

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


}

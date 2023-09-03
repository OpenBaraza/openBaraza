/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.utils;

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import jakarta.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.DB.BDB;
import org.baraza.DB.BUser;
import org.baraza.DB.BTranslations;
import org.baraza.xml.BElement;

public class BWebUtils {
	static Logger log = Logger.getLogger(BWebUtils.class.getName());

	public static void showHeaders(HttpServletRequest request) {
		System.out.println("HEADERS ------- ");
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			System.out.println(headerName);
			request.getHeader(headerName);
			Enumeration<String> headers = request.getHeaders(headerName);
			while (headers.hasMoreElements()) {
				String headerValue = headers.nextElement();
				System.out.println("\t" + headerValue);
			}
		}
		System.out.print("\n");
	}

	public static void showParameters(HttpServletRequest request) {
		System.out.println("PARAMETERS ------- ");
		Enumeration en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String paramName = (String)en.nextElement();
			System.out.println(paramName + " : " + request.getParameter(paramName));
		}
		System.out.print("\n");
	}

	public static String requestBody(HttpServletRequest request) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) jb.append(line);
		} catch (IOException ex) {
			log.severe("IO Error reading body  : " + ex);
		}
		return jb.toString();
	}

	public static String getFileName(String htmlHeader) {
		for (String content : htmlHeader.split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf("=") + 2, content.length() - 1);
			}
		}
		return "tmpfile";
	}
	
	public static String createToken(String tokenKey, String userId, Integer orgId) {
		String token = null;
		try {
			Algorithm algorithm = Algorithm.HMAC256(tokenKey);
			token = JWT.create().withIssuer("auth0")
				.withSubject(userId)
				.withClaim("orgId", orgId.toString())
				.sign(algorithm);
		} catch (JWTCreationException ex){
			log.severe("JWTCreationException : " + ex);
		}
		
		return token;
	}
	
	public static String decodeToken(String tokenKey, String token) {
		String payLoad = null;
		try {
			Algorithm algorithm = Algorithm.HMAC256(tokenKey);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); 
			DecodedJWT jwt = verifier.verify(token);
			payLoad = jwt.getSubject();
		} catch (JWTVerificationException ex){
			log.severe("JWTVerificationException : " + ex);
		}
		return payLoad;
	}
	
	public static String decodeTokenOrg(String tokenKey, String token, String claimName) {
		String payLoad = null;
		try {
			Algorithm algorithm = Algorithm.HMAC256(tokenKey);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); 
			DecodedJWT jwt = verifier.verify(token);
			if (jwt.getClaims().containsKey(claimName)) payLoad = jwt.getClaims().get(claimName).asString();
		} catch (JWTVerificationException ex){
			log.severe("JWTVerificationException : " + ex);
		}
		return payLoad;
	}
	
	public static Map<String, String> decodeTokenMap(String tokenKey, String token) {
		Map<String, String> payLoad = new HashMap<String, String>();
		try {
			Algorithm algorithm = Algorithm.HMAC256(tokenKey);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); 
			DecodedJWT jwt = verifier.verify(token);
			
			payLoad.put("userId", jwt.getSubject());
			for(String claimName : jwt.getClaims().keySet())
				payLoad.put(claimName, jwt.getClaim(claimName).asString());
		} catch (JWTVerificationException ex){
			log.severe("JWTVerificationException : " + ex);
		}
		return payLoad;
	}

	public static int getViewType(String viewTypeName) {
		int type = 0;
		if(viewTypeName == null) type = 0;
		else if(viewTypeName.equals("ACCORDION")) type = 1;
		else if(viewTypeName.equals("CROSSTAB")) type = 2;
		else if(viewTypeName.equals("DASHBOARD")) type = 3;
		else if(viewTypeName.equals("DIARY")) type = 4;
		else if(viewTypeName.equals("FILES")) type = 5;
		else if(viewTypeName.equals("FILTER")) type = 6;
		else if(viewTypeName.equals("FORM")) type = 7;
		else if(viewTypeName.equals("FORMVIEW")) type = 8;
		else if(viewTypeName.equals("GRID")) type = 9;
		else if(viewTypeName.equals("JASPER")) type = 10;
		else if(viewTypeName.equals("TABLEVIEW")) type = 11;
		else if(viewTypeName.equals("DIARYEDIT")) type = 12;
		return type;
	}

	
	public static int getFieldType(String fieldTypeName) {
		int type = 0;
		if(fieldTypeName == null) type = 0;
		else if(fieldTypeName.equals("TEXTFIELD")) type = 0;
		else if(fieldTypeName.equals("TEXTAREA")) type = 1;
		else if(fieldTypeName.equals("CHECKBOX")) type = 2;
		else if(fieldTypeName.equals("TEXTTIME")) type = 3;
		else if(fieldTypeName.equals("TEXTDATE")) type = 4;
		else if(fieldTypeName.equals("TEXTTIMESTAMP")) type = 5;
		else if(fieldTypeName.equals("SPINTIME")) type = 6;
		else if(fieldTypeName.equals("SPINDATE")) type = 7;
		else if(fieldTypeName.equals("SPINTIMESTAMP")) type = 8;
		else if(fieldTypeName.equals("TEXTDECIMAL")) type = 9;
		else if(fieldTypeName.equals("COMBOLIST")) type = 10;
		else if(fieldTypeName.equals("COMBOBOX")) type = 11;
		else if(fieldTypeName.equals("GRIDBOX")) type = 12;
		else if(fieldTypeName.equals("DEFAULT")) type = 13;			//  No display
		else if(fieldTypeName.equals("EDITOR")) type = 14;
		else if(fieldTypeName.equals("FUNCTION")) type = 15;		//  No display
		else if(fieldTypeName.equals("USERFIELD")) type = 16;		//  No display
		else if(fieldTypeName.equals("USERNAME")) type = 17;		//  No display
		else if(fieldTypeName.equals("PICTURE")) type = 18;
		else if(fieldTypeName.equals("LOCATION")) type = 19;		//  No display
		else if(fieldTypeName.equals("PHONE")) type = 20;			//  No display
		else if(fieldTypeName.equals("ACTION")) type = 21;
		else if(fieldTypeName.equals("BARCODE")) type = 22;
		return type;
	}

	public static boolean canDisplayField(String fieldTypeName) {
		boolean canDisplay = true;
		if(fieldTypeName == null) canDisplay = false;
		else if(fieldTypeName.equals("DEFAULT")) canDisplay = false;	//  No display
		else if(fieldTypeName.equals("USERFIELD")) canDisplay = false;	//  No display
		else if(fieldTypeName.equals("USERNAME")) canDisplay = false;	//  No display
		else if(fieldTypeName.equals("LOCATION")) canDisplay = false;	//  No display
		else if(fieldTypeName.equals("PHONE")) canDisplay = false;		//  No display
		else if(fieldTypeName.equals("FORMVIEW")) canDisplay = false;	//  No display
		else if(fieldTypeName.equals("FORM")) canDisplay = false;		//  No display
		return canDisplay;
	}
	
	public static boolean checkInjection(String filterValue) {
		if(filterValue == null) return false;
		if(filterValue.toLowerCase().contains("select")) return true;
		if(filterValue.toLowerCase().contains("update")) return true;
		if(filterValue.toLowerCase().contains("insert")) return true;
		if(filterValue.toLowerCase().contains("delete")) return true;
		return false;
	}

	public static String comboboxSQL(BElement el, BUser user, String orgID, String formLinkData) {
		String lptable = el.getAttribute("lptable");
		String lpfield = el.getAttribute("lpfield");
		String lpkey = el.getAttribute("lpkey");
		String cmb_fnct = el.getAttribute("cmb_fnct");
		if(lpkey == null) lpkey = el.getValue();
		
		String userOrg = null;
		if(user != null) userOrg = user.getUserOrg();

		String mysql = "";
		if(lpkey.equals(lpfield)) mysql = "SELECT " + lpfield;
		else if (cmb_fnct == null) mysql = "SELECT " + lpkey + ", " + lpfield;
		else mysql = "SELECT " + lpkey + ", (" + cmb_fnct + ") as " + lpfield;
		for(String attributeName : el.getAttributeNames()) {
			if(attributeName.startsWith("select.")) mysql += ", " + el.getAttribute(attributeName);
		}
		mysql += " FROM " + lptable;
		
		String cmbWhereSql = el.getAttribute("where");
		if((el.getAttribute("noorg") == null) && (orgID != null) && (userOrg != null)) {
			if(cmbWhereSql == null) cmbWhereSql = "(";
			else cmbWhereSql += " AND (";
			
			if(el.getAttribute("org.id") == null) cmbWhereSql += orgID + "=" + userOrg + ")";
			else cmbWhereSql += el.getAttribute("org.id") + "=" + userOrg + ")";
		}

		if(el.getAttribute("user") != null) {
			String userFilter = "(" + el.getAttribute("user") + " = '" + user.getUserID() + "')";
			if(cmbWhereSql == null) cmbWhereSql = userFilter;
			else cmbWhereSql += " AND " + userFilter;
		}

		String tableFilter = null;
		String linkField = el.getAttribute("linkfield");
		if((linkField != null) && (formLinkData != null)) {
			if(el.getAttribute("linkfnct") == null) tableFilter = linkField + " = '" + formLinkData + "'";
			else tableFilter = linkField + " = " + el.getAttribute("linkfnct") + "('" + formLinkData + "')";

			if(cmbWhereSql == null) cmbWhereSql = "(" + tableFilter + ")";
			else cmbWhereSql += " AND (" + tableFilter + ")";
		}

		if(cmbWhereSql != null) mysql += " WHERE " + cmbWhereSql;

		String orderBySql = el.getAttribute("orderby");
		if(orderBySql == null) mysql += " ORDER BY " + lpfield;
		else mysql += " ORDER BY " + orderBySql;
		
		return mysql;
	}
	
	public static String getJSONHeader(BElement elGrid, BTranslations translations, BUser user, String viewKey, String linkData) {
		return 	getJSONHeader(elGrid, translations, user.getUserOrgId(), user.getLanguageId(), viewKey, linkData);
	}
	
	public static String getJSONHeader(BElement elGrid, BTranslations translations, Integer orgId, Integer languageId, String viewKey, String linkData) {
		JSONObject jShd = new JSONObject();
		JSONArray jsColNames = new JSONArray();
		JSONArray jsColModel = new JSONArray();

		boolean hasAction = false;
		boolean hasSubs = false;
		boolean hasTitle = false;
		boolean hasFilter = false;
		int col = 0;
		for(BElement el : elGrid.getElements()) {
			if(!el.getValue().equals("")) {
				JSONObject jsColEl = new JSONObject();
				String mydn = el.getValue();
				
				String fieldTitle = el.getAttribute("title", "");
				if(el.getAttribute("lang") != null) {
					fieldTitle = translations.getTitle(orgId, languageId, el.getAttribute("lang"), fieldTitle);
				} else if(languageId > 0) { //if the language is not english
					fieldTitle = translations.getTitle(orgId, languageId, el.getValue(), fieldTitle);
				}
				
				if(!el.getValue().equals("")) jsColNames.put(fieldTitle);
				jsColEl.put("name", mydn);
				jsColEl.put("width", Integer.valueOf(el.getAttribute("w", "50")));
				if(el.getName().equals("EDITFIELD")) {
					jsColEl.put("editable", true);
					
					if(el.getAttribute("edittype") != null) jsColEl.put("edittype", el.getAttribute("edittype"));
					if(el.getAttribute("editoptions") != null) {
						JSONObject jsColElVal = new JSONObject();
						jsColElVal.put("value", el.getAttribute("editoptions"));
						jsColEl.put("editoptions", jsColElVal);
					}
				}
				jsColModel.put(jsColEl);
			}
			
			if(el.getName().equals("ACTIONS")) hasAction = true;
			if(el.getName().equals("GRID") || el.getName().equals("FORM") || el.getName().equals("JASPER")) hasSubs = true;
			if(el.getName().equals("ACCORDION") || el.getName().equals("FILES") || el.getName().equals("DIARY")) hasSubs = true;
			if(el.getName().equals("DIARYEDIT")) hasSubs = true;
			if(el.getName().equals("COLFIELD") || el.getName().equals("TITLEFIELD")) hasTitle = true;
			if(el.getName().equals("FILTERGRID")) hasFilter = true;
		}
		
		JSONObject jsColEl = new JSONObject();
		jsColNames.put("CL");
		jsColEl.put("name", "CL");
		jsColEl.put("width", 5);
		jsColEl.put("hidden", true);
		jsColModel.put(jsColEl);
		
		JSONObject jsColKF = new JSONObject();
		jsColNames.put("KF");
		jsColKF.put("name", "KF");
		jsColKF.put("width", 5);
		jsColKF.put("hidden", true);
		jsColModel.put(jsColKF);
		
		String jUrl = elGrid.getAttribute("url", "jsondata");
		if(viewKey != null) {
			jUrl += "?view=" + viewKey;
			if(linkData != null) jUrl += "&linkdata=" + linkData;
		}
		
		jShd.put("url", jUrl);
		jShd.put("datatype", "json");
		jShd.put("mtype", "GET");
		jShd.put("colNames", jsColNames);
		jShd.put("colModel", jsColModel);
		jShd.put("pager", "#jqpager");
		jShd.put("viewrecords", true);
		jShd.put("gridview", true);
		jShd.put("autoencode", true);
		jShd.put("autowidth", true);
		jShd.put("sortable", true);
		if(elGrid.getAttribute("ssort") == null) jShd.put("loadonce", true);
		else jShd.put("loadonce", false);
		
		//System.out.println("BASE 2030 : " + jShd.toString());

		return jShd.toString();
	}
	
	public static String authenticate(String myURL, String appKey, String appPass) {
		String auth = null;
		if(appKey == null || appPass == null) return auth;
		
		try {
			String authUser = Base64.getEncoder().encodeToString(appKey.getBytes("UTF-8"));
			String authPass = Base64.getEncoder().encodeToString(appPass.getBytes("UTF-8"));

			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
				.url(myURL)
				.get()
				.addHeader("action", "authorization")
				.addHeader("authUser", authUser)
				.addHeader("authPass", authPass)
				.addHeader("cache-control", "no-cache")
				.build();
			Response response = client.newCall(request).execute();
			String rBody = response.body().string();
			System.out.println("Resp : " + rBody);
			
			JSONObject jResp = new JSONObject(rBody);
			int ResultCode = jResp.getInt("ResultCode");
			if(jResp.has("ResultCode") && (jResp.getInt("ResultCode") == 0)) {
				auth = jResp.getString("access_token");
			}
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return auth;
	}

	public static String sendData(String myURL, String auth, String action, String data) {
		String resp = null;
		
		try {			
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(data, mediaType);
			Request request = new Request.Builder()
				.url(myURL)
				.post(body)
				.addHeader("action", action)
				.addHeader("authorization", auth)
				.addHeader("content-type", "application/json")
				.build();
			Response response = client.newCall(request).execute();
			resp = response.body().string();
		} catch(IOException ex) {
			System.out.println("IO Error : " + ex);
		}

		return resp;
	}
}

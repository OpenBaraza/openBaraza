/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.xml;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.baraza.utils.Bio;

public class BXML {
	Logger log = Logger.getLogger(BXML.class.getName());
	String xmlFile = null;
	BElement root = null;

	public BXML(String xml, boolean isText) {
		initXML(xml, isText);
	}
	
	public BXML(InputStream inXml) {
		initXML(inXml);
	}
	
	public BXML(ServletContext context, HttpServletRequest request, boolean setSession) {
		initXML(context, request, setSession);
	}
	
	public void initXML(String xml, boolean isText) {
		try {
			// initialise Specifications from a Local Property file
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = null;
			if(isText) {
				InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
				document = builder.parse(in);
			} else {
				document = builder.parse(xml);
				xmlFile = xml;
			}
			root = new BElement(document);
		} catch (SAXParseException ex) {
			log.severe("XML Error : " + ex.getMessage());
		} catch (ParserConfigurationException ex) {
			log.severe("File IO error : " + ex);
		} catch (SAXException ex) {
			log.severe("File IO error : " + ex);
		} catch (IOException ex) {
			log.severe("File IO error : " + ex);
		} catch(Exception ex) {
			log.severe("File createtion error : " + ex);
		}
	}

	public void initXML(InputStream inXml) {
        try {
			// initialise Specifications from a Local Property file
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inXml);
			root = new BElement(document);
		} catch (SAXParseException ex) {
			log.severe("XML Error : " + ex.getMessage());
		} catch (ParserConfigurationException ex) {
			log.severe("Inputsteam IO error : " + ex);
		} catch (SAXException ex) {
			log.severe("Inputsteam IO error : " + ex);
		} catch (IOException ex) {
			log.severe("Inputsteam IO error : " + ex);
		} catch(Exception ex) {
			log.severe("Inputsteam createtion error : " + ex);
		}
	}
	
	public void initXML(ServletContext context, HttpServletRequest request, boolean setSession) {
		HttpSession session = request.getSession(true);
		String xmlCnf = request.getParameter("xml");
		if(xmlCnf == null) xmlCnf = (String)session.getAttribute("xmlcnf");
		if(xmlCnf == null) xmlCnf = context.getInitParameter("init_xml");
		if(xmlCnf == null) xmlCnf = context.getInitParameter("config_file");
		if(setSession && (xmlCnf != null)) session.setAttribute("xmlcnf", xmlCnf);
		
		String ps = System.getProperty("file.separator");
		xmlFile = context.getRealPath("WEB-INF") + ps + "configs" + ps + xmlCnf;
		
		String projectDir = context.getInitParameter("projectDir");
		if(projectDir != null) xmlFile = projectDir + ps + "configs" + ps + xmlCnf;
		
		if(xmlFile == null) return;					// File error check
		if(!Bio.FileExists(xmlFile)) return;		// File error check

		initXML(xmlFile, false);
	}

	public BElement getDocument() {
		return root;
	}

	public BElement getRoot() {
		return root.getFirst();
	}

	public void saveFile() {
		if(xmlFile != null) {
			BElement el = root.getFirst();
			Bio io = new Bio();
			io.saveFile(xmlFile, el.toString());
		}
	}
	
	public void saveFile(String fileName) {
		if(fileName != null) {
			BElement el = root.getFirst();
			Bio io = new Bio();
			io.saveFile(fileName, el.toString());
		}
	}
}

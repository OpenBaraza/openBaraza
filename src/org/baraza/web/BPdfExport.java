/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2020.0329
 * @since       2.7
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.web;

import java.util.Map;
import java.io.StringReader;
import java.io.IOException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.itextpdf.tool.xml.exceptions.RuntimeWorkerException;

import org.baraza.utils.BTextFormat;
import org.baraza.xml.BElement;
import org.baraza.DB.BQuery;

public class BPdfExport extends HttpServlet {

	BWeb web = null;

	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		web = new BWeb(getServletContext(), request);
		
		BElement view = web.getView();
		
		// Check for null view error
		if(view == null) {
			web.close(); 
			return;
		}

		// Call the where create function
		Map<String, String> whereParams = web.getWhere(request);
		String whereSql = whereParams.get("wheresql");

		try {
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "attachment; filename=ob_report.pdf");
			
			ServletOutputStream os = response.getOutputStream();
			
			if(view.getName().equals("GRID")) getGridPdf(os, view, whereSql);
			if(view.getName().equals("FORMVIEW")) getFormViewPdf(os, view, whereSql);
		} catch(IOException ex) {
			System.out.println("IO Exception : " + ex);
		}

		web.close(); 
	}
	
	public void getGridPdf(ServletOutputStream os, BElement view, String whereSql) {
	
		int colCount = 0;
		for(BElement el : view.getElements()) {
			if(!el.getValue().equals("")) colCount++;
		}

		try {
			Document doc = new Document();
			PdfWriter writer = PdfWriter.getInstance(doc, os);
			doc.addCreator("openbaraza.org");
			doc.setMargins(30, 30, 30, 30);
			
			doc.addTitle(view.getAttribute("name", "Report"));
			if(colCount < 4) doc.setPageSize(PageSize.A4);
			else doc.setPageSize(PageSize.A4.rotate());
			doc.open();
			
			PdfPTable table = new PdfPTable(colCount);
			table.setWidthPercentage(100);
			
			Font headingFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD);
			Font boldFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD);
			Font baseFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.NORMAL);
			
			for(BElement el : view.getElements()) {
				String colTitle = el.getAttribute("title");
				if(!el.getValue().equals("") && (colTitle != null)) {
					PdfPCell cell = new PdfPCell(new Phrase(colTitle, headingFont));
					cell.setBorderWidth(1);
					cell.setBorderColor(BaseColor.LIGHT_GRAY );
					table.addCell(cell);
				}
			}
			
			BQuery pdfData = new BQuery(web.getDB(), view, whereSql, null, false);
			pdfData.beforeFirst();
			while(pdfData.moveNext()) {
				for(BElement el : view.getElements()) {
					String colName = el.getValue();
					if(!colName.equals("") && (el.getAttribute("title") != null)) {
						String elValue = pdfData.formatData(el);
						if(el.getAttribute("raw") != null) elValue = BTextFormat.htmlToText(elValue);
						PdfPCell cell = new PdfPCell(new Phrase(elValue, baseFont));
						if(el.getAttribute("bold") != null) cell = new PdfPCell(new Phrase(elValue, boldFont));
						
						cell.setBorderWidth(1);
						cell.setBorderColor(BaseColor.LIGHT_GRAY );
						table.addCell(cell);
					}
				}
			}
			pdfData.close();

			doc.add(table);

			doc.close();
		} catch(DocumentException ex) {
			System.out.println("Document Exception error : " + ex);
		}
	}
	
	public void getFormViewPdf(ServletOutputStream os, BElement view, String whereSql) {

		try {
			Document doc = new Document();
			PdfWriter writer = PdfWriter.getInstance(doc, os);
			doc.addCreator("openbaraza.org");
			doc.setMargins(30, 30, 30, 30);
			
			doc.addTitle(view.getAttribute("name", "Report"));
			doc.setPageSize(PageSize.A4);
			doc.open();
			
			PdfPTable table = new PdfPTable(new float[]{200, 500});
			table.setWidthPercentage(100);
			
			Font headingFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD);
			Font boldFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD);
			Font baseFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.NORMAL);
			
			BQuery pdfData = new BQuery(web.getDB(), view, whereSql, null, false);
			pdfData.beforeFirst();
			int row = 0;
			while(pdfData.moveNext()) {
				for(BElement el : view.getElements()) {
					String colName = el.getValue();
					String colTitle = el.getAttribute("title");
					if(!colName.equals("") && (colTitle != null)) {
						String elValue = pdfData.formatData(el);
						if(el.getAttribute("raw") != null) elValue = BTextFormat.htmlToText(elValue);
						
						PdfPCell titleCell = new PdfPCell(new Phrase(colTitle, headingFont));
						PdfPCell cell = new PdfPCell(new Phrase(elValue, baseFont));
						if(el.getAttribute("bold") != null) cell = new PdfPCell(new Phrase(elValue, boldFont));
						
						if((row % 2) == 0) {
							titleCell.setBackgroundColor(new BaseColor(245, 245, 245));
							cell.setBackgroundColor(new BaseColor(245, 245, 245));
						}
						
						titleCell.setBorder(Rectangle.NO_BORDER);
						table.addCell(titleCell);
						
						cell.setBorder(Rectangle.NO_BORDER);
						table.addCell(cell);
					}
					row++;
				}
			}
			pdfData.close();

			doc.add(table);

			doc.close();
		} catch(DocumentException ex) {
			System.out.println("Document Exception error : " + ex);
		}
	}
	
	public ElementList getHtmlElements(String htmlData) {
		ElementList elements = new ElementList();
		try {
			// CSS
			CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
			
			// HTML
			HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
			htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
			htmlContext.autoBookmark(false);
			
			// Pipelines
			ElementHandlerPipeline end = new ElementHandlerPipeline(elements, null);
			HtmlPipeline html = new HtmlPipeline(htmlContext, end);
			CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
			
			// XML Worker
			XMLWorker worker = new XMLWorker(css, true);
			XMLParser p = new XMLParser(worker);
			p.parse(new StringReader(htmlData));
		} catch(IOException ex) {
			System.out.println("Error occured on HTML to PDF parsing : " + ex);
			System.out.println(htmlData);
		} catch(RuntimeWorkerException ex) {
			System.out.println("Error occured on HTML to PDF parsing : " + ex);
			System.out.println(htmlData);
		}
		
		return elements;
	}

}


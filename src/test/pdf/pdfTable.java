import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class pdfTable {

	public static void main(String[] args) {
		pdfTable pt = new pdfTable();
		pt.pdfStrikeThough("results.pdf");
	}

	public void pdfTableWrite(String dest) {
		try {
			Rectangle small = new Rectangle(290,100);
			Font smallfont = new Font(FontFamily.HELVETICA, 8);
			Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 0f);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
			
			document.open();
			PdfPTable table = new PdfPTable(14);
			table.setWidthPercentage(95f);
			PdfContentByte cb = writer.getDirectContent();
			
			String[] myDays = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
			
			// first row
			for(String myDay : myDays) {
				PdfPCell cell = new PdfPCell(new Phrase(myDay));
				cell.setFixedHeight(30);
				cell.setBorder(Rectangle.BOX);
				cell.setColspan(2);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}
			
			// second row
			for(int i = 0; i < 7; i++) {
				PdfPCell cell = new PdfPCell(new Phrase("KW:", smallfont));
				cell.setFixedHeight(30);
				cell.setBorder(Rectangle.BOX);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase("HQ:", smallfont));
				cell.setFixedHeight(30);
				cell.setBorder(Rectangle.BOX);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(cell);
			}
	
			
			document.add(table);
			document.close();
		} catch(FileNotFoundException ex) {
			System.out.println("Error FileNotFoundException " + ex);
		} catch(DocumentException ex) {
			System.out.println("Error DocumentException " + ex);
		}
	}
	
	public void pdfWrite(String dest) {
		try {
			Rectangle small = new Rectangle(290,100);
			Font smallfont = new Font(FontFamily.HELVETICA, 10);
			Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 0f);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
			
			document.open();
			PdfPTable table = new PdfPTable(2);
			table.setTotalWidth(new float[]{ 160, 120 });
			table.setLockedWidth(true);
			PdfContentByte cb = writer.getDirectContent();
			
			// first row
			PdfPCell cell = new PdfPCell(new Phrase("Some text here"));
			cell.setFixedHeight(30);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setColspan(2);
			table.addCell(cell);
			
			// second row
			cell = new PdfPCell(new Phrase("Some more text", smallfont));
			cell.setFixedHeight(30);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
			
			Barcode128 code128 = new Barcode128();
			code128.setCode("14785236987541");
			code128.setCodeType(Barcode128.CODE128);
			Image code128Image = code128.createImageWithBarcode(cb, null, null);
			cell = new PdfPCell(code128Image, true);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setFixedHeight(30);
			table.addCell(cell);
			
			// third row
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("and something else here", smallfont));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			
			document.add(table);
			document.close();
		} catch(FileNotFoundException ex) {
			System.out.println("Error FileNotFoundException " + ex);
		} catch(DocumentException ex) {
			System.out.println("Error DocumentException " + ex);
		}
	}
	
	public void pdfStrikeThough(String dest) {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(dest));
			document.open();
			PdfPTable table = new PdfPTable(2);
			table.addCell(new Phrase("0123456789"));
			Font font = new Font(FontFamily.HELVETICA, 12f, Font.STRIKETHRU);
			table.addCell(new Phrase("0123456789", font));
			Chunk chunk1 = new Chunk("0123456789");
			chunk1.setUnderline(1.5f, -1);
			table.addCell(new Phrase(chunk1));
			Chunk chunk2 = new Chunk("0123456789");
			chunk2.setUnderline(1.5f, 3.5f);
			table.addCell(new Phrase(chunk2));
			document.add(table);
			document.close();
		} catch(FileNotFoundException ex) {
			System.out.println("Error FileNotFoundException " + ex);
		} catch(DocumentException ex) {
			System.out.println("Error DocumentException " + ex);
		}
    }
}



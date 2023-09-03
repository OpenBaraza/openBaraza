
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;


public class pdfPassword {

	static String owner = "890082072"; //"299796"; //"DEWCI890082072";
	static String original = "./attach/95900111212072.pdf"; //"SCB_eStatement9558793.pdf";
	static String destination = "F0365.pdf";

	public static void main(String... args) {
		pdfPassword pd = new pdfPassword();
		pd.decryptPdf();
	}
	

	public void decryptPdf() {
		try {
			// select a file for Decryption operation
			File inFile = new File(original);
			File outFile = new File(destination);

			// Load the PDF file
			PDDocument pdd = PDDocument.load(inFile, owner);

			// removing all security from PDF file
			pdd.setAllSecurityToBeRemoved(true);

			// Save the PDF file
			pdd.save(outFile);

			// Close the PDF file
			pdd.close();
			System.out.println("Decryption Done...");
		} catch(IOException ex) {
			System.out.println("IO Exception : " + ex);
		}
	}

	public void itextOpen() {
		try {
			PdfReader reader = new PdfReader(original, owner.getBytes());
			reader.unethicalreading = true;
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destination));
			stamper.close();
		} catch(IOException ex) {
			System.out.println("IO Exception : " + ex);
		} catch(DocumentException ex) {
			System.out.println("Document Exception : " + ex);
		}
	}
}

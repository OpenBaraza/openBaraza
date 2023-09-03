/* Basic Java testing class */
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

class pdfDecode {

	public static void main(String[] args) {
		pdfDecode b2 = new pdfDecode();
		b2.base64toPdf();
	}

	public void base64toPdf() {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("/root/Desktop/TODO"));
			byte[] decoded = Base64.getMimeDecoder().decode(encoded);

			FileOutputStream fos = new FileOutputStream("/root/Desktop/TODO.pdf");
			fos.write(decoded);
			fos.close();
		} catch(IOException ex) {
			System.out.println("IO error : " + ex);
		}
	}

}


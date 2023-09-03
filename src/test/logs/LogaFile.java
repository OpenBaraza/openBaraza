
import java.io.IOException;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

class LogaFile {

	Logger logger = Logger.getLogger(LogaFile.class.getName());

	public static void main(String argv[]) {
		LogaFile laf = new LogaFile();
		laf.testFileLog();
	}
	
	public void testFileLog() {
		FileHandler fh;  

		try {  
			// This block configure the logger with handler and formatter  
			fh = new FileHandler("MyLogFile.log");  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  

			// the following statement is used to log any messages  
			logger.info("My first log");  

		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  

		logger.info("Hi How r u?");  

	}

}

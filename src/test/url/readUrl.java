
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

class readUrl {

	public static void main(String[] args) {
		readUrl ru = new readUrl();
		ru.runFile("update_pin_data.txt");
	}
	
	public void runFile(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName)); 
			String line;
			while ((line = br.readLine()) != null) {
				if(line.length() > 10) readLink(line);
			}
		} catch(IOException ex) {
			System.out.println("IOException : " + ex);
		}
	}
	
	public String readLink(String link) {
		StringBuffer result = new StringBuffer();
		
		try {
			URL url = new URL(link);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) result.append(inputLine + "\n");
			in.close();
		} catch(MalformedURLException ex) {
			System.out.println("MalformedURLException : " + ex);
		} catch(IOException ex) {
			System.out.println("IOException : " + ex);
		}
		
		System.out.println(result.toString());
    
		return result.toString();
	}


}

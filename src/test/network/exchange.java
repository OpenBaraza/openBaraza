import java.net.*;
import java.io.*;

public class exchange {

    public static void main(String[] args) throws Exception {

        URL exchUrl = new URL("https://www.centralbank.go.ke/cbk-indicative-rates/");
        BufferedReader in = new BufferedReader(new InputStreamReader(exchUrl.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
    }
    
}

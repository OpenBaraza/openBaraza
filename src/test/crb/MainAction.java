
import java.rmi.RemoteException;

import co.ke.transunion.crbws.ws.*;

public class MainAction {

	public static void main(String[] args) {
		KenyaClientService clientService = new KenyaClientService("https://secure3.transunionafrica.com/crbws/ke?wsdl", "cX26K2W836QT8Up", "D57Jc8SdsSJ1gAE");

		try {
			//ServerInfo serverInfo = clientService.retrieveServerInfo("client", "crb@123456", "1329", "ke12345");
			ServerInfo serverInfo = clientService.retrieveServerInfo("WS_SNM1", "n6s0i@hit", "2221", "1328KE46406");
			
			System.out.println("Response Code: " + serverInfo.getResponseCode());

			if (serverInfo.getResponseCode() == 100) {
				System.out.println("Protocol: " + serverInfo.getProtocol());
				System.out.println("Country: " + serverInfo.getCountry());
				System.out.println("Version: " + serverInfo.getVersion());
				
				// Check credit
				System.out.println("\nGet Credit");
				CreditBal cBal = clientService.getCreditBal("WS_SNM1", "n6s0i@hit", "2221", "1328KE46406");
				System.out.println("Response Code : " + cBal.getResponseCode());
				System.out.println("Credit : " + cBal.getCredit());
				
				
				// Get product
				System.out.println("\nGet personal information");
				Product131 P131 = clientService.getProduct131("WS_SNM1", "n6s0i@hit", "2221", "1328KE46406", "Dennis", "Gichangi", "21407379");
				if(P131 == null) {
					System.out.println("Null Response");
				} else {
					System.out.println("Response Code : " + P131.getResponseCode());
				
					if(P131.getHeader() != null) {
						System.out.println("\nPrint Header");
						System.out.println(P131.getHeader().getCrbName());
						System.out.println(P131.getHeader().getProductDisplayName());
						System.out.println(P131.getHeader().getReportDate());
						System.out.println(P131.getHeader().getReportType());
						System.out.println(P131.getHeader().getRequestNo());
						System.out.println(P131.getHeader().getRequester());
					}
	
					if(P131.getPersonalProfile() != null) {
						System.out.println("\nPrint personal profile");
						System.out.println(P131.getPersonalProfile().getFullName());
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}

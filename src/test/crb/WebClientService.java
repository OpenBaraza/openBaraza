
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.net.MalformedURLException;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;

import co.ke.transunion.crbws.ws.*;
import co.ke.transunion.crbws.ws.impl.*;

public class WebClientService {
		
	public static void main(String[] args) {
		Authenticator myAuth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new java.net.PasswordAuthentication("cX26K2W836QT8Up", "D57Jc8SdsSJ1gAE".toCharArray());
			}
		};
		Authenticator.setDefault(myAuth);
		
	   try {
			String WS_URL = "https://secure3.transunionafrica.com/crbws/ke?wsdl";
			URL url = new URL(WS_URL);
			QName qname = new QName("http://impl.ws.crbws.transunion.ke.co/", "ControllerKenyaImplService");

			Service service = Service.create(url, qname);
			ControllerKenya controller = service.getPort(ControllerKenya.class);

			Map<String, Object> req_ctx = ((BindingProvider)controller).getRequestContext();
			req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WS_URL);

			Map<String, List<String>> headers = new HashMap<String, List<String>>();
			headers.put("Username", Collections.singletonList("WS_SNM1"));
			headers.put("Password", Collections.singletonList("n6s0i@hit"));
			req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
			
			//ServerInfo serverInfo = controller.getServerInfo("client", "crb@123456", "1329", "ke12345");
			//ServerInfo serverInfo = controller.getServerInfo("client", "transunion", "1329", "ke12345");
			//ServerInfo serverInfo = controller.getServerInfo("WS_BSK1", "mnqRuv", "2104", "ke123456789");
			ServerInfo serverInfo = controller.getServerInfo("WS_SNM1", "n6s0i@hit", "2221", "1328KE46406");
			
			System.out.println("Response Code: " + serverInfo.getResponseCode());
			if (serverInfo.getResponseCode() == 100) {
				System.out.println("Protocol: " + serverInfo.getProtocol());
				System.out.println("Country: " + serverInfo.getCountry());
				System.out.println("Version: " + serverInfo.getVersion());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}


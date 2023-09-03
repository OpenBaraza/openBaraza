

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;

import co.ke.transunion.crbws.ws.*;
import co.ke.transunion.crbws.ws.impl.*;

public class KenyaClientService {
	private ControllerKenya controller;

	public KenyaClientService(String url, String userName, String password) {
		init(url, userName, password);
	}
	
	private void init(String url, String userName, String password) {
		Authenticator myAuth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new java.net.PasswordAuthentication(userName, password.toCharArray());
			}
		};
		Authenticator.setDefault(myAuth);

		try {
			URL wsdlUrl = new URL(url);
			ControllerKenyaImplService ckis = new ControllerKenyaImplService(wsdlUrl);
			controller = ckis.getControllerKenyaImplPort();
			

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public ServerInfo retrieveServerInfo(String username, String password, String code, String infinityCode) throws RemoteException {
		return controller.getServerInfo(username, password, code, infinityCode);
	}
	
	public CreditBal getCreditBal(String username, String password, String code, String infinityCode) throws RemoteException {
		return controller.getCreditBal(username, password, code, infinityCode);
	}
		
	public Product131 getProduct131(String username, String password, String code, String infinityCode, String firstName, String surName, String nationalID) throws RemoteException {
		String name1 = firstName;
		String name2 = surName;
		String name3 = null;
		String name4 = null;
		String passportNo = null;
		String serviceID = null;
		String alienID = null;
		String taxID = null;
		XMLGregorianCalendar dateOfBirth = null;
		String postalBoxNo = null;
		String postalTown = null;
		String postalCountry = null;
		String telephoneWork = null;
		String telephoneHome = null;
		String telephoneMobile = null;
		String physicalAddress = null;
		String physicalTown = null;
		String physicalCountry = null;
		int reportSector = 2;
		int reportReason = 4;
		
		Product131 P131 = controller.getProduct131(username, password, code, infinityCode, 
			name1, name2, name3, name4, nationalID, passportNo, serviceID, alienID, taxID, 
			dateOfBirth, postalBoxNo, postalTown, postalCountry, telephoneWork, telephoneHome, 
			telephoneMobile, physicalAddress, physicalTown, physicalCountry, reportSector, reportReason);
		
		return P131;
	}

}


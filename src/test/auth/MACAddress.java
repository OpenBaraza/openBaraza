
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MACAddress {

	public static void main(String[] args){
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)) {
				displayInterfaceInformation(netint);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		Properties props = System.getProperties();
		for(String prop : props.stringPropertyNames()) {
			System.out.println("Prop : " + prop + " = " + props.getProperty(prop));
		}
		
		Map<String,String> envs = System.getenv();
		for(String env : envs.keySet()) {
			System.out.println("Env : " + env + " = " + envs.get(env));
		}
	}

	private static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
		System.out.printf("Display name: %s%n", netint.getDisplayName());
		System.out.printf("Name: %s%n", netint.getName());
		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
		for (InetAddress inetAddress : Collections.list(inetAddresses)) {
			System.out.printf("InetAddress: %s%n", inetAddress);
		}

		System.out.printf("Parent: %s%n", netint.getParent());
		System.out.printf("Up? %s%n", netint.isUp());
		System.out.printf("Loopback? %s%n", netint.isLoopback());
		System.out.printf("PointToPoint? %s%n", netint.isPointToPoint());
		System.out.printf("Supports multicast? %s%n", netint.isVirtual());
		System.out.printf("Virtual? %s%n", netint.isVirtual());
		if(netint.getHardwareAddress() != null) displayMACAddress(netint.getHardwareAddress());
		System.out.printf("MTU: %s%n", netint.getMTU());

		List<InterfaceAddress> interfaceAddresses = netint.getInterfaceAddresses();
		for (InterfaceAddress addr : interfaceAddresses) {
			System.out.printf("InterfaceAddress: %s%n", addr.getAddress());
		}
		System.out.printf("%n");
		Enumeration<NetworkInterface> subInterfaces = netint.getSubInterfaces();
		for (NetworkInterface networkInterface : Collections.list(subInterfaces)) {
			System.out.printf("%nSubInterface%n");
			displayInterfaceInformation(networkInterface);
		}
		System.out.printf("%n");
	}

	private static void displayMACAddress(byte[] mac) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
		System.out.println("Current MAC address : " + sb.toString());
		sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], ""));		
		}
		System.out.println("Current MAC address : " + sb.toString());
	}
}



import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

public class jtailf {

	public static void main(String args[]) {
		if (args.length > 0) {
			System.out.println("Application " + args[0]);
			
			jtailf jt = new jtailf();
			Map<String, List<String>> serverMap = jt.readServers("server.app");
			List<String> serverList = serverMap.get(args[0]);
			if(serverList == null) {
				System.out.println("Application " + args[0] + " not found");
				return;
			}
			
			if(args.length == 1) jt.serverCommand(serverList);
			else if(args.length == 2) jt.serverConnect(serverList);
		}
	}
	
	public Map<String, List<String>> readServers(String fileName) {
		Map<String, List<String>> mApps = new HashMap<String, List<String>>();
		List<String> sLines = null;
		try {
			Stream<String> sFile = Files.lines(Paths.get("server.app"));
			sLines = sFile.collect(Collectors.toList());
		} catch(IOException ex) {
			System.out.println(ex);
		}
		//sLines.forEach(System.out::println);
		
		for(String sLine : sLines) {
			StringTokenizer stok = new StringTokenizer(sLine, "\"\t");
			List<String> stList = new ArrayList<String>();
			while (stok.hasMoreTokens()){
				String st = stok.nextToken().trim();
				if(st.length() > 1) stList.add(st);
			}
			if(stList.size() == 5) mApps.put(stList.get(0), stList);
		}
		
		return mApps;
	}
	
	public void serverCommand(List<String> serverArgs) {
		try {
			String host = serverArgs.get(1);
			String username = serverArgs.get(2); 
			String password = serverArgs.get(3);
			String command = serverArgs.get(4);
			
			System.out.println("Server Command : " + host + " : " + command);
			
			JSch jsch = new JSch();
			Session session = jsch.getSession(username, host, 22);
			Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect(50000);
			
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			channel.connect(3*1000);
		} catch(JSchException ex) {
			System.out.println(ex);
		}
	}
	
	public void serverConnect(List<String> serverArgs) {
		try {
			String host = serverArgs.get(1);
			String username = serverArgs.get(2); 
			String password = serverArgs.get(3);
			String command = serverArgs.get(4);
			
			System.out.println("Server Connection : " + host + " : " + command);
			
			JSch jsch = new JSch();
			Session session = jsch.getSession(username, host, 22);
			Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect(50000);
			
			Channel channel = session.openChannel("shell");
      
			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			channel.connect(3*1000);
		} catch(JSchException ex) {
			System.out.println(ex);
		}
	}

}

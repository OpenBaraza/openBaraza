import java.io.BufferedReader;
import java.io.InputStreamReader;

public class getMotherBoardSerial {

	private static String OS = System.getProperty("os.name").toLowerCase();
	static String command= null;

	public static void main(String[] args) {

		getMotherBoardSerial obj = new getMotherBoardSerial();

		if (isWindows()) {
			System.out.println("This is Windows");
			String execName = "serialnumber";
			command = "wmic baseboard get" + execName;
		} else if (isMac()) {
			System.out.println("This is Mac");
			String execName = "/sys/devices/virtual/dmi/id/board_serial";
			command = "cat " + execName;
		} else if (isUnix()) {
			System.out.println("This is Unix or Linux");
			//String execName = "/sys/devices/virtual/dmi/id/chassis_serial ";
			//String execName = "/sys/devices/virtual/dmi/id/board_serial";
			//command = "cat " + execName;
			String execName = "dmidecode -t system";
			command = execName;
		} else if (isSolaris()) {
			System.out.println("This is Solaris");
		} else {
			System.out.println("Your OS is not support!!");
		}

		String output = obj.executeCommand(command);
		System.out.println(output);

	}

	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
		
	}

	public static boolean isSolaris() {

		return (OS.indexOf("sunos") >= 0);

	}

}


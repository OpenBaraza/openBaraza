/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class mytomcat {

	public static void main(String args[]) {
		mytomcat mtc = new mytomcat();
		mtc.init();
	}

	public void init() {
		try {
			Tomcat tomcat = new Tomcat();
			tomcat.setBaseDir("temp");
			tomcat.setPort(9090);
			tomcat.getConnector();

			tomcat.start();
			tomcat.getServer().await();
		} catch(LifecycleException ex) {
			System.out.println("error LifecycleException : " + ex);
		}
	}

}

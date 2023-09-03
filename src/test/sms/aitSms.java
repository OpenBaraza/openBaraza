
import com.africastalking.Callback;
import com.africastalking.SmsService;
import com.africastalking.sms.Message;
import com.africastalking.sms.Recipient;
import com.africastalking.AfricasTalking;

import java.util.List;
import java.io.IOException;

public class aitSms {

    public static void main(String[] args) {
		/* Set your app credentials */
		String USERNAME = "dewcisoln";
		String API_KEY = "fd12b52a3e84fa9b3fc20cd06c3aef5544975f63424cba0fab4c302ded967d66";
		
		/* Initialize SDK */
		AfricasTalking.initialize(USERNAME, API_KEY);

		/* Get the SMS service */
		SmsService sms = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);

		/* Set the numbers you want to send to in international format */
		String[] recipients = new String[] {"+254711572013", "+254733578156"};

		/* Set your message */
		String message = "We are lumberjacks. We sleep all day and code all night.";

		/* Set your shortCode or senderId */
		String from = "Dewcis";
		
		/* That’s it, hit send and we’ll take care of the rest */
		try {
			List<Recipient> response = sms.send(message, from, recipients, true);
			for (Recipient recipient : response) {
				System.out.print(recipient.number);
				System.out.print(" : ");
				System.out.println(recipient.status);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
   	}
}


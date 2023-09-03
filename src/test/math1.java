
import java.lang.Math;

public class math1 {

	public static void main(String args[]) {

		for(double a = 0; a < 10; a++) {
			for(double b = 0; b < 10; b++) {
				for(double c = 0; c < 10; c++) {
					Double gs = (a * a) + (b * b) + (c * c);
					Double g = Math.sqrt(gs);

					double dv = g - g.longValue();
					if(dv == 0) System.out.println(a + "^2 + " + b + "^2 + " + c + "^2 = " + gs + " g = " + g + " dv = " + dv);
				}
			}
		}

		System.out.println("Exiting Test mode");
	}
}



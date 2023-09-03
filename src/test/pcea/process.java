import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;

public class process {

	BDB db = null;

	public static void main(String args[]) {
		System.out.println("Starting data processing for PCEA excel file");

		process pr = new process();
		pr.connect();
		//pr.processDataOne();
		//pr.ProcessDataTwo();
		//pr.ProcessDataThree();
		pr.ProcessDataFour();
		pr.close();

		System.out.println("Finished data processing for PCEA excel file .... ");
	}

	public void processDataOne() {

		String mySql = "SELECT imp_parishs_id, col1, col2, col3, col4, col5, col6, col7, col8, col9 "
			+ "FROM imp_parishs ORDER BY imp_parishs_id";
		BQuery rs = new BQuery(db, mySql);
		
		while(rs.moveNext()) {
			String col1 = rs.getString("col1");
			//System.out.println(col1);
			
			if(col1 == null) col1 = "";
			col1 = col1.toLowerCase();
			int emailPos = col1.indexOf("@");

			if(col1.contains("presbytery") && (emailPos == -1)) {
				//System.out.println(col1);
				rs.recEdit();
				rs.updateRecField("col2", col1);
				rs.recSave();
			} else if(col1.contains("parish") && (emailPos == -1)) {
				//System.out.println(col1);
				rs.recEdit();
				rs.updateRecField("col3", col1);
				rs.recSave();
			} else if(col1.contains("rev") && (emailPos == -1)) {
				//System.out.println(col1);
				rs.recEdit();
				rs.updateRecField("col4", col1);
				rs.recSave();
			} else if(col1.contains("po") && col1.contains("box") && (emailPos == -1)) {
				//System.out.println(col1);
				rs.recEdit();
				rs.updateRecField("col5", col1);
				rs.recSave();
			} else if(col1.contains("p.o") && col1.contains("box") && (emailPos == -1)) {
				//System.out.println(col1);
				rs.recEdit();
				rs.updateRecField("col5", col1);
				rs.recSave();
			} else if(col1.contains("tel") && (emailPos == -1)) {
				//System.out.println(col1);
				rs.recEdit();
				rs.updateRecField("col6", col1);
				rs.recSave();
			} else if((col1.contains("07") || col1.contains("01")) && (emailPos == -1)) {
				//System.out.println(col1);
				rs.recEdit();
				rs.updateRecField("col7", col1);
				rs.recSave();
			} else if(col1.contains("region") && (emailPos == -1)) {
				//System.out.println(col1);
				rs.recEdit();
				rs.updateRecField("col9", col1);
				rs.recSave();
			} else if(emailPos > 1) {
				//System.out.println(col1);
				rs.recEdit();
				rs.updateRecField("col8", col1);
				rs.recSave();
			} else {
				System.out.println(col1);
			}
		}
		rs.close();
	}
	
	public void ProcessDataTwo() {

		String mySql = "SELECT imp_parishs_id, col1, col2, col3, col4, col5, col6, col7, col8, col9 "
			+ "FROM imp_parishs ORDER BY imp_parishs_id";
		BQuery rs = new BQuery(db, mySql);
		
		String inSql = ""; String insData = "";
		Map<String, String> mParish = new LinkedHashMap<String, String>();
		String region = null;
		String presbytery = null;
		String parish = null;
		while(rs.moveNext()) {
			if(rs.getString("col9") != null) region = rs.getString("col9");
			if(rs.getString("col2") != null) {
				presbytery = rs.getString("col2");
				int ppos = presbytery.indexOf(" ");
				if((ppos > 0) && (ppos < 3)) presbytery = presbytery.substring(ppos).trim();
				presbytery = presbytery.replace(".", "");
			}
			if(rs.getString("col3") != null) {
				if(!rs.getString("col3").toLowerCase().startsWith("www")) {
					parish = rs.getString("col3");
				}
			}
			
			if(rs.getString("col4") != null) {	// Rev
				if(mParish.size() > 1) {
					inSql += ") VALUES " + insData + ")";
					System.out.println(inSql);
					String keyFieldId = db.saveRec(inSql, mParish);
				}
				
				String rev = rs.getString("col4");
				rev = rev.toLowerCase().replace("rev", "").replace(".", "").trim();
				if(rev.startsWith("dr ")) rev = rev.substring(3).trim();
				rev = rev.replace("'", "").replace("’", "");
				
				//System.out.println(rev);
				String firstName = null;
				String middleName = null;
				String lastName = null;
				if(rev.indexOf(" ") > 1) {
					firstName = rev.substring(0, rev.indexOf(" ")).trim();
					lastName = rev.substring(rev.lastIndexOf(" ")).trim();
				}
				String[] revNames = rev.split(" ");
				if(revNames.length > 2) middleName = revNames[1].trim();
				
				mParish = new LinkedHashMap<String, String>();
				mParish.put("col1", region);
				mParish.put("col2", presbytery);
				mParish.put("col3", parish);
				mParish.put("col4", rs.getString("col4"));
				mParish.put("col9", rev);
				mParish.put("col10", firstName);
				mParish.put("col11", lastName);
				mParish.put("col12", middleName);
				
				inSql = "INSERT INTO imp_depts (col1, col2, col3, col4, col9, col10, col11, col12";
				insData = "(?, ?, ?, ?, ?, ?, ?, ?";
			}
			
			if(rs.getString("col5") != null) {		//PO Box
				if(mParish.containsKey("col5")) {
					mParish.put("col5", mParish.get("col5") + "| " + rs.getString("col5"));
				} else {
					mParish.put("col5", rs.getString("col5"));
					inSql += ", col5";
					insData += ", ?";
				}
			} else if(rs.getString("col6") != null) {		//Telephone
				String tel = rs.getString("col6");
				tel = tel.replace("tel:", "").trim();
				if(mParish.containsKey("col6")) {
					mParish.put("col6", mParish.get("col6") + ", " + tel);
				} else {
					mParish.put("col6", tel);
					inSql += ", col6";
					insData += ", ?";
				}
			} else if(rs.getString("col7") != null) {		//Cell Phone
				String mobile = rs.getString("col7");
				mobile = mobile.replace("mobile:", "").trim();
				if(mParish.containsKey("col7")) {
					mParish.put("col7", mParish.get("col7") + ", " + mobile);
				} else {
					mParish.put("col7", mobile);
					mParish.put("phone", mobile);
					inSql += ", phone, col7";
					insData += ", ?, ?";
				}
			} else if(rs.getString("col8") != null) {		//Email
				if(mParish.containsKey("col8")) {
					mParish.put("col8", mParish.get("col8") + ", " + rs.getString("col8"));
				} else {
					mParish.put("col8", rs.getString("col8"));
					mParish.put("email", rs.getString("col8"));
					inSql += ", email, col8";
					insData += ", ?, ?";
				}
			}
		}
		rs.close();
	}
	
	public void ProcessDataThree() {
		String mySql = "SELECT imp_updated_data_id, surname, first_name, middle_name, col10, col11, col12 "
			+ "FROM imp_updated_data "
			+ "ORDER BY imp_updated_data_id";
		BQuery rs = new BQuery(db, mySql);
		
		while(rs.moveNext()) {
			String surname = rs.getString("surname");
			String firstName = rs.getString("first_name");
			String middleName = rs.getString("middle_name");
			if(surname != null) {
				surname = surname.replace("'", "").replace("’", "");
				surname = surname.toLowerCase().trim();
			}
			if(firstName != null) {
				firstName = firstName.replace("'", "").replace("’", "");
				firstName = firstName.toLowerCase().trim();
			}
			if(middleName != null) {
				middleName = middleName.replace("'", "").replace("’", "");
				middleName = middleName.toLowerCase().trim();
			}
			
			rs.recEdit();
			rs.updateRecField("col10", firstName);
			rs.updateRecField("col11", surname);
			rs.updateRecField("col12", middleName);
			rs.recSave();
		}
	}
	
	public void ProcessDataFour() {
		String mySql = "SELECT imp_data_id, surname, first_name, middle_name, employee_name "
			+ "FROM imp_data "
			+ "ORDER BY imp_data_id";
		BQuery rs = new BQuery(db, mySql);
		
		while(rs.moveNext()) {
			String employeeName = rs.getString("employee_name");
			String surname = rs.getString("surname");
			String firstName = rs.getString("first_name");
			String middleName = rs.getString("middle_name");
			
			employeeName = employeeName.trim();
			String[] revNames = employeeName.split(" ");
			if(revNames.length == 1) {
				surname = revNames[0].trim();
			} else if(revNames.length == 2) {
				surname = revNames[0].trim();
				firstName = revNames[1].trim();
			} else if(revNames.length == 3) {
				surname = revNames[0].trim();
				firstName = revNames[1].trim();
				middleName = revNames[2].trim();
			} else if(revNames.length == 4) {
				surname = revNames[0].trim();
				firstName = revNames[1].trim();
				middleName = revNames[2].trim() + " " + revNames[3].trim();
			}
			
			rs.recEdit();
			rs.updateRecField("first_name", firstName);
			rs.updateRecField("surname", surname);
			rs.updateRecField("middle_name", middleName);
			rs.recSave();
		}
	}

	public void connect() {
		String dbClass = "org.postgresql.Driver";
		String url = "jdbc:postgresql://localhost/hr";
		db = new BDB(dbClass, url, "postgres", "invent");
    }

	public void close() {
		if(db != null) db.close();
    }

}

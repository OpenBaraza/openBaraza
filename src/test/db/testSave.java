import java.sql.*;

public class testSave {

	public static void main(String args[]) {
		updateStatement();
	}
	
	public static void newStatement() {
		try {
			Connection db = DriverManager.getConnection("jdbc:postgresql://localhost/hr", "root", "invent2k");
			
			String inSql = "INSERT INTO bmc (org_id, bmc_name) VALUES (?, ?)";
			PreparedStatement ps = db.prepareStatement(inSql, Statement.RETURN_GENERATED_KEYS);
			ResultSetMetaData psmd = ps.getMetaData();
			
			int colCount = psmd.getColumnCount();
			for(int i = 1;  i <= colCount; i++) {
				System.out.println(psmd.getColumnName(i));
				System.out.println(psmd.getColumnType(i));
			}
			
			ps.close();
			db.close();
		} catch (SQLException ex) {
			System.out.println("Database connection error : " + ex);
		}
	}
	
	public static void updateStatement() {
		try {
			Connection db = DriverManager.getConnection("jdbc:postgresql://localhost/hr", "root", "invent2k");
			
			String inSql = "UPDATE bmc SET org_id = ?, bmc_name = ? WHERE bmc_id = 0";
			PreparedStatement ps = db.prepareStatement(inSql, Statement.RETURN_GENERATED_KEYS);
			ResultSetMetaData psmd = ps.getMetaData();
			
			int colCount = psmd.getColumnCount();
			for(int i = 1;  i <= colCount; i++) {
				System.out.println(psmd.getColumnName(i));
				System.out.println(psmd.getColumnType(i));
			}
			
			ps.close();
			db.close();
		} catch (SQLException ex) {
			System.out.println("Database connection error : " + ex);
		}
	}
	
	public static void updateSMS() {
		try {
			Connection db = DriverManager.getConnection("jdbc:sqlserver://192.168.0.14:1433;databaseName=GIDS_BTS;selectMethod=cursor", "sa", "galileo");
			String update = "UPDATE sms SET is_sent = '1' WHERE (is_sent = '0')";
			Statement stUpd = db.createStatement();
			stUpd.execute(update);
			stUpd.close();
			db.close();
						
			db = DriverManager.getConnection("jdbc:sqlserver://192.168.0.14:1433;databaseName=GIDS_FCM;selectMethod=cursor", "sa", "galileo");
			stUpd = db.createStatement();
			stUpd.execute(update);
			stUpd.close();
			db.close();
			
		} catch (SQLException ex) {
			System.out.println("Database connection error : " + ex);
		}
	}
	
	public static void hrUpdate() {
		try {
			Connection db = DriverManager.getConnection("jdbc:postgresql://localhost/hr", "root", "invent2k");
			DatabaseMetaData dbmd = db.getMetaData();
			System.out.println("DB Name : " + dbmd.getDatabaseProductName());

			String mysql = "SELECT entity_id, department_role_id, employee_id, phone, person_title, surname, middle_name, first_name ";
			mysql += "date_of_birth, nationality, gender, marital_status, nation_of_birth, place_of_birth, location_id, ";
			mysql += "pay_group_id, identity_card, active, bank_account, bank_branch_id, language, org_id, appointment_date, ";
			mysql += "exit_date, contract, contract_period, basic_salary, currency_id, pay_scale_id, pay_scale_step_id, ";
			mysql += "employment_terms, objective, Interests, field_of_study, picture_file, height, weight, blood_group, "; 
			mysql += "allergies, details "; 
			mysql += "FROM employees ";
			mysql += "WHERE (entity_id = '1') AND (org_id = '0') ";
			mysql += "ORDER BY entity_id";

			mysql = "SELECT entity_id, person_title, surname, middle_name, first_name ";
			mysql += "FROM employees ";
			mysql += "WHERE (entity_id = '1')";

			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mysql);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				System.out.println(rs.getString("entity_id")  + " : " + rs.getString("person_title"));

				rs.updateString("person_title", "Mr");
				rs.updateRow();
				rs.moveToCurrentRow();
			}

System.out.println("BASE 400");

			mysql = "UPDATE employees SET person_title = 'Miss' WHERE (entity_id = '938')";
			Statement stUpd = db.createStatement();
			stUpd.execute(mysql);

			rs.close();
			st.close();
			db.close();
		} catch (SQLException ex) {
			System.out.println("Database connection error : " + ex);
		}
	}
}

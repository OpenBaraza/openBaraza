
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class pfsa {

	public static void main(String args[]) {
		Update();
	}

	
	public static void Update() {
		try {
			Connection db = DriverManager.getConnection("jdbc:postgresql://localhost/hr", "postgres", "Invent2k");
			DatabaseMetaData dbmd = db.getMetaData();
			System.out.println("DB Name : " + dbmd.getDatabaseProductName());

			String mySql = "SELECT entity_id, first_name, middle_name, surname "
				+ "FROM employees "
				+ "ORDER BY entity_id ";

			Statement st1 = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs1 = st1.executeQuery(mySql);
			Map<Integer, List<String>> mEmp = new HashMap<Integer, List<String>>();
			while (rs1.next()) {
				List<String> lNames = new ArrayList<String>();
				lNames.add(rs1.getString("first_name").replace("'", "").trim().toUpperCase());
				lNames.add(rs1.getString("surname").replace("'", "").trim().toUpperCase());
				if(rs1.getString("middle_name") != null) {
					lNames.add(rs1.getString("middle_name").replace("'", "").trim().toUpperCase());
				}
				mEmp.put(rs1.getInt("entity_id"), lNames);
			}

			for(int mon = 1; mon <= 12; mon++) {
				Update(db, mEmp, mon);
			}

System.out.println("BASE 400");

			rs1.close();
			st1.close();
			db.close();
		} catch (SQLException ex) {
			System.out.println("Database connection error : " + ex);
		}
	}

	public static void Update(Connection db, Map<Integer, List<String>> mEmp, int mon) {
		String table;
		if(mon < 10) table = "imp_m0" + mon;
		else table = "imp_m" + mon;

		try {
			String mySql = "SELECT " + table + "_id, entity_id, employee_name, gross_pay "
				+ "FROM " + table;

			Statement st2 = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs2 = st2.executeQuery(mySql);
			while (rs2.next()) {

				String employeeName = rs2.getString("employee_name");
				employeeName = employeeName.replace(",", "").replace("  ", " ").replace("'", "");
				employeeName = employeeName.trim().toUpperCase();
				System.out.println(employeeName);

				String[] aNames = employeeName.split(" ");
				for(Integer eId : mEmp.keySet()) {		// Get a list of employees
					List<String> lNames = mEmp.get(eId);

					int cName = 0;
					for (String eName : aNames) {
						if(lNames.contains(eName)) cName++;
					}

					if(cName > 1) {
						System.out.println(employeeName + " " + cName + " " + eId);

						rs2.updateInt("entity_id", eId);
						rs2.updateRow();
						rs2.moveToCurrentRow();
					}
				}
			}

			rs2.close();
			st2.close();
		} catch (SQLException ex) {
			System.out.println("Database connection error : " + ex);
		}
	}
}

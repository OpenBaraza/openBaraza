import java.sql.*;

public class boq {

	public static void main(String args[]) {
		Update();
	}
	
	public static void Update() {
		try {
			Connection db = DriverManager.getConnection("jdbc:postgresql://sandbox/corban", "postgres", "Invent2k");
			DatabaseMetaData dbmd = db.getMetaData();
			System.out.println("DB Name : " + dbmd.getDatabaseProductName());

			String mySql = "INSERT INTO phases (project_id, org_id, phase_name, start_date, phase_deadline) "
				+ "SELECT project_id, 0, elements || ' ' || category, current_date, current_date "
				+ "FROM imp_BOQ "
				+ "WHERE elements is not null "
				+ "ORDER BY line_no; ";
			executeQuery(db, mySql);

			mySql = "UPDATE imp_BOQ SET phase_id = phases.phase_id, is_element = true "
				+ "FROM phases "
				+ "WHERE imp_BOQ.elements || ' ' || imp_BOQ.category = phases.phase_name "
				+ "AND imp_BOQ.project_id = phases.project_id; ";
			executeQuery(db, mySql);

			mySql = "UPDATE imp_BOQ SET quantity = 0 WHERE quantity is null;";
			executeQuery(db, mySql);

			mySql = "UPDATE imp_BOQ SET rate = 0 WHERE rate is null;";
			executeQuery(db, mySql);

			mySql = "SELECT imp_boq_id, phase_id, elements, category "
				+ "FROM imp_boq "
				+ "ORDER BY project_id, line_no ";
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mySql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int phaseId = 0;
			String element = null;
			String category = null;
			while (rs.next()) {
				if(rs.getString("elements")  == null) {
					rs.updateString("elements", element);
					rs.updateRow();
					rs.moveToCurrentRow();
				} else {
					element = rs.getString("elements");
				}
				
				if(rs.getString("phase_id")  == null) {
					rs.updateInt("phase_id", phaseId);
					rs.updateRow();
					rs.moveToCurrentRow();
				} else {
					phaseId = rs.getInt("phase_id");
				}
			}

System.out.println("BASE 400");

			rs.close();
			st.close();

			mySql = "INSERT INTO bill_of_quantitys (org_id, boq_stage_id, phase_id, line_no, line_tag, "
				+ "narrative, quantity, units, price_estimate) "
				+ "SELECT 0, 1, phase_id, line_no::int, line_item, COALESCE(category, description), "
				+ "quantity::real, unit, rate::real "
				+ "FROM imp_BOQ "
				+ "WHERE is_element = false "
				+ "AND COALESCE(category, description) is not null; ";
			executeQuery(db, mySql);

			db.close();
		} catch (SQLException ex) {
			System.out.println("Database connection error : " + ex);
		}
	}

	public static void executeQuery(Connection db, String mysql) {
		try {
			Statement st = db.createStatement();
			st.execute(mysql);
			st.close();
		} catch (SQLException ex) {
			System.out.println("Database executeQuery error : " + ex);
		}
	}

}

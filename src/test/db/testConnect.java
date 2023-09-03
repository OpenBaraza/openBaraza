import java.sql.*;

public class testConnect {

	public static void main(String args[]) {
		try {
			String dbPath = "jdbc:sqlserver://192.168.3.11;database=SunSystemsData;user=statement;password=Staet:2018;";
			Connection db = DriverManager.getConnection(dbPath, "statement", "Staet:2018");
			
			String mySql = "SELECT * FROM EDU_A_SALFLDG "
				+ "WHERE (ACCNT_CODE = 'SMUEMU1621') AND (TRANS_DATETIME >= '2018-01-01') "
				+ "ORDER BY TRANS_DATETIME";
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mySql);
			
			while(rs.next()) {
				System.out.println(rs.getString("TRANS_DATETIME"));
			}
			
			rs.close();
			st.close();
			db.close();
		} catch (SQLException ex) {
			System.out.println("Database connection error : " + ex);
		}
	}
	
	
}

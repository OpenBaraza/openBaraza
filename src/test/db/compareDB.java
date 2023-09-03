
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class compareDB {

	Connection mdb = null;
	Connection cdb = null;
	
	public compareDB(String mdbPath, String cdbPath, String userName, String password) {
		try {
			mdb = DriverManager.getConnection(mdbPath, userName, password);
			cdb = DriverManager.getConnection(cdbPath, userName, password);
			
			System.out.println("Connected both databases");
		} catch (SQLException ex) {
			System.out.println("Database connection error : " + ex);
		}
	}
	
	public List<String> getTableNames(int isType) {
		List<String> mltb = getTableNames(mdb, isType);
		List<String> cltb = getTableNames(cdb, isType);
		
		//for(String tb : mltb) System.out.println(tb);
		
		mltb.removeAll(cltb);
		
		for(String tb : mltb) System.out.println(tb);
		
		return mltb;
	}
	
	public List<String> getFunctionNames() {
		List<String> mltb = getFunctionNames(mdb);
		List<String> cltb = getFunctionNames(cdb);
		
		for(String tb : mltb) System.out.println(tb);
		
		mltb.removeAll(cltb);
		
		for(String tb : mltb) System.out.println(tb);
		
		return mltb;
	}
	
	public List<String> getTriggerNames() {
		List<String> mltb = getTriggerNames(mdb);
		List<String> cltb = getTriggerNames(cdb);
		
		for(String tb : mltb) System.out.println(tb);
		
		mltb.removeAll(cltb);
		
		for(String tb : mltb) System.out.println(tb);
		
		return mltb;
	}
	
	public List<String> getFieldNames(int isType) {
		List<String> mltb = getTableNames(mdb, isType);
		List<String> cltb = getTableNames(cdb, isType);
		
		List<String> mmFields = new ArrayList<String>();
		for(String tb : mltb) mmFields.addAll(getFieldNames(mdb, tb));
		
		List<String> cmFields = new ArrayList<String>();
		for(String tb : cltb) cmFields.addAll(getFieldNames(cdb, tb));
		
		mmFields.removeAll(cmFields);
		
		//for(String field : mmFields) System.out.println(field);
		
		return mmFields;
	}
	
	public List<String> getTableNames(Connection conn, int isType) {
		List<String> lsTables = new ArrayList<String>();
		
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			String[] types = {"TABLE"};
			if(isType == 1) types[0] = "VIEW";
			if(isType == 2) types[0] = "TRIGGER";
			ResultSet rs = dbmd.getTables(null, null, "%", types);

			while(rs.next()) {
				String table_schema = rs.getString("TABLE_SCHEM");
				String table_name = rs.getString("TABLE_NAME");
				lsTables.add(table_schema + "." + table_name);
			}
			rs.close();
		} catch (SQLException ex) {
			System.out.println("Database error : " + ex);
		}
		
		return lsTables;
	}
	

	public List<String> getFunctionNames(Connection conn) {
		List<String> lsFunctions = new ArrayList<String>();
		
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getFunctions(null, "%", "%");

			while(rs.next()) {
				String functionSchema = rs.getString("FUNCTION_SCHEM");
				String functionName = rs.getString("FUNCTION_NAME");
				if(!functionSchema.equals("pg_catalog")) lsFunctions.add(functionSchema + "." + functionName);
			}
			rs.close();
		} catch (SQLException ex) {
			System.out.println("Database error : " + ex);
		}
		
		return lsFunctions;
	}
	
	public List<String> getTriggerNames(Connection conn) {
		List<String> lTriggers = new ArrayList<String>();
		
		try {
			String trigerSql = "SELECT event_object_table, trigger_name, action_timing, event_manipulation, action_statement "
				+ "FROM  information_schema.triggers ORDER BY event_object_table, event_manipulation";
			Statement stmt = conn.createStatement();
			ResultSet rst = stmt.executeQuery(trigerSql);

			while(rst.next()) {
				String strTrigger = rst.getString("event_object_table") + " " + rst.getString("trigger_name") + " " 
					+ rst.getString("action_timing") + " " + rst.getString("event_manipulation") + " " + rst.getString("action_statement");
				lTriggers.add(strTrigger);
			}
			rst.close();
     	} catch (SQLException ex) {
			System.out.println("Database error : " + ex);
		}
		
		return lTriggers;
	}
	
	public List<String> getFieldNames(Connection conn, String tableName) {
		List<String> mFields = new ArrayList<String>();
		
		try {
			Date startTime = new Date();
			Statement stmt = conn.createStatement();
			ResultSet rst = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1");
			ResultSetMetaData rsmd = rst.getMetaData();
			int numberOfCols = rsmd.getColumnCount();
			Date stopTime = new Date();
			long timeDiff = stopTime.getTime() - startTime.getTime();
			
			System.out.println("Checking : " + tableName + " : " + timeDiff);

			for(int i = 1; i <= numberOfCols; i++) {
				String field_name = rsmd.getColumnName(i);
				String column_type = rsmd.getColumnTypeName(i);
				mFields.add(tableName + "." + field_name);
			}
			rst.close();
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("Database error : " + tableName + " : " + ex);
		}
		
		return mFields;
	}

	public void close() {
		try {
			if(mdb != null) mdb.close();
			if(mdb != null) mdb.close();
		} catch (SQLException ex) {
			System.out.println("Database closing error : " + ex);
		}
	}
	
}

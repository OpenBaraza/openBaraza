/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.DB;

import java.util.logging.Logger;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.math.BigDecimal;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONObject;
import org.json.JSONArray;

import org.baraza.xml.BElement;
import org.baraza.utils.BLogHandle;

public class BDB {
	Logger log = Logger.getLogger(BDB.class.getName());
    static Logger log2 = Logger.getLogger(BDB.class.getName());
	Connection db = null;
	DatabaseMetaData dbmd = null;
	String dbTemplate = null;
	String dbschema = null;
	int dbType = 1;
	String orgID = null;
	BUser user = null;
	BLogHandle logHandle = null;
	List<String> fullAudit;
	Map<String, String> configs;

	private String lastErrorMsg = null;
	private String lDBclass;
	private String lDBpath;
	private String lDBuser;
	private String lDBpassword;
	private boolean readOnly = false;
	
	public BDB(BElement dbconfig) {
		fullAudit =  new ArrayList<String>();
		String dbclass = dbconfig.getAttribute("dbclass", "");
		String dbpath = dbconfig.getAttribute("dbpath", ""); 
		String dbusername = dbconfig.getAttribute("dbusername", "");
		String dbpassword = dbconfig.getAttribute("dbpassword", "");
		dbTemplate = dbconfig.getAttribute("dbtemplate");
		dbschema = dbconfig.getAttribute("dbschema");
		orgID = dbconfig.getAttribute("org");
		if(dbconfig.getAttribute("readonly", "false").equals("true")) readOnly = true;

		connectDB(dbclass, dbpath, dbusername, dbpassword);
	}

	public BDB(BElement dbconfig, String dbuser, String dbpassword) {
		fullAudit =  new ArrayList<String>();
		String dbclass = dbconfig.getAttribute("dbclass", "");
		String dbpath = dbconfig.getAttribute("dbpath", ""); 
		dbTemplate = dbconfig.getAttribute("dbtemplate");
		dbschema = dbconfig.getAttribute("dbschema");
		orgID = dbconfig.getAttribute("org");

		connectDB(dbclass, dbpath, dbuser, dbpassword);
	}

	public BDB(String dbclass, String dbpath, String dbuser, String dbpassword) {
		fullAudit =  new ArrayList<String>();
		connectDB(dbclass, dbpath, dbuser, dbpassword);
	}

	// initialize the database and web output
	public BDB(String datasource) {
		connectDB(datasource);
	}

	public void connectDB(String datasource) {
		fullAudit =  new ArrayList<String>();
		try {
			InitialContext cxt = new InitialContext();
			DataSource ds = (DataSource) cxt.lookup(datasource);
			db = ds.getConnection();
			dbmd = db.getMetaData();
			String dbtype = dbmd.getDatabaseProductName();
			if(dbtype.toLowerCase().indexOf("oracle") >= 0) dbType = 2;
			if(dbtype.toLowerCase().indexOf("mysql") >= 0) dbType = 3;
		} catch (SQLException ex) {
			log.severe("Cannot connect to this database : datasource " + datasource + " : " + ex);
        } catch (NamingException ex) {
			log.severe("Cannot pick on the database : datasource " + datasource + " : " + ex);
        }
	}

	public void connectDB(String dbclass, String dbpath, String dbuser, String dbpassword) {
		if(dbclass.toLowerCase().indexOf("oracle")>=0) dbType = 2;
		if(dbclass.toLowerCase().indexOf("mysql")>=0) dbType = 3;

		lDBclass = dbclass;
		lDBpath = dbpath;
		lDBuser = dbuser;
		lDBpassword = dbpassword;

		try {
			Class.forName(dbclass);  
			db = DriverManager.getConnection(dbpath, dbuser, dbpassword);
			dbmd = db.getMetaData();

			if(dbschema != null) {
				Statement exst = db.createStatement();
				exst.execute("ALTER session set current_schema=" + dbschema);
				exst.close();
			}
		} catch (ClassNotFoundException ex) {
			log.severe("Cannot find the database driver classes. : path " + dbpath + " : " + ex);
		} catch (SQLException ex) {
			log.severe("Database connection SQL Error : path " + dbpath + " : " + ex);
		}
	}
	
	public void setSchema(String dbSchema) {
		this.dbschema = dbSchema;

		if(dbschema != null) {
			try {
				Statement exst = db.createStatement();
				exst.execute("ALTER session set current_schema=" + dbschema);
				exst.close();
			} catch (SQLException ex) {
				log.severe("Database connection SQL Error : " + ex);
			}
		}
	}

	public void reconnect(String datasource) {
		close();
		connectDB(datasource);
	}

	public void reconnect() {
		close();
		connectDB(lDBclass, lDBpath, lDBuser, lDBpassword);
	}
	
	public void newUser(String userIP, String userName) {
		user = new BUser(this, userIP, userName, true);
	}

	public void setUser(String userIP, String userName) {
		user = new BUser(this, userIP, userName);
	}

	public void setUser(String userIP, String userName, String narrative) {
		user = new BUser(this, userIP, userName);
		String mysql = "INSERT INTO sys_logins (entity_id, login_ip, narrative) VALUES ('";
		mysql += user.getUserID() + "', '" + userIP + "', '" + narrative + "')";
		executeQuery(mysql);
	}

	public void setUser(String tableName, String idCol, String nameCol, String userName) {
		user.setUser(this, tableName, idCol, nameCol, userName);
	}

	public void logConfig(BLogHandle logHandle) {
		this.logHandle = logHandle;
		logHandle.config(log);
	}

	public BLogHandle getLogHandle() {
		return logHandle;
	}

	public ResultSet readQuery(String mysql) {
		return readQuery(mysql, -1);
	}

	public ResultSet readQuery(String mysql, int limit) {
		ResultSet rs = null;

		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			if(limit > 0) st.setFetchSize(limit);
			rs = st.executeQuery(mysql);
		} catch (SQLException ex) {
			log.severe("Database readQuery error : " + ex);
		}

		return rs;
	}

	public String executeFunction(String mysql) {
		String ans = null;

		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mysql);

			if(rs.next()) ans = rs.getString(1);
			rs.close();
			st.close();
		} catch (SQLException ex) {
			ans = null;
			lastErrorMsg = ex.getMessage();
			log.severe("Database executeFunction error : " + ex);
			log.severe("SQL : " + mysql);
		}

		return ans;
	}
	
	public String executeFunction(String mysql, boolean readOnly) {
		String ans = null;

		Statement st = null;
		ResultSet rs = null;
		try {
			st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery(mysql);

			if(rs.next()) ans = rs.getString(1);
		} catch (SQLException ex) {
			ans = null;
			lastErrorMsg = ex.getMessage();
			log.severe("Database executeFunction error : " + ex);
		} finally {
			if (rs != null) {
				try { rs.close(); } catch (Exception ex) { }
				rs = null;
			}
			if (st != null) {
				try { st.close(); } catch (Exception ex) { }
				st = null;
			}
		}

		// Close the handles
		try {
			if(rs != null) rs.close();
			if(st != null) st.close();
		} catch (SQLException ex) {}

		return ans;
	}

	public String executeQuery(String mysql) {
		String rst = null;

		try {
			Statement st = db.createStatement();
			st.execute(mysql);
			st.close();
		} catch (SQLException ex) {
			rst = ex.toString();
			lastErrorMsg = ex.toString();
			log.severe("Database executeQuery error : " + ex);
		}

		return rst;
	}

	public String executeAutoKey(String mysql) {
		String rst = null;

		try {
			Statement st = db.createStatement();
			st.execute(mysql, Statement.RETURN_GENERATED_KEYS);

			ResultSet rsa = st.getGeneratedKeys();
			if(rsa.next()) rst = rsa.getString(1);

			rsa.close();
			st.close();
		} catch (SQLException ex) {
			rst = null;
			lastErrorMsg = ex.toString();
			log.severe("Database executeAutoKey error : " + ex);
		}

		return rst;
	}

	public String executeUpdate(String updsql) {
		String rst = null;

		try {
			Statement stUP = db.createStatement();
			stUP.executeUpdate(updsql);
			stUP.close();
		} catch (SQLException ex) {
			rst = ex.toString();
			lastErrorMsg = ex.getMessage();
			System.err.println("Database transaction get data error : " + ex);
		}

		return rst;
	}

	public String executeBatch(String mysql) {
		String rst = null;

		try {
			Statement st = db.createStatement();
			String[] lines = mysql.split(";");
			for(String line : lines) {
				if(!"".equals(line.trim()))
					st.addBatch(line);
			}
			st.executeBatch();
			st.close();
		} catch (SQLException ex) {
			rst = ex.toString();
			log.severe("Database executeBatch error : " + ex);
		}

		return rst;
	}

	public Clob createClob() {
		Clob clb = null;
		try {
			clb = db.createClob();
		} catch (SQLException ex) {
			log.severe("Clob Creation error : " + ex);
		}

		return clb;
	}
	
	public Map<String, String> getMapData(String keyField, String valueField, String mySource) {
		Map<String, String> ans = new HashMap<String, String>();

		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery("SELECT " + keyField + ", " + valueField + " FROM " + mySource);

			while(rs.next()) {
				ans.put(rs.getString(keyField), rs.getString(valueField));
			}

			rs.close();
			st.close();
		} catch (SQLException ex) {
			lastErrorMsg = ex.getMessage();
			log.severe("Database executeFunction error : " + ex);
		}

		return ans;
	}

	public Map<String, String> getFieldsData(String fields[], String mysql) {
		Map<String, String> ans = new HashMap<String, String>();

		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mysql);

			if(rs.next()) {
				for(String field : fields) ans.put(field.trim(), rs.getString(field.trim()));
			}

			rs.close();
			st.close();
		} catch (SQLException ex) {
			lastErrorMsg = ex.getMessage();
			log.severe("Database executeFunction error : " + ex);
		}

		return ans;
	}

	public Map<String, String> readFields(String myFields, String mySource) {
		String fields[] = myFields.split(",");
		Map<String, String> ans = getFieldsData(fields, "SELECT " + myFields + " FROM " + mySource);

		return ans;
	}
	
	public Vector<Vector<String>> readTable(String mySql) {
		Vector<Vector<String>> ans = new Vector<Vector<String>>();
		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mySql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			while(rs.next()) {
				Vector<String> rec = new Vector<String>();
				for(int i = 1; i <= colCount; i++) rec.add(rs.getString(i));
				ans.add(rec);
			}

			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database executeFunction error : " + ex);
		}

		return ans;
	}

	public Vector<Vector<String>> readTable(String myFields, String mysource) {
		String fields[] = myFields.split(",");
		String mySql = "SELECT " + myFields + " FROM " + mysource;

		Vector<Vector<String>> ans = new Vector<Vector<String>>();
		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mySql);
			while(rs.next()) {
				Vector<String> rec = new Vector<String>();
				for(String field : fields) rec.add(rs.getString(field.trim()));
				ans.add(rec);
			}

			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database executeFunction error : " + ex);
		}

		return ans;
	}
	
	public JSONArray jsonTable(String myFields, String mySource) {
		String fields[] = myFields.split(",");
		String mySql = "SELECT " + myFields + " FROM " + mySource;
		log.info("JSON SQL : " + mySql);

		JSONArray ans = new JSONArray();
		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mySql);
			while(rs.next()) {
				JSONObject jo = new JSONObject();
				for(String field : fields) jo.put(field.trim(), rs.getString(field.trim()));
				ans.put(jo);
			}
			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database executeFunction error : " + ex);
			log.severe("SQL " + mySql);
		}

		return ans;
	}
	
	public Vector<String> readColumn(String mySql) {
		Vector<String> ans = new Vector<String>();
		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mySql);
			while(rs.next()) ans.add(rs.getString(1));
			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database executeFunction error : " + ex);
		}
		return ans;
	}

	public Map<String, String> readRecord(String myFields, String mySource) {
		String fields[] = myFields.split(",");
		String mySql = "SELECT " + myFields + " FROM " + mySource;

		Map<String, String> ans = new HashMap<String, String>();
		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mySql);
			if(rs.next()) {
				for(String field : fields)
					ans.put(field.trim(), rs.getString(field.trim()));
			}
			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database executeFunction error : " + ex);
		}

		return ans;
	}
	
	public Vector<Map<String, String>> readRecords(String myFields, String mySource) {
		String fields[] = myFields.split(",");
		String mySql = "SELECT " + myFields + " FROM " + mySource;

		Vector<Map<String, String>> ans = new Vector<Map<String, String>>();
		try {
			Statement st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mySql);
			while(rs.next()) {
				Map<String, String> rec = new HashMap<String, String>();
				for(String field : fields) 
					rec.put(field.trim(), rs.getString(field.trim()));
				ans.add(rec);
			}
			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database executeFunction error : " + ex);
		}

		return ans;
	}
	
	// Get the table names
	public List<String> getTables() {
		List<String> tableList = new ArrayList<String>();
		try {
			String[] types = {"TABLE"};
        	ResultSet rs = dbmd.getTables(null, dbschema, "%", types);
    		while (rs.next()) {
				String tableName = rs.getString(3);
				if(tableName.indexOf("$")<0)
					tableList.add(tableName);
			}
			rs.close();
		} catch (SQLException ex) {
			log.severe("Table Listing error : " + ex);
		}

		return tableList;
	}

	// Get the view names
	public List<String> getViews() {
		List<String> viewList = new ArrayList<String>();
		try {
			String[] types = {"VIEW"};
        	ResultSet rs = dbmd.getTables(null, dbschema, "%", types);
    		while (rs.next()) viewList.add(rs.getString(3));
			rs.close();
		} catch (SQLException ex) {
			log.severe("Table Listing error : " + ex);
		}

		return viewList;
	}

    public List<BTableLinks> getForeignLinks(String tablename) {
		List<BTableLinks> fkList = new ArrayList<BTableLinks>();

   		try {
			ResultSet tablemd = dbmd.getImportedKeys(null, null, tablename);

			while(tablemd.next()) {
				fkList.add(new BTableLinks(tablemd.getString(7), tablemd.getString(8), tablemd.getString(3), tablemd.getString(4)));
				//System.out.println(tablemd.getString(7) + "." + tablemd.getString(8) + " = " + tablemd.getString(3) + "." + tablemd.getString(4));
			}
			tablemd.close();
		} catch (SQLException ex) {
			log.severe("Table Listing error : " + ex);
		}

		return fkList;
	}

	public BElement getAppConfig(BElement root) {
		try {
			String[] types = {"TABLE"};
        	ResultSet rs = dbmd.getTables(null, null, "%", types);
			
			// Make the menu
			Integer i = 1;
			BElement menu = new BElement("MENU");
			menu.setAttribute("name", root.getAttribute("name"));
    		while (rs.next()) {
				String tableName = rs.getString(3);
				if(!tableName.toLowerCase().startsWith("sys_")) {
					BElement mel = new BElement("MENU");
					mel.setAttribute("name", initCap(tableName));
					mel.setValue((i++).toString());
					menu.addNode(mel);
				}
			}
			root.addNode(menu);
			rs.close();

			i = 1;
			rs = dbmd.getTables(null, null, "%", types);
			while (rs.next()) {
				String tableName = rs.getString(3);
				BQuery query = new BQuery(this, "*", tableName, 2);

				if(!tableName.toLowerCase().startsWith("sys_")) {
					BElement del = new BElement("DESK");
					del.setAttribute("h", "500");
					del.setAttribute("w", "700");
					del.setAttribute("name", initCap(tableName));
					del.setAttribute("key", (i++).toString());
					del.addNode(query.getDeskConfig(0));
					root.addNode(del);
				}
				query.close();
			}
			rs.close();
		} catch (SQLException ex) {
			log.severe("App Config Creation error : " + ex);
		}

		return root;
	}

	public void createdb(String dbName) {
		String mysql = "CREATE DATABASE " + dbName;
		if(dbTemplate != null) mysql += " TEMPLATE " + dbTemplate;
		
		executeQuery(mysql);
	}

	public void dropdb(String dbName) {	
		String mysql = "DROP DATABASE " + dbName;
		
		executeQuery(mysql);
	}

	public String getViewSQL() {	
		String views = "";
		try {
			// Get the table name
			String[] tabletypes = {"TABLE"};
        	ResultSet tablers = dbmd.getTables(null, dbschema, "%", tabletypes);			
    		while (tablers.next()) {
            	String tableName = tablers.getString(3);
				views += getViewSQL(tableName);
        	}

			tablers.close();
        } catch (SQLException ex) {
        	log.severe("SQL Error : " + ex);
        }

		return views;	
	}

	public String getViewSQL(String tablename) {
		String mystr = "\n\nCREATE VIEW vw_" + tablename + " AS";
		mystr += "\n\tSELECT ";
		try {
			String mysql = "SELECT * FROM " + tablename;
			ResultSet tablemd = dbmd.getImportedKeys(null, null, tablename);
			Statement st = db.createStatement();
			st.setFetchSize(50);
			ResultSet rs = st.executeQuery(mysql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int colnum = rsmd.getColumnCount();    // Get column numbers
			boolean linked = false;

			List<String> fieldNames = new ArrayList<String>();

			String strfrom = "\n\tFROM " + tablename; 
			while(tablemd.next()) {
				if(linked) mystr += ", ";
				mystr += tablemd.getString(3) + "." + tablemd.getString(4) + ", ";
				mystr += tablemd.getString(3) + "." + tablemd.getString(4).replaceFirst("id", "name");
				fieldNames.add(tablemd.getString(4));

				strfrom += "\n\tINNER JOIN " + tablemd.getString(3);
				strfrom += " ON " + tablename + "." + tablemd.getString(8);
				strfrom += " = " + tablemd.getString(3) + "." + tablemd.getString(4);
								
				linked = true;
			}
			
			if(linked) {
				for (int column=1; column <= colnum; column++) {
					if(!fieldNames.contains(rsmd.getColumnLabel(column)))
						mystr += ", " + tablename + "." + rsmd.getColumnLabel(column);
				}

				mystr += strfrom + ";";
			} else {
				for (int column=1; column <= colnum; column++) {
					if(column > 1) mystr += ", ";
					mystr += tablename + "." + rsmd.getColumnLabel(column);
				}
				mystr += strfrom + ";";
			}

			rs.close();
			st.close();
			tablemd.close();			
		} catch (SQLException ex) {
			log.severe("Function getViewSQL Error : " + ex);
		}

		return mystr;
	}
	
	public String getAddFieldSQL(String tablename, List<String> lFields) {
		String myStr = "ALTER TABLE " + tablename;
		try {
			String mysql = "SELECT * FROM " + tablename;
			Statement st = db.createStatement();
			st.setFetchSize(10);
			ResultSet rs = st.executeQuery(mysql);
			ResultSet rsIk = dbmd.getImportedKeys(null, null, tablename);
			ResultSetMetaData rsmd = rs.getMetaData();
			int colnum = rsmd.getColumnCount();
			
			mysql = " information_schema.columns "
				+ "WHERE ((table_schema, table_name) = ('public', '" + tablename + "')) AND (column_default is not null)";
			Map<String, String> mDefaultValue = getMapData("column_name", "column_default", mysql);
			
			Map<String, Integer> mFieldCols = new HashMap<String, Integer>();
			for (int col = 1; col <= colnum; col++) {
				if(lFields.contains(rsmd.getColumnName(col))) {
					mFieldCols.put(rsmd.getColumnName(col), col);
				}
			}
			
			Map<String, String> mImportedKeys = new HashMap<String, String>();
			while(rsIk.next()) mImportedKeys.put(rsIk.getString(8), rsIk.getString(3));
			
			boolean isFirst = true;
			for(String fieldName : lFields) {
				int col = mFieldCols.get(fieldName);
				int colType = rsmd.getColumnType(col);
				
				if(isFirst) isFirst = false;
				else myStr += ",";
				
				myStr += "\nADD " + fieldName;
				if(colType == 1) myStr += " char";
				else if(colType == 4) myStr += " integer";
				else if(colType == 7) myStr += " real";
				else if(colType == -7) myStr += " boolean";
				else myStr += " " + rsmd.getColumnTypeName(col);
				
				if(((colType == 1) || (colType == 12)) && (!"text".equals(rsmd.getColumnTypeName(col)))) {
					myStr += "(" + rsmd.getColumnDisplaySize(col) + ")";
				}
				
				if(mImportedKeys.containsKey(fieldName)) myStr += " references " + mImportedKeys.get(fieldName);
				
				if(mDefaultValue.containsKey(fieldName)) myStr += " default " + mDefaultValue.get(fieldName);
				
				if(rsmd.isNullable(col) == 0) myStr += " not null"; 
				
				//myStr += " " + rsmd.getColumnType(col);
			}
			myStr += ";\n";
			
			rsIk.close();
			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Function getViewSQL Error : " + ex);
		}

		return myStr;
	}
	
	

	public String initCap(String mystr) {
		if(mystr != null) {
			String[] mylines = mystr.toLowerCase().split("_");
			mystr = "";
			for(String myline : mylines) {
				if(myline.length()>0)
					myline = myline.replaceFirst(myline.substring(0, 1), myline.substring(0, 1).toUpperCase());
				mystr += myline + " ";
			}
			mystr = mystr.trim();
		}
		return mystr;
	}
	
	public String getCatalogName() {
		String catalogName = null;
		try {
			catalogName = db.getCatalog();
		} catch(SQLException ex) {
			log.severe("Database name : " + ex);
		}
		
		return catalogName;
	}

	public boolean isValid() {
		boolean dbv = false;
		try {
			if(db != null) {
				Statement tst = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet trs = tst.executeQuery("SELECT 1;");
				if(trs.next()) dbv = true;
				trs.close();
				tst.close();
			}
		} catch (SQLException ex) {
			log.severe("DB Validation Error : " + ex);
		}
		return dbv;
	}
	
	public void setFullAudit(BElement audit) {
		for(BElement aTables : audit.getElements()) {
			fullAudit.add(aTables.getValue());
		}
	}
	
	public boolean isFullAudit(String tableName) {
		return fullAudit.contains(tableName);
	}

	public String insAudit(String tableName, String recordID, String functionType) {
		String insSql = "INSERT INTO sys_audit_trail (user_id, user_ip, table_name, record_id, change_type) VALUES('"
			+ getUserID() + "', '" + getUserIP() + "', '" + tableName + "', '"
			+ recordID  + "', '" + functionType + "')";
		String autoKeyID = executeAutoKey(insSql);
		return autoKeyID;
	}

	public String insAudit(String tableName, String recordID, String functionType, BUser qUser) {
		String insSql = "INSERT INTO sys_audit_trail (user_id, user_ip, table_name, record_id, change_type) VALUES('"
			+ qUser.getUserID() + "', '" + qUser.getUserIP() + "', '" + tableName
			+ "', '" + recordID  + "', '" + functionType + "')";
		String autoKeyID = executeAutoKey(insSql);
		return autoKeyID;
	}
	
	public void insAuditDetails(String auditId, String oldValues) {
		String inssql = "INSERT INTO sys_audit_details (sys_audit_trail_id, old_value) VALUES('";
		inssql += auditId + "', '" + oldValues + "')";
		executeQuery(inssql);
	}
	
	public String getDefaultValue(BElement el) {
		return getDefaultValue(el, user);
	}
	
	public String getDefaultValue(BElement el, BUser currUser) {
		String defaultValue = el.getAttribute("default", "");
		String default_fnct = el.getAttribute("default_fnct");
		String default_org_fnct = el.getAttribute("default_org_fnct");
		String default_user = el.getAttribute("default_user");
		if(default_fnct != null) {
			if(default_fnct.indexOf("(") > 1) defaultValue = executeFunction("SELECT " + default_fnct + ", '" + currUser.getUserID() + "')");
			else defaultValue = executeFunction("SELECT " + default_fnct + "('" + currUser.getUserID() + "')");
		} else if(default_org_fnct != null) {
			if(default_org_fnct.indexOf("(") > 1) defaultValue = executeFunction("SELECT " + default_org_fnct + ", " + currUser.getUserOrg() + ")");
			else defaultValue = executeFunction("SELECT " + default_org_fnct + "(" + currUser.getUserOrg() + ")");
		} else if(default_user != null) {
			defaultValue = currUser.getUserID();
		}
		
		return defaultValue;
	}
    
    
    public static DataSource getDataSource(String datasource) {
		DataSource ds = null;
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(datasource);//"java:comp/env/jdbc/database"
		} catch (NamingException e) {
            log2.severe("Unable to create DataSource : " + e.toString());
		}

		return ds;
	}

	public static Connection getConnection(String datasource) {
        Connection con = null;
		try {
			con = getDataSource(datasource).getConnection();
		} catch (SQLException e) {
            log2.severe("Unable to get Connection : " + e.toString());
		}
		return con;
	}
    
    public static PreparedStatement getStatement(String datasource, String sql) {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = getConnection(datasource).prepareStatement(sql);
		} catch (SQLException e) {
            log2.severe("Unable to Prepare Statement : " + e.toString());
		} catch (Exception ex) {
            log2.severe("Error Preparing Statement : " + ex.toString());
		}
		return preparedStatement;
	}
    
    public static PreparedStatement getStatement(Connection con, String sql) {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = con.prepareStatement(sql);
		} catch (SQLException e) {
            log2.severe("Unable to Prepare Statement : " + e.toString());
		} catch (Exception ex) {
            log2.severe("Error Preparing Statement : " + ex.toString());
		}
		return preparedStatement;
	}
    
    
    public static Integer executeStatement(PreparedStatement preparedStatement) {
		Integer es = null;
		try {
			es = preparedStatement.executeUpdate(); // execute insert statement
		} catch (SQLException e) {
            log2.severe("Error Executing Statement" + e.getMessage());
			es = null;
		} catch (Exception e1) {
            log2.severe("Error Executing Prepared Statement : " + e1.toString());
			es = null;
		} finally {
			if (preparedStatement != null) {
				Connection conn = null;
				try {
					conn = preparedStatement.getConnection();
                    log2.info("Connection Retrievd");
				} catch (SQLException e) {
                    log2.severe("Failed To Retrieve Connection : " + e.toString());
				}

				try {
					preparedStatement.close();
                    log2.info("Statement Closed");
				} catch (SQLException e) {
                    log2.severe("Error Closing Statement : " + e.toString());
				}

				if (conn != null) {
					try {
						conn.close();
                        log2.info("Connection Closed");
					} catch (SQLException e) {
                        log2.severe("Errot Closing Connection : " + e.toString());
					}
				}
			}
		}
		return es;
	}
	
	public String saveRec(String inSql, Map<String, String> addNewBlock) {
		String errMsg = null;
		String keyFieldId = null;
		String logFieldName = "";

		log.fine("BASE 100 : " + inSql);

		try {
			PreparedStatement ps = db.prepareStatement(inSql, Statement.RETURN_GENERATED_KEYS);
			ResultSetMetaData psmd = ps.getMetaData();
			Map<String, Integer> mFieldCols = new HashMap<String, Integer>();
			for(int i = 1;  i <= psmd.getColumnCount(); i++) mFieldCols.put(psmd.getColumnName(i), i);
		
			String fValue = "";
			int colIndex = 1;
			int typeColIndex = 1;
			int fType = -1;
			int colLen = -1;
			for (String fieldName : addNewBlock.keySet()) {
				logFieldName = fieldName;
				fValue = addNewBlock.get(fieldName);
				typeColIndex = mFieldCols.get(fieldName.toLowerCase());
				fType = psmd.getColumnType(typeColIndex);
				colLen = psmd.getColumnDisplaySize(typeColIndex);
				log.fine("BASE 1010 : " + colIndex + " : " + fieldName + " : " + fValue + " : " + fType + " : " + colLen);

				if(fValue == null) {
					ps.setNull(colIndex, fType);
			    } else if(fValue.length()<1) {
					ps.setNull(colIndex, fType);
			    } else {
					switch(fType) {
		    			case Types.CHAR:
		    			case Types.VARCHAR:
		    			case Types.LONGVARCHAR:
							if(colLen < fValue.length()) {
								if(errMsg == null) errMsg = fieldName + " is too long maximum is : " + colLen;
								else errMsg += "\n<br>" + fieldName + " is too long maximum is : " + colLen;
							}
		        			ps.setString(colIndex, fValue);
							break;
		   				case Types.BIT:
							if(fValue.equals("true")) ps.setBoolean(colIndex, true);
							else ps.setBoolean(colIndex, false);
							break;
		    			case Types.TINYINT:
		    			case Types.SMALLINT:
		    			case Types.INTEGER:
							int ivalue = Integer.valueOf(fValue).intValue();
							ps.setInt(colIndex, ivalue);
							break;
						case Types.NUMERIC:
							BigDecimal bdValue = new BigDecimal(fValue);
							ps.setBigDecimal(colIndex, bdValue);
							break;
				    	case Types.BIGINT:
							long lvalue = Long.valueOf(fValue).longValue();
							ps.setLong(colIndex, lvalue);
							break;
				    	case Types.FLOAT:
				    	case Types.DOUBLE:
						case Types.REAL:
							double dvalue = Double.valueOf(fValue).doubleValue();
							ps.setDouble(colIndex, dvalue);
							break;
				    	case Types.DATE:
							java.sql.Date dtvalue = java.sql.Date.valueOf(fValue);
							ps.setDate(colIndex, dtvalue);
							break;
				    	case Types.TIME:
							java.sql.Time tvalue = Time.valueOf(fValue);
							ps.setTime(colIndex, tvalue);
							break;
						case Types.TIMESTAMP:
							java.sql.Timestamp tsvalue = java.sql.Timestamp.valueOf(fValue);
							ps.setTimestamp(colIndex, tsvalue);
							break;
						case Types.CLOB:
							Clob clb = db.createClob();
							clb.setString(1, fValue);
							ps.setClob(colIndex, clb);
							break;
						default:
		        			ps.setString(colIndex, fValue);
							break;
					}
				}
				colIndex++;
			}
			ps.executeUpdate();

			ResultSet rsb = ps.getGeneratedKeys();
			if(rsb.next()) keyFieldId = rsb.getString(1);
			rsb.close();
		} catch (SQLException ex) {
			Integer errCode = ex.getErrorCode();
			String errCodeMsg = ex.getMessage(); 
			
			if(errCodeMsg != null) {
				int ePos = errCodeMsg.indexOf("PL/pgSQL");
				if(ePos > 7) errCodeMsg = errCodeMsg.substring(0, ePos - 7);
			}
			
			if(errMsg == null) errMsg = errCodeMsg + "\n";
			else errMsg += "\n<br>" + errCodeMsg + "\n";

			log.severe("The SQL Exeption on new record " + logFieldName + " : " + ex);
			log.severe("The error code " + errCode);
		} catch (NumberFormatException ex) {
			errMsg = logFieldName + " : " + ex.getMessage() + "\n";
			log.severe("Number format exception on field = " + logFieldName + " : value = " + addNewBlock.get(logFieldName) + " : " + ex);
		}

		return keyFieldId;
	}


	public Map<String, String> getConfigs(String configType) {
		Map<String, String> cfgs = new HashMap<String, String>();
		try {
			String mySql = "SELECT config_name, config_value FROM sys_configs WHERE config_type_id = " + configType;
			Statement st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery(mySql);
			while(rs.next()) cfgs.put(rs.getString("config_name"), rs.getString("config_value"));
			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database connection SQL Error : " + ex);
		}
		return cfgs;
	}

	public void makeConfigs() {
		configs = new HashMap<String, String>();
		try {
			String mySql = "SELECT config_name, config_value FROM sys_configs WHERE config_type_id = 1";
			Statement st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery(mySql);
			while(rs.next()) configs.put(rs.getString("config_name"), rs.getString("config_value"));
			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database connection SQL Error : " + ex);
		}
	}

	public Map<String, String> getConfigs() {
		return configs;
	}
	
	public String getConfig(String configName) {
		return configs.get(configName);
	}
    
	public String getSqlOrgWhere(String noorg) {
		String whereSql = null;
		String userOrg = getUserOrg();
		if((noorg == null) && (orgID != null) && (userOrg != null)) {
			whereSql = "(" + orgID + " = " + userOrg + ")";
		}
		return whereSql;
	}
	
	public String getSqlUserWhere(String userAttr) {
		String whereSql = null;
		if(userAttr != null) {
			whereSql = "(" +userAttr + " = '" + getUserID() + "')";
		}
		return whereSql;
	}

	public Connection getDB() { return db; }
	public DatabaseMetaData getDBMetaData() { return dbmd; }
	public int getDBType() { return dbType; }
	public BUser getUser() { return user; }
	public String getUserID() { return user.getUserID(); }
	public String getUserIP() { return user.getUserIP(); }
	public String getUserOrg() { return user.getUserOrg(); }
	public Integer getUserOrgId() { return user.getUserOrgId(); }
	public String getUserName() { return user.getUserName(); }
	public boolean getSuperUser() { return user.getSuperUser(); }
	public List<String> getUserRoles() { return user.getUserRoles(); }
	public List<String> getGroupRoles() { return user.getGroupRoles(); }
	public String getGroupID() { return user.getGroupID(); }
	public String getGroupIDs() { return user.getGroupIDs(); }

	public String getOrgID() { return orgID; }
	public void setOrgID(String orgID) { this.orgID = orgID; }
	public String getOrgWhere(String orgTable) { return user.getOrgWhere(orgTable); }
	public String getOrgAnd(String orgTable) { return user.getOrgAnd(orgTable); }
	public String getDBSchema() { return dbschema; }
	
	public String getWebLogos() { 
		if(user == null) return "";
		return user.getWebLogos(); 
	}

	public String getStartView() { return user.getStartView(); }

	public String getLastErrorMsg() {
		String lemsg = null;
		int lemPos = lastErrorMsg.indexOf("Where: PL/pgSQL");
		if((lastErrorMsg != null) && (lemPos > 0)) lemsg = lastErrorMsg.substring(0, lemPos);
		return lemsg; 
	}
	
	public void setReadOnly(boolean readOnly) { this.readOnly = readOnly; }
	public boolean getReadOnly() { return readOnly; }

	public void close() {
		try {
			if(db != null) db.close();
			db = null;
		} catch (SQLException ex) {
			log.severe("SQL Error : " + ex);
		}
	}

}

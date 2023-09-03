/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.ide;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JDesktopPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;

import org.baraza.DB.BDB;
import org.baraza.DB.BDBCompare;
import org.baraza.xml.BElement;
import org.baraza.utils.Bio;
import org.baraza.utils.BLogHandle;

public class BDBUpgrade extends JPanel implements ActionListener {
	Logger log = Logger.getLogger(BDataImport.class.getName());
	BLogHandle logHandle;
	BDB db = null;
	BDBCompare cDB = null;
	BElement desk;
	String dbFilesDir;
	JPanel controls, bodyPanel;
	JButton[] button;
	
	JTextArea area1, area2;
	JTextField txtCompDB;

	public BDBUpgrade(BLogHandle logHandle, BDB db, BElement desk, String dbFilesDir) {
		super(new BorderLayout());
		this.db = db;
		this.desk = desk;
		this.dbFilesDir = dbFilesDir;
		this.logHandle = logHandle;
		logHandle.config(log);

		controls = new JPanel(new GridLayout(2, 4));
		super.add(controls, BorderLayout.PAGE_START);
		
		JLabel lblFields = new JLabel("Upgrade : ");
		txtCompDB = new JTextField(20);
		txtCompDB.setText("jdbc:postgresql://localhost/upgr");
		controls.add(lblFields);
		controls.add(txtCompDB);

		String[] btArray = {"Connect Database", "Missing Tables", "Missing Fields", "Missing View Fields", "Missing Functions",
							"Missing Triggers", "Drop Views", "Get Tables"};
		button = new JButton[btArray.length];		
		for(int i = 0; i < btArray.length; i++) {
			button[i] = new JButton(btArray[i]);
			button[i].addActionListener(this);
			controls.add(button[i]);
		}
		
		//Creating a pagination
		area1 = new JTextArea();
		area2 = new JTextArea();
				
		JScrollPane scrollPane1 = new JScrollPane(area1);
		JScrollPane scrollPane2 = new JScrollPane(area2);
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Data", scrollPane1);
		tabPane.addTab("Query", scrollPane2);
		
		bodyPanel = new JPanel(new BorderLayout());
		bodyPanel.add(tabPane, BorderLayout.CENTER);
		super.add(bodyPanel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent ev) {
		String aKey = ev.getActionCommand();
		
		if(aKey.equals("Connect Database")) {
			connectDB();
		} else if(aKey.equals("Missing Tables")) {
			getMissingTables();
		} else if(aKey.equals("Missing Fields")) {
			getMissingFields();
		} else if(aKey.equals("Missing View Fields")) {
			getMissingViewFields();
		} else if(aKey.equals("Missing Functions")) {
			getMissingFunctions();
		} else if(aKey.equals("Missing Triggers")) {
			getMissingTriggers();
		} else if(aKey.equals("Drop Views")) {
			getDropViews();
		} else if(aKey.equals("Get Tables")) {
			getTables();
		}
	}

	public void connectDB() {
		String dbPath = desk.getAttribute("dbpath");
		String dbUsername = desk.getAttribute("dbusername");
		String dbPassword = desk.getAttribute("dbpassword");
		
		if(cDB != null) cDB.close();

		cDB = new BDBCompare(dbPath, txtCompDB.getText(), dbUsername, dbPassword);
	}
	
	public void getMissingTables() { 
		System.out.println("Check missing tables");
		
		if(cDB != null) {
			List<String> mTables = cDB.getTableNames(0);
			for(String mTable : mTables) area1.append(mTable + "\n");
			
			if(mTables.size() == 0) {
				area1.append("No missing tables\n");
			} else {
				System.out.println(dbFilesDir + "setup/setup.sql");
				Bio bio = new Bio();
				StringBuffer tableFile = new StringBuffer(bio.loadFile(dbFilesDir + "setup/setup.sql"));
				StringBuffer tableSql = new StringBuffer();
				
				for(String mTable : mTables) {
					int sPos = tableFile.indexOf("CREATE TABLE " + mTable) - 1;
					int ePos = tableFile.indexOf(");", sPos) + 2;
					
					if(sPos > 1) tableSql.append(tableFile.substring(sPos, ePos));

					sPos = 2;
					while(sPos > 1) {
						sPos = tableFile.indexOf("ALTER TABLE ONLY " + mTable + "\n    ADD CONSTRAINT", sPos + 15) - 1;
						if(sPos > 1) {
							ePos = tableFile.indexOf(");", sPos) + 2;
							String kfSql = tableFile.substring(sPos, ePos);
							if(kfSql.contains("PRIMARY KEY")) {
								tableSql.append(kfSql);
								sPos = 0;
							}
						}
					}
				}
				area2.append(tableSql.toString());
			}
		}
	}
	
	public void getMissingFields() { 
		System.out.println("Get missing fields");
		
		if(cDB != null) {
			List<String> mFields = cDB.getFieldNames(0);
			Map<String, List<String>> mMissingFields = new HashMap<String, List<String>>();
			for(String mField : mFields) {
				area1.append(mField + "\n");
				String[] aotc = mField.split("\\.");
				if(mMissingFields.containsKey(aotc[1])) {
					mMissingFields.get(aotc[1]).add(aotc[2]);
				} else {
					List<String> lmFields = new ArrayList<String>();
					lmFields.add(aotc[2]);
					mMissingFields.put(aotc[1], lmFields);
				}
			}
			if(mFields.size() == 0) area1.append("No missing table fields\n");
			
			// Get the fields only for tables in both databases
			List<String> mTables = cDB.getTableNames(0);
			for(String tableName : mMissingFields.keySet()) {
				if(!mTables.contains("public." + tableName)) {
					area2.append(db.getAddFieldSQL(tableName, mMissingFields.get(tableName)) + "\n");
				}
			}
		}
	}
	
	public void getMissingViewFields() { 
		System.out.println("Get missing fields");
		
		if(cDB != null) {
			List<String> mFields = cDB.getFieldNames(1);
			for(String mField : mFields) area1.append(mField + "\n");
			if(mFields.size() == 0) area1.append("No missing view fields\n");
		}
	}
	
	public void getMissingFunctions() { 
		System.out.println("Check missing Functions");
		
		if(cDB != null) {
			List<String> mTables = cDB.getFunctionNames();
			for(String mTable : mTables) area1.append(mTable + "\n");
			if(mTables.size() == 0) area1.append("No missing Functions\n");
		}
	}
	
	public void getMissingTriggers() { 
		System.out.println("Check missing Triggers");
		
		if(cDB != null) {
			List<String> mTables = cDB.getTriggerNames();
			for(String mTable : mTables) area1.append(mTable + "\n");
			if(mTables.size() == 0) area1.append("No missing Triggers\n");
		}
	}
	
	public void getDropViews() { 
		System.out.println("Get drop views");
		
		String viewSql = area1.getText();
		
		String[] lines = viewSql.split("\n");
		List<String> lTables = new ArrayList<String>();
		
		for(String line : lines) {
			if(line.toLowerCase().contains("create view")) {
				System.out.println(line);
				lTables.add(line.replace("CREATE", "DROP").replace(" AS", ";"));
			}
		}
		
		String strDrop = "";
		for(int i = lTables.size(); i > 0; i--) strDrop += lTables.get(i - 1) + "\n";
		area2.setText(strDrop);
	}
	
	public void getTables() { 
		System.out.println("Get Tables");
		
		String viewSql = area1.getText();
		
		String[] lines = viewSql.split("\n");
		List<String> lTables = new ArrayList<String>();
		
		for(String line : lines) {
			line = line.toLowerCase();
			if(line.contains("create table")) {
				System.out.println(line);
				String tableName = line.replace("create", "").replace("as", "").replace("(", "").replace("table", "").trim();
				lTables.add("pg_dump -U postgres -a --column-inserts -t " + tableName + " hr");
			}
		}
		
		String strDrop = "";
		for(int i = 0; i < lTables.size(); i++) {
			strDrop += lTables.get(i) + "\n";
			runProcess(lTables.get(i));
		}
		area1.setText(strDrop);
	}
	
	public void runProcess(String processCommand)  {
		try {
			Process process = Runtime.getRuntime().exec(processCommand);
		
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				if(line.startsWith("SET")) {}
				else if(line.startsWith("--")) {}
				else { System.out.println(line); }
			}
		} catch(IOException ex) {
			System.out.println("IO Error on process execution : " + ex);
		}
	}
	
	public void close() {
		if(cDB != null) cDB.close();
	}

}
	

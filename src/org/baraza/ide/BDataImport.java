/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.ide;

import java.util.logging.Logger;
import java.util.Vector;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.text.DecimalFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.baraza.DB.BDB;
import org.baraza.swing.BVectorTableModel;
import org.baraza.utils.Bio;
import org.baraza.utils.BLogHandle;

public class BDataImport extends JPanel implements ActionListener {
	Logger log = Logger.getLogger(BDataImport.class.getName());
	BLogHandle logHandle;
	BDB db;
	String projectDir;
	File excelFile = null;
	int workSheet = 0;
	
	JPanel controls, bodyPanel, pnFields, pnButtons;
	JButton[] button;
	JTextArea area1, area2;
	JTable table;
	JTextField[] txtFields;
	JTextField txtTableName;
	
	BVectorTableModel vectorTableMode;
	Vector<Vector<String>> myData;
	Vector<String> myTitles;

	public BDataImport(BLogHandle logHandle, BDB db, String projectDir) {
		super(new BorderLayout());
		this.db = db;
		this.projectDir = projectDir;
		this.logHandle = logHandle;
		logHandle.config(log);

		controls = new JPanel(new BorderLayout());
		super.add(controls, BorderLayout.PAGE_START);
		
		pnFields = new JPanel(new FlowLayout());
		String[] txtArray = {"Fields", "Columns", "Sub Titles", "Split Name", "Worksheet"};
		txtFields = new JTextField[txtArray.length];
		JLabel[] lblFields = new JLabel[txtArray.length];
		for(int i = 0; i < txtArray.length; i++) {
			lblFields[i] = new JLabel(txtArray[i] + " : ");
			txtFields[i] = new JTextField(5);
			pnFields.add(lblFields[i]);
			pnFields.add(txtFields[i]);
		}
		txtFields[0].setText("Title0,Title1,Title2,Title3,Title4,Title5,Title6,Title7,Title8,Title9");
		txtFields[0].setColumns(40);
		txtFields[1].setText("15");
		txtFields[4].setText("0");
		controls.add(pnFields, BorderLayout.PAGE_START);

		pnButtons = new JPanel(new FlowLayout());
		String[] btnArray = {"Select File", "Set Columns", "Read Headers", "Title to Column", "Create Table", "Export Data", "Import Data", "Auto Import"};
		button = new JButton[btnArray.length];
		for(int i = 0; i < btnArray.length; i++) {
			button[i] = new JButton(btnArray[i]);
			button[i].addActionListener(this);
			pnButtons.add(button[i]);
		}
		txtTableName = new JTextField(10);
		txtTableName.setText("imp_data");
		pnButtons.add(txtTableName);
		controls.add(pnButtons, BorderLayout.PAGE_END);
		
		bodyPanel = new JPanel(new BorderLayout());
		
		myData = new Vector<Vector<String>>();
		myTitles = new Vector<String>();
		vectorTableMode = new BVectorTableModel(myData, myTitles);
		table = new JTable(vectorTableMode);
		
		area1 = new JTextArea();
		area2 = new JTextArea();
				
		JScrollPane scrollPane1 = new JScrollPane(table);
		JScrollPane scrollPane2 = new JScrollPane(area1);
		JScrollPane scrollPane3 = new JScrollPane(area2);
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Data", scrollPane1);
		tabPane.addTab("Query", scrollPane2);
		tabPane.addTab("Data Export", scrollPane3);
		
		bodyPanel.add(tabPane, BorderLayout.CENTER);
		super.add(bodyPanel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent ev) {
		String aKey = ev.getActionCommand();
		
		if(aKey.equals("Select File")) {
			autoImport(0);
		} else if(aKey.equals("Set Columns")) {
			setColumns();
		} else if(aKey.equals("Read Headers")) {
			readHeaders();
		} else if(aKey.equals("Export Data")) {
			exportData();
		} else if(aKey.equals("Title to Column")) {
			titleToColumn();
		} else if(aKey.equals("Create Table")) {
			createImportTable();
		} else if(aKey.equals("Import Data")) {
			importData();
		} else if(aKey.equals("Auto Import")) {
			autoImport(1);
		}
	}
	
	public int selectFile() {
		int nofSheets = 0;
		if(excelFile != null) {
			String myFieldList = txtFields[0].getText();
			String[] myFields = myFieldList.split(",");
			myTitles = new Vector<String>(Arrays.asList(myFields));
			nofSheets = readFile(excelFile, 0, myTitles.size());
	
			vectorTableMode = new BVectorTableModel(myData, myTitles);
			table.setModel(vectorTableMode);
			table.setFillsViewportHeight(true);
			table.setAutoCreateRowSorter(true);
			table.repaint();
		}
		
		return nofSheets;
	}
	
	public void setColumns() {
		Integer cols = Integer.valueOf(txtFields[1].getText());
		String sTitles = "Title0";
		for(int j = 1; j < cols; j++) sTitles += ",Title" + j;
		
		if(txtFields[2].getText().length() > 0) sTitles += "," + txtFields[2].getText();
		
		txtFields[0].setText(sTitles);
		
		selectFile();
	}
	
	public void readHeaders() {
		String tableName = txtTableName.getText();
		if(myData.size() > 0) {
			Vector<String> firstRow = myData.get(0);
			String sTitles = "";
			String impTable = "CREATE TABLE " + tableName + " (\n";
			impTable += "\t" + tableName + "_id\t\t\tserial primary key,\n";
			String impData = "INSERT INTO " + tableName + " (";
			for(int j = 0; j < firstRow.size(); j++) {
				String fieldName = formatFieldName(firstRow.get(j));
				
				if(!"".equals(fieldName)) {
					if(j != 0) {
						sTitles += ",";
						impTable += ",\n";
						impData += ", ";
					}
					sTitles += fieldName;
					impTable += "\t" + fieldName + "\t\t\tvarchar(250)";
					impData += fieldName;

					System.out.println(fieldName);
				}
			}
			impTable += "\n);\n\n";
			impData += ") VALUES\n";
					
			area1.setText(impTable);
			area2.append(impData);
			
			txtFields[0].setText(sTitles);
			selectFile();
		}
	}
	
	public void titleToColumn() {
		Integer cols = Integer.valueOf(txtFields[1].getText());
		if(txtFields[2].getText().length() > 0) {
			String[] strSubTitles = txtFields[2].getText().toUpperCase().split(",");
			String[] strSubValues = new String[strSubTitles.length];
			for(int j = 0; j < strSubTitles.length; j++) strSubValues[j] = "";
			
			for(int i = 0; i < myData.size(); i++) {
				Vector<String> myRow = myData.get(i);
				
				for(int j = 0; j < strSubTitles.length; j++) {
					if(myRow.get(0).toUpperCase().trim().startsWith(strSubTitles[j].trim())) {
						String strSubValue = myRow.get(0).toUpperCase().replace(strSubTitles[j].trim(), "").trim();
						if(strSubValue.length() > 0) strSubValues[j] = strSubValue;
					}
					myData.get(i).set(cols + j, strSubValues[j]);
				}
			}
			
			vectorTableMode.refresh();
		}
	}
	
	public void exportData() {
		int splitCol = -1;
		if(txtFields[3].getText().length() > 0) splitCol = Integer.valueOf(txtFields[3].getText());
		for(int i = 0; i < myData.size(); i++) {
			Vector<String> myRow = myData.get(i);
			
			String rowData = "(";
			for(int j = 0; j < myRow.size(); j++) {
				rowData += "'" + myRow.get(j) + "'"; 
				if(j < myRow.size() - 1) rowData += ", ";
			}
			
			if(splitCol > 0) {
				String fullName = myRow.get(splitCol).replaceAll("  ", " ").trim();
				String sNames[] = fullName.split(" ");
				rowData += ", ";
				if(sNames.length == 0) rowData += "'', '', ''";
				else if(sNames.length == 1) rowData += "'" + sNames[0] + "', '', ''";
				else if(sNames.length == 2) rowData += "'" + sNames[0] + "', '', '" + sNames[1] + "'";
				else if(sNames.length == 3) rowData += "'" + sNames[0] + "', '" + sNames[1] + "', '" + sNames[2] + "'";
				else if(sNames.length == 4) rowData += "'" + sNames[0] + "', '" + sNames[1] + " " + sNames[2] + "', '" + sNames[3] + "'";
				else if(sNames.length == 5) rowData += "'" + sNames[0] + "', '" + sNames[1] + " " + sNames[2] + " " + sNames[3] + "', '" + sNames[4] + "'";
			}
			
			area2.append(rowData + ")");
			
			if(i == myData.size() - 1) area2.append(";\n");
			else area2.append(",\n");
		}
	}

	public int readFile(File file, int firstRow, int columnCount) {
		myData = new Vector<Vector<String>>();
		
		Workbook wb = null;
		try {
			FileInputStream excelFile = new FileInputStream(file);
			if(file.getName().indexOf(".xlsx")>1) wb = new XSSFWorkbook(excelFile);
		    else if(file.getName().indexOf(".xls")>1) wb = new HSSFWorkbook(excelFile);
		} catch (IOException ex) {
			System.out.println("an I/O error occurred, or the InputStream did not provide a compatible POIFS data structure : " + ex);
		}
		int nofSheets = wb.getNumberOfSheets();
		Sheet sheet = wb.getSheetAt(workSheet);

		String wsName = wb.getSheetName(workSheet);
		System.out.println(wsName);
		txtTableName.setText("imp_" + wsName);
		int noOfColumns = sheet.getRow(0).getPhysicalNumberOfCells();
		txtFields[1].setText(Integer.toString(noOfColumns));
		
		Row row = null;
		int i = 0;
		if(firstRow < sheet.getFirstRowNum()) firstRow = sheet.getFirstRowNum();
		String myline = "";
		for(i = firstRow; i <= sheet.getLastRowNum(); i++) {
			Vector<String> myvec = new Vector<String>();
			row = sheet.getRow(i);
			if(row != null)  {
				myline = getCellValue(row, 0);

				//System.out.println(myline);
				for (int j=0; j<columnCount; j++)
					myvec.add(getCellValue(row, j));
					
				if(!myline.equals("")) myData.add(myvec);
			} else myline = "";
		}
		
		//area1.selectAll();
		//area1.replaceSelection("");
		
		return nofSheets;
	}
	
	public String getCellValue(Row row, int column) {
		String mystr = "";

		Cell cell = row.getCell(column);
		if (cell == null) cell = row.createCell(column);
		if (cell.getCellType() == CellType.STRING) {
			if(cell.getStringCellValue()!=null)
				mystr += cell.getStringCellValue().trim();
		} else if (cell.getCellType() == CellType.NUMERIC) {
			mystr += numberFormat(cell.getNumericCellValue());
		} else if (cell.getCellType() == CellType.FORMULA) {
			if(cell.getCachedFormulaResultType() == CellType.NUMERIC) {
				mystr += numberFormat(cell.getNumericCellValue());
			} else if(cell.getCachedFormulaResultType() == CellType.STRING) {
				mystr += cell.getRichStringCellValue();
			}
		}

		return mystr;
	}
	
	public String numberFormat(double cellVal) {
		DecimalFormat formatter = new DecimalFormat("############.###");
		return formatter.format(cellVal);
	}
	
	public void createImportTable() {
		db.executeQuery(area1.getText());
	}
	
	public void importData() {
		String tableName = txtTableName.getText();
		if(myData.size() > 0) {
			Vector<String> firstRow = myData.get(0);
			String impData = "INSERT INTO " + tableName + " (";
			String impValues = ") VALUES (";
			for(int j = 0; j < firstRow.size(); j++) {
				if(j != 0) {impData += ","; impValues += ","; }

				String fieldName = formatFieldName(firstRow.get(j));

				impData += fieldName;
				impValues += "?";
			}
			String inSql = impData + impValues + ")";
			System.out.println(inSql);
			
			for(int i = 1; i < myData.size(); i++) {
				Vector<String> myRow = myData.get(i);
				Map<String, String> mData = new LinkedHashMap<String, String>();
				for(int j = 0; j < myRow.size(); j++) {
					String fieldName = formatFieldName(firstRow.get(j));

					mData.put(fieldName, myRow.get(j));
				}
				String keyFieldId = db.saveRec(inSql, mData);
			}
		}
	}
	
	public void autoImport(int importType) {
		JFileChooser fc = new JFileChooser(projectDir);
		fc.setDialogTitle("Open Resource File");
		int returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			excelFile = fc.getSelectedFile();
			workSheet = Integer.valueOf(txtFields[4].getText());
			int nofSheets = selectFile();
			System.out.println("Number of sheets : " + nofSheets);
			
			if(importType == 1) {
				for(workSheet = 0; workSheet < nofSheets; workSheet++) {
					selectFile();
					setColumns();
					readHeaders();
					titleToColumn();
					createImportTable();
					importData();
				}
			}
		}
	}

	public String formatFieldName(String fieldName) {
		if(fieldName == null) return "";
		if(fieldName.indexOf("{") > 0) fieldName = fieldName.substring(0, fieldName.indexOf("{")).trim();
		fieldName = fieldName.replace(" ", "_").trim().toLowerCase();
		fieldName = fieldName.replace(".", "").trim();
		fieldName = fieldName.replace("'", "").trim();
		fieldName = fieldName.replace("\"", "_").trim();
		fieldName = fieldName.replace("/", "_").trim();
		fieldName = fieldName.replace("-", "_").trim();
		return fieldName;
	}

}
	

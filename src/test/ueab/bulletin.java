
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.text.DecimalFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

public class bulletin {

	Logger log = Logger.getLogger(bulletin.class.getName());
	BDB db;
	String projectDir;
	File excelFile = null;
	
	Workbook wb = null;

	public static void main(String args[]) {
		System.out.println("Starting importing reference data");
		
		bulletin b = new bulletin();
		if(args.length > 0) b.readFile(args[0]);
		
		System.out.println("Importing reference data completed");
	}
	
	
	public int readFile(String fileName) {
		db = new BDB("org.postgresql.Driver", "jdbc:postgresql://localhost/ueab", "root", "baraza");
		
		try {
			FileInputStream excelFile = new FileInputStream(fileName);
			if(fileName.indexOf(".xlsx")>1) wb = new XSSFWorkbook(excelFile);
		    else if(fileName.indexOf(".xls")>1) wb = new HSSFWorkbook(excelFile);
		} catch (IOException ex) {
			System.out.println("an I/O error occurred, or the InputStream did not provide a compatible POIFS data structure : " + ex);
		}
		int nofSheets = wb.getNumberOfSheets();
		
		String insertQuery = "INSERT INTO import_bulleting(majorid, courseid, contenttypeid) "
			+ "VALUES (?, ?, ?)";

		for(int ws = 0; ws < nofSheets; ws++) {
			String wsName = wb.getSheetName(ws);
			System.out.println(wsName);
			
			Map<String, String> courseData = readSheet(ws, 1, 7);
	
			for(String courseCode : courseData.keySet()) {
				Map<String, String> sData = new LinkedHashMap<String, String>();
				sData.put("majorid", wsName);
				sData.put("courseid", courseCode);
				sData.put("contenttypeid", "1");
				String recId = db.saveRec(insertQuery, sData);
			}
		}
		
		db.close();
		
		return nofSheets;
	}
	
	public Map<String, String> readSheet(int workSheet, int firstRow, int columnCount) {
		Sheet sheet = wb.getSheetAt(workSheet);
		
		Map<String, String> courseData = new HashMap<String, String>();

		int noOfColumns = sheet.getRow(0).getPhysicalNumberOfCells();
		
		Row row = null;
		int i = 0;
		if(firstRow < sheet.getFirstRowNum()) firstRow = sheet.getFirstRowNum();
		
		String courseCode = ""; String courseType = "";
		for(i = firstRow; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if(row != null)  {
				courseCode = getCellValue(row, 1);
				courseType = getCellValue(row, 6);
					
				if(!courseCode.equals("")) {
					courseCode = courseCode.replace(" ", "").trim();
					if(courseCode.length() > 6) {
						System.out.println(courseCode + " " + courseType);
						courseData.put(courseCode, courseType);
					}
				}
			}
		}
		
		return courseData;
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

}


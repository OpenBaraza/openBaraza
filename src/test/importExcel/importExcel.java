
import org.apache.poi.poifs.filesystem.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;


import java.util.*;

public class importExcel {

	public static void main(String[] args) {
		importExcel ie = new importExcel();
		importExcel ic = new importExcel();

		ie.getExcelData("/root/Downloads/Zetor_contribution.xls", 0);
		ic.getExcelData2("/root/Downloads/Zetort_loans (copy).xls", 0);
	}

	public void getExcelData(String fileName, int worksheet) {
		int colCount = 0;
		HSSFWorkbook wb = null;
		try {
			InputStream stream = new FileInputStream(fileName);
			POIFSFileSystem fs = new POIFSFileSystem(stream);
			DirectoryEntry rootdir = fs.getRoot();
			wb = new HSSFWorkbook(fs);
		} catch (IOException ex) {
			System.out.println("an I/O error occurred, or the InputStream did not provide a compatible POIFS data structure : " + ex);
		}

		HSSFSheet sheet = wb.getSheetAt(Integer.valueOf(worksheet));

		StringBuilder sb = new StringBuilder();
		for(int r = 1; r < 235; r++ ) {
			 HSSFRow row = sheet.getRow(r);
			 sb.append("(");
			for(int c = 0; c < 117; c++ ) {
				String data = getstrvalue(row, c);
				if(data == null) data = "0";

				if(c == 0) data = data.replace(".0", "");
				else if(c == 1) data = ",'" + data.trim() + "','{";
				else if(c > 2) data = "," + data;
				sb.append(data);
			}

			sb.append("}'),\n");
		}
		
		System.out.println(sb.toString());
	}


	
	public void getExcelData2(String fileName, int worksheet) {
		int colCount = 0;
		HSSFWorkbook wb = null;
		try {
			InputStream stream = new FileInputStream(fileName);
			POIFSFileSystem fs = new POIFSFileSystem(stream);
			DirectoryEntry rootdir = fs.getRoot();
			wb = new HSSFWorkbook(fs);
		} catch (IOException ex) {
			System.out.println("an I/O error occurred, or the InputStream did not provide a compatible POIFS data structure : " + ex);
		}

		HSSFSheet sheet = wb.getSheetAt(Integer.valueOf(worksheet));

		StringBuilder sb = new StringBuilder();
		for(int r = 1; r < 171; r++ ) {
			 HSSFRow row = sheet.getRow(r);
			 sb.append("(");
			for(int c = 0; c < 96; c++ ) {
				String data = getstrvalue(row, c);
				if(data == null) data = "0";

				if(c == 0) data = data.replace(".0", "");
				else if(c == 1) data = ",'" + data.trim().replace(".0", "") + "'";
				else if(c == 2) data = ",'" + data.trim() + "','{";
				else if(c > 2) data = "," + data;
				sb.append(data);
			}

			sb.append("}'),\n");
		}
		
		System.out.println(sb.toString());
	}



	public String getstrvalue(HSSFRow row, int column) {
		String mystr = null;

		HSSFCell cell = row.getCell(column);
		if (cell == null) cell = row.createCell(column);
		if (cell.getCellType()==cell.CELL_TYPE_STRING) {
			if(cell.getStringCellValue()!=null) mystr = cell.getStringCellValue().trim();
		} else if (cell.getCellType()==cell.CELL_TYPE_NUMERIC) {
			mystr = String.valueOf(cell.getNumericCellValue());
		}
		//System.out.println(mystr);
		return mystr;
	}

} 

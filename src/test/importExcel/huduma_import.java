
import org.apache.poi.poifs.filesystem.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.sql.*;

import java.util.*;

public class huduma_import {

	public static void main(String[] args) {
		huduma_import hi = new huduma_import();

		hi.getExcelData("/home/reagan/Downloads/huduma_import.xls", 0);
	}

	public void getExcelData(String fileName, int worksheet) {
		int colCount = 0;
		HSSFWorkbook wb = null;
		Connection conn = null;
		
		try {
		
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/db_name", "postgres", "xxxxx");
            
			InputStream stream = new FileInputStream(fileName);
			POIFSFileSystem fs = new POIFSFileSystem(stream);
			DirectoryEntry rootdir = fs.getRoot();
			wb = new HSSFWorkbook(fs);
		} catch (IOException ex) {
			System.out.println("an I/O error occurred, or the InputStream did not provide a compatible POIFS data structure : " + ex);
		}
		catch(SQLException ex) {
            System.out.println("SQL Connection Error! "+ex);
		}

		HSSFSheet sheet = wb.getSheetAt(Integer.valueOf(worksheet));
		
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO huduma_data (loan_id, s_no, member_name, member_number, loan_amount,loan_period_months, disbursement_date, repayment_duration, monthly_repayment, loan_interest, months_paid_amounts, months_paid_interests) VALUES\n ");
		
		int code = 0;
		for(int r = 1; r < 63; r++ ) {
			 HSSFRow row = sheet.getRow(r);
			 sb.append("(");
			for(int c = 0; c < 38; c++ ) {
				String data = getstrvalue(row, c);
				if(data == null) data = "0";

				if(c == 0) data = "'" + data + "'";
				else if(c == 1) data = ",'" + data + "'";
				else if(c == 2) data = ",'" + data.replace("'", "") + "'";
				else if(c == 3) data = ",'" + data + "'";
				else if(c == 4) data = ",'" + data + "'";
				else if(c == 5) data = ",'" + data + "'";
				else if(c == 6) data = ",'" + data + "'";
				else if(c == 7) data = ",'" + data + "'";
				else if(c == 8) data = ",'" + data + "'";
				else if(c == 9) data = ",'" + data + "','{";
				else if(c > 9 && c < 23)  data = data + ",";
				else if(c == 23)data = data + "}','{";
				else if(c > 23 && c < 37) data = data +",";
				sb.append(data);
			}
            
            if(r == 62) sb.append("}')"); 
			else sb.append("}'),\n");
		}
		
		System.out.println(sb.toString());
		
		try {
			Statement st = conn.createStatement();
			st.execute(sb.toString());  
			st.close();  
			
		} catch(SQLException ex) {
			System.out.println("Database error : " + ex);
		}
		
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

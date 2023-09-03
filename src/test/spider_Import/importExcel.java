
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.sql.*;

import java.util.*;

public class importExcel {

	public static void main(String[] args) {
		importExcel ie = new importExcel();
		importExcel ic = new importExcel();

		//ie.getExcelData("mashujaa.xlsx", 0);
		//ie.getExcelData("latest_loan_data.xlsx", 0);
		ie.getExcelData("three_months_update.xlsx", 0);
	}

	public void getExcelData(String fileName, int worksheet) {
		int colCount = 0;
		XSSFWorkbook wb = null;
		Connection conn = null;
		
		try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/banking", "postgres", "invent");

			File myFile = new File(fileName);
            FileInputStream fis = new FileInputStream(myFile);
			wb = new XSSFWorkbook(fis);
		} catch (IOException ex) {
			System.out.println("An I/O error occurred, or the InputStream did not provide a compatible POIFS data structure : " + ex);
		} catch(SQLException ex) {
            System.out.println("SQL Connection Error! "+ex);
		}

		XSSFSheet sheet = wb.getSheetAt(Integer.valueOf(worksheet));

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO imp_add_loans (phone_number,Name,disbursal_reference,date_granted,due_date,amount_granted,total_granted,repayment_reference,date_of_payment,principle_payment,running_balance,total_principle,interest_payments,running_interest,total_interests,total_due_loan_interest,overdue_period,days_0_30,days_31_60,days_61_90,days_above_90,loan_code) VALUES\n ");
		int code = 0;
		for(int r = 7; r < 440; r++ ) {         
            XSSFRow row = sheet.getRow(r);
            sb.append("(");
			for(int c = 0; c < 22; c++ ) {
				String data = getstrvalue(row, c);
								
				if(data == null) data = "0";

				if(c == 0)data = "'" + data.replace(".0", "") + "'";
				else if(c == 1) data = ",'" + data.replaceAll("'","") + "'";
				else if(c == 2) data = ",'" + data.trim() + "'";
				else if(c == 3) data = ",'" + data.trim() + "'";
				else if(c == 4) data = ",'" + data.trim() + "'";
				else if(c == 5) {
                    if(Double.parseDouble(data) > 0) code++;
                    data = ",'" + data.trim() + "'";
				}
				else if(c == 6) data = ",'" + data.trim() + "'";
				else if(c == 7) data = ",'" + data.trim() + "'";
				else if(c == 8) data = ",'" + data.trim() + "'";
				else if(c == 9) data = ",'" + data.trim() + "'";
				else if(c == 10) data = ",'" + data.trim() + "'";
				else if(c == 11) data = ",'" + data.trim() + "'";
				else if(c == 12) data = ",'" + data.trim() + "'";
				else if(c == 13) data = ",'" + data.trim() + "'";
				else if(c == 14) data = ",'" + data.trim() + "'";
				else if(c == 15) data = ",'" + data.trim() + "'";
				else if(c == 16) data = ",'" + data.trim() + "'";
				else if(c == 17) data = ",'" + data.trim() + "'";
				else if(c == 18) data = ",'" + data.trim() + "'";
				else if(c == 19) data = ",'" + data.trim() + "'";
				else if(c == 20) data = ",'" + data.trim() + "'";
				else if(c == 21) data = ",'" + code + "'";
				else if(c > 21) data = "," + data;
				sb.append(data);
				
			}

			/** If this is the last row, don't append , to avoid insertion error **/
			if(r == 439) sb.append(")");       //number for test
			else sb.append("),\n");
                
        }
		
        /** Insert this data into the database **/
	//System.out.println(sb.toString());
		
		try {
			Statement st = conn.createStatement();
			st.execute(sb.toString());  
			st.close();  
			
		} catch(SQLException ex) {
			System.out.println("Database error : " + ex);
		}  
		
	}


	public String getstrvalue(XSSFRow row, int column) {
		String mystr = null;

		XSSFCell cell = row.getCell(column);
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


import java.sql.*;

import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.util.logging.Logger;

public class tableCounter {
	
    static Logger log = Logger.getLogger(tableCounter.class.getName());
   
    public static void main(String[] args) {
    	 
    	JFrame frame = new JFrame("Database Metadata");
    	Vector<Vector<String>> data =  new Vector<Vector<String>>();
    	
		try{
			//Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/frontaccounting", "root", "");
			Connection con = DriverManager.getConnection("jdbc:postgresql://localhost/upgr", "root", "invent2k");
			
			DatabaseMetaData dbmd = con.getMetaData();
			String[] types = {"TABLE"};
			ResultSet rs = dbmd.getTables(null, null,"%",types);
			
			while(rs.next()) {
				String table_schema = rs.getString("TABLE_SCHEM");
				String table_name = rs.getString("TABLE_NAME");
				if(table_schema == null) table_schema = "";
				else table_schema += ".";
				
				//  erad the data
				PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM " + table_schema + table_name + "");
				ResultSet rst = ps.executeQuery();
				rst.next();
				Integer rowCount = rst.getInt(1);			
				
				if(rowCount > 0) {
					System.out.println(table_name);
					
					Vector<String> item = new Vector<String>();
					item.add(table_name);
					item.add(rowCount.toString());
					data.add(item);
				}
				
				rst.close();
			}
			rs.close();

			Vector<String> columns = new Vector<String>();
			columns.add("Table Name");
			columns.add("Number of Rows");
			
			JTable table = new JTable(data, columns);
			table.setAutoCreateRowSorter(true);
			
			JScrollPane scrollPane = new JScrollPane(table);
			table.setFillsViewportHeight(true);
		
			JLabel lblHeading = new JLabel("Relations and their row count");
			lblHeading.setFont(new Font("Arial",Font.TRUETYPE_FONT,24));
		
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(lblHeading,BorderLayout.PAGE_START);
			frame.getContentPane().add(scrollPane,BorderLayout.CENTER);
		
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(550, 200);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
    	} catch (SQLException e) {
			log.severe("Error in Query : " + e.toString());
		}
	}
} 



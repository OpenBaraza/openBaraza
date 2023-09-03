
import java.util.Vector;
import java.util.Arrays;
import java.text.DecimalFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class importModel {

	JTextArea area;
	JTable table;
	JTextField txtDBPath, txtDBCPath, txtUsername, txtFields;
	JPasswordField txtPassword;
	
	Vector<Vector<String>> myData;
	Vector<String> myTitles;

	public importModel() {
		JFrame frame = new JFrame("Excel Import processing");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel topPanel = new JPanel(new GridLayout(1, 2));
		topPanel.add(addTextBox());
		topPanel.add(addButtons());

		frame.getContentPane().add(topPanel, BorderLayout.PAGE_START);
		frame.getContentPane().add(addBody(), BorderLayout.CENTER);

		frame.setSize(900, 700);

		frame.setVisible(true);
    }
    
   public JPanel addTextBox() {
		JPanel hbox = new JPanel(new GridLayout(5, 2));
		
		JLabel lblDBPath = new JLabel("Main Database : ");
		txtDBPath = new JTextField(50);
		txtDBPath.setText("jdbc:postgresql://localhost/hr");
		hbox.add(lblDBPath);
		hbox.add(txtDBPath);
		
		JLabel lblDBCPath = new JLabel("Checking  : ");
		txtDBCPath = new JTextField(50);
		txtDBCPath.setText("jdbc:postgresql://localhost/hcmke");
		hbox.add(lblDBCPath);
		hbox.add(txtDBCPath);

		JLabel lblUsername = new JLabel("Username : ");
		txtUsername = new JTextField(50);
		txtUsername.setText("postgres");
		hbox.add(lblUsername);
		hbox.add(txtUsername);

		JLabel lblPassword = new JLabel("Password : ");
		txtPassword = new JPasswordField(50);
		hbox.add(lblPassword);
		hbox.add(txtPassword);

		JLabel lblFields = new JLabel("Fields : ");
		txtFields = new JTextField(50);
		txtFields.setText("Title1,Title2,Title3,Title4");
		hbox.add(lblFields);
		hbox.add(txtFields);

		return hbox;
	}
    
    public JPanel addButtons() {
		JPanel hbox = new JPanel(new GridLayout(5, 1));
		
		JButton btnOpenFile = new JButton("Select File");
		hbox.add(btnOpenFile);
		
		JButton btnDBConnect = new JButton("Connect Database");
		hbox.add(btnDBConnect);

		JButton btnMissingTables = new JButton("Missing Tables");
		hbox.add(btnMissingTables);

		return hbox;
	}
	
	 public JPanel addBody() {
		JPanel hbox = new JPanel(new BorderLayout());
		
		// Create a table
		myData = new Vector<Vector<String>>();
		myTitles = new Vector<String>();
		table = new JTable(myData, myTitles);
		
		//Creating a pagination
		area = new JTextArea();
				
		JScrollPane scrollpane1 = new JScrollPane(table);
		JScrollPane scrollpane2 = new JScrollPane(area);
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Data", scrollpane1);
		tabPane.addTab("Query", scrollpane2);
		
		hbox.add(tabPane, BorderLayout.CENTER);
		
		return hbox;
	}
	
	public void openFile() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open Resource File");
		/*File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			String myFieldList = txtFields.getText();
			String[] myFields = myFieldList.split(",");
			myTitles = new Vector<String>(Arrays.asList(myFields));
						
			myData = readFile(file, 0, 0, myTitles.size());			
		}*/
	}
	
	public Vector<Vector<String>> readFile(File file, int worksheet, int firstRow, int columnCount) {
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		
		Workbook wb = null;
		try {
			FileInputStream excelFile = new FileInputStream(file);
			if(file.getName().indexOf(".xlsx")>1) wb = new XSSFWorkbook(excelFile);
		    else if(file.getName().indexOf(".xls")>1) wb = new HSSFWorkbook(excelFile);
		} catch (IOException ex) {
			System.out.println("an I/O error occurred, or the InputStream did not provide a compatible POIFS data structure : " + ex);
		}

		Sheet sheet = wb.getSheetAt(worksheet);
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
				if(!myline.equals(""))
					rows.add(myvec);
			} else myline = "";
		}
		
		return rows;
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
	
	public void connectDB() {
	}
	
	public void getMissingTables() { 
		System.out.println("Check missing tables");
	}
	
	public static void main(String[] args) {
		importModel im = new importModel();
	}

}

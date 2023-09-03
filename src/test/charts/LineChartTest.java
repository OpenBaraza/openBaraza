
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.*;
import org.jfree.data.jdbc.JDBCCategoryDataset;

public class LineChartTest {
   public static void main( String[] args )throws Exception {
// Create a connection to the PostgreSQL database
 Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/umis", "postgres", "Baraza2011");
// Create a statement object to execute the query
 Statement stmt = conn.createStatement();
// Execute the query and store the results in a ResultSet
 ResultSet rs = stmt.executeQuery("SELECT charge_id, unit_charge, general_fees, lab_charges, exam_fees FROM charges");
// Create an XYSeries object to hold the data for the first Y-axis
XYSeries series1 = new XYSeries("Unit charges");
XYSeries series2 = new XYSeries("General fees");
XYSeries series3 = new XYSeries("Lab Charges");
XYSeries series4 = new XYSeries("Exam fees");

// Loop through the ResultSet object, adding each row to the appropriate series
while (rs.next()) {
  series1.add(rs.getDouble("charge_id"), rs.getDouble("unit_charge"));
  series2.add(rs.getDouble("charge_id"), rs.getDouble("general_fees"));
  series3.add(rs.getDouble("charge_id"), rs.getDouble("lab_charges"));
  series4.add(rs.getDouble("charge_id"), rs.getDouble("exam_fees"));
}

// Create an XYSeriesCollection object to hold both series
XYSeriesCollection dataset = new XYSeriesCollection();


// Add the data series to the dataset
dataset.addSeries(series1);
dataset.addSeries(series2);
dataset.addSeries(series3);
dataset.addSeries(series4);

// Create a new line chart with the data
JFreeChart chart = ChartFactory.createXYLineChart("Unit Charges Graph", "Charge ID", "Unit Charge", dataset, PlotOrientation.VERTICAL, true, true, false);

// panel.add(new ChartPanel(chart));
ApplicationFrame f = new ApplicationFrame("Chart");
		f.add(new ChartPanel(chart));
		f.pack();
		f.setVisible(true);

   }
}

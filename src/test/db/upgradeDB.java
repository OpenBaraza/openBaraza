
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
		
import java.util.List;
import java.util.List; 
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class upgradeDB extends Application {

	compareDB cDB = null;
	TextArea area;
	
	TextField txtDBPath, txtDBCPath, txtUsername;
	PasswordField txtPassword;

	@Override
	public void start(Stage primaryStage) {
		System.out.println(" Start DB compare");
				
		primaryStage.setTitle("DB Migration");
		
		BorderPane topPane = new BorderPane();
		topPane.setCenter(addTextBox());
		topPane.setBottom(addButtons());
		
		BorderPane mainPane = new BorderPane();
		mainPane.setTop(topPane);
		mainPane.setCenter(addBody());

		Scene scene = new Scene(mainPane, 1100, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
    }
    
   public GridPane addTextBox() {
		GridPane hbox = new GridPane();
		hbox.setPadding(new Insets(10, 10, 10, 10));
		hbox.setVgap(5);
		hbox.setHgap(5);
		hbox.setStyle("-fx-background-color: #336699;");
		
		Label lblDBPath = new Label("Main Database : ");
		txtDBPath = new TextField();
		txtDBPath.setText("jdbc:postgresql://localhost/hr");
		txtDBPath.setPrefWidth(400);
		
		Label lblDBCPath = new Label("Checking  : ");
		txtDBCPath = new TextField();
		txtDBCPath.setText("jdbc:postgresql://localhost/hcmke");
		txtDBCPath.setPrefWidth(400);

		Label lblUsername = new Label("Username : ");
		txtUsername = new TextField();
		txtUsername.setText("postgres");

		Label lblPassword = new Label("Password : ");
		txtPassword = new PasswordField();
		
		hbox.getChildren().addAll(lblDBPath, txtDBPath, lblDBCPath, txtDBCPath, lblUsername, txtUsername, lblPassword, txtPassword);
		GridPane.setConstraints(lblDBPath, 0, 0);
		GridPane.setConstraints(txtDBPath, 1, 0);
		GridPane.setConstraints(lblDBCPath, 0, 1);
		GridPane.setConstraints(txtDBCPath, 1, 1);
		GridPane.setConstraints(lblUsername, 0, 2);
		GridPane.setConstraints(txtUsername, 1, 2);
		GridPane.setConstraints(lblPassword, 0, 3);
		GridPane.setConstraints(txtPassword, 1, 3);

		return hbox;
	}
    
    public HBox addButtons() {
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #336699;");
		
		Button btnDBConnect = new Button("Connect Database");
		btnDBConnect.setPrefSize(200, 20);
		btnDBConnect.setOnAction(event -> connectDB());

		Button btnMissingTables = new Button("Missing Tables");
		btnMissingTables.setPrefSize(200, 20);
		btnMissingTables.setOnAction(event -> getMissingTables());

		Button btnMissingFields = new Button("Missing Fields");
		btnMissingFields.setPrefSize(200, 20);
		btnMissingFields.setOnAction(event -> getMissingFields());
		
		Button btnMissingViewFields = new Button("Missing View Fields");
		btnMissingViewFields.setPrefSize(200, 20);
		btnMissingViewFields.setOnAction(event -> getMissingViewFields());
		
		Button btnMissingFunctions = new Button("Missing Functions");
		btnMissingFunctions.setPrefSize(200, 20);
		btnMissingFunctions.setOnAction(event -> getMissingFunctions());
		
		Button btnMissingTriggers = new Button("Missing Triggers");
		btnMissingTriggers.setPrefSize(200, 20);
		btnMissingTriggers.setOnAction(event -> getMissingTriggers());
		
		Button btnDropViews = new Button("Drop Views");
		btnDropViews.setPrefSize(200, 20);
		btnDropViews.setOnAction(event -> getDropViews());
		
		Button btnGetTables = new Button("Get Tables");
		btnGetTables.setPrefSize(200, 20);
		btnGetTables.setOnAction(event -> getTables());
		
		hbox.getChildren().addAll(btnDBConnect, btnMissingTables, btnMissingFields, btnMissingViewFields, btnMissingFunctions, btnMissingTriggers, btnDropViews, btnGetTables);

		return hbox;
	}
	
	 public HBox addBody() {
		Label label = new Label("Updates");
		Font font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
		label.setFont(font);
		
		//Creating a pagination
		area = new TextArea();
		area.setPrefColumnCount(20);
		area.setPrefHeight(400);
		area.setPrefWidth(700);
		
		//Creating a hbox to hold the pagination
		HBox hbox = new HBox();
		hbox.setSpacing(20);
		hbox.setPadding(new Insets(20, 50, 50, 50));
		hbox.getChildren().addAll(label, area);
		
		return hbox;
	}
	
	public void connectDB() {
		cDB = new compareDB(txtDBPath.getText(), txtDBCPath.getText(), txtUsername.getText(), txtPassword.getText());
	}
	
	public void getMissingTables() { 
		System.out.println("Check missing tables");
		
		if(cDB != null) {
			List<String> mTables = cDB.getTableNames(0);
			for(String mTable : mTables) area.appendText(mTable + "\n");
			if(mTables.size() == 0) area.appendText("No missing tables\n");
		}
	}
	
	public void getMissingFields() { 
		System.out.println("Get missing fields");
		
		if(cDB != null) {
			List<String> mFields = cDB.getFieldNames(0);
			for(String mField : mFields) area.appendText(mField + "\n");
			if(mFields.size() == 0) area.appendText("No missing table fields\n");
		}
	}
	
	public void getMissingViewFields() { 
		System.out.println("Get missing fields");
		
		if(cDB != null) {
			List<String> mFields = cDB.getFieldNames(1);
			for(String mField : mFields) area.appendText(mField + "\n");
			if(mFields.size() == 0) area.appendText("No missing view fields\n");
		}
	}
	
	public void getMissingFunctions() { 
		System.out.println("Check missing Functions");
		
		if(cDB != null) {
			List<String> mTables = cDB.getFunctionNames();
			for(String mTable : mTables) area.appendText(mTable + "\n");
			if(mTables.size() == 0) area.appendText("No missing Functions\n");
		}
	}
	
	public void getMissingTriggers() { 
		System.out.println("Check missing Triggers");
		
		if(cDB != null) {
			List<String> mTables = cDB.getTriggerNames();
			for(String mTable : mTables) area.appendText(mTable + "\n");
			if(mTables.size() == 0) area.appendText("No missing Triggers\n");
		}
	}
	
	public void getDropViews() { 
		System.out.println("Get drop views");
		
		String viewSql = area.getText();
		
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
		area.setText(strDrop);

	}
	
	public void getTables() { 
		System.out.println("Get Tables");
		
		String viewSql = area.getText();
		
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
		area.setText(strDrop);

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

	
	public static void main(String[] args) {
		launch(args);
	}

}

package userinterface;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Controller implements Initializable {
	
	@FXML
    private TextField tbxOutpath;

    @FXML
    private CheckBox chkLinks;

    @FXML
    private CheckBox chkTables;

    @FXML
    private Button btnBrowse;

    @FXML
    private TextField tbxUrl;

    @FXML
    private CheckBox chkPictures;

    @FXML
    private Button btnChoose;

    @FXML
    private CheckBox chkVideos;

    @FXML
    private CheckBox chkText;

    @FXML
    private Button btnRun;
    

    @FXML
    void handleButtonAction(ActionEvent event) {
    	//Input handling
    	if (tbxUrl.getText().equals("")) {
    		new Alert(Alert.AlertType.WARNING, "URL darf nicht leer sein!").showAndWait();
    	} else if (!(chkLinks.isSelected() || chkVideos.isSelected() || chkText.isSelected()
    				|| chkTables.isSelected() || chkPictures.isSelected())) {
    		new Alert(Alert.AlertType.WARNING, "Bitte mindestens ein Element auswählen!").showAndWait();
    	}
    	
    	File f = new File(System.getProperty("user.home") + "\\Documents\\test.txt");
    	try {
    		f.createNewFile();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    
    	//TODO: IMPLEMENT ME
    }
    
    @FXML
    void setDirectory(ActionEvent event) {
    	DirectoryChooser dc = new DirectoryChooser();
    	dc.setTitle("Choose a directory");
    	File selectedDirectory = dc.showDialog(new Stage());
    	if (selectedDirectory != null) {
    	tbxOutpath.setText(selectedDirectory.getPath());
    	}
    }
    
    @FXML
    void setFilePath(ActionEvent event) {
    	FileChooser fc = new FileChooser();
    	fc.setTitle("Choose txt file");
    	fc.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
    	File selectedFile = fc.showOpenDialog(new Stage());
    	if (selectedFile != null) {
    		tbxUrl.setText(selectedFile.getAbsolutePath());
    	}
    }
	
	private static void Main(String[] args) {
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tbxOutpath.setText(System.getProperty("user.home") + "\\Documents");
		tbxUrl.setPromptText("Copy URL here or browse browse Textfile with links...");
	}
	
	
}

package lk.ijse.dep11.Controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;

public class MainController {

    @FXML
    private Button btnCopy;

    @FXML
    private Button btnSource;

    @FXML
    private Button btnTarget;

    @FXML
    private Label lblProgress;

    @FXML
    private Label lblStatus;

    @FXML
    private ProgressBar pbBar;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField txtSourceFile;

    @FXML
    private TextField txtTargetFile;

    public void initialize() {
        btnCopy.setDisable(true);
        txtSourceFile.requestFocus();

    }


    @FXML
    void btnCopyOnAction(ActionEvent event) {
        btnCopy.setDisable(true);
       Task<Void> copyTask=new Task<Void>(){
           @Override
           protected Void call() throws Exception {
               copy();
               Platform.runLater(()->btnCopy.setDisable(false));
               return null;
           }
       };

       new Thread(copyTask).start();


    }

    @FXML
    void btnSourceOnAction(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the Source File");
        File sourceFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (sourceFile != null) {
            txtSourceFile.setText(sourceFile.getAbsolutePath());
        } else {
            txtSourceFile.clear();
        }
        btnCopy.setDisable(txtSourceFile.getText().isBlank() || txtTargetFile.getText().isBlank());

    }

    @FXML
    void btnTargetOnAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select the Target Folder");
        File targetDirectory = directoryChooser.showDialog(root.getScene().getWindow());
        if (targetDirectory != null) {
            txtTargetFile.setText(targetDirectory.getAbsolutePath());
        } else {
            txtTargetFile.clear();
        }
        btnCopy.setDisable(txtSourceFile.getText().isBlank() || txtTargetFile.getText().isBlank());


    }

    void copy(){
        File sourceFile = new File(txtSourceFile.getText());
        File targetFile = new File(txtTargetFile.getText(), sourceFile.getName());
        System.out.println(sourceFile);

        try {
            targetFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Failed to Create File");
            e.printStackTrace();

        }

        try {
            FileInputStream fis = new FileInputStream(sourceFile);
            FileOutputStream fos = new FileOutputStream(targetFile);
            int read = 0;
            int copied=0;

            while ((read = fis.read()) != -1) {
                fos.write(read);
                copied++;
                updateProgress(copied,sourceFile.length());
            }
            fis.close();
            fos.close();
            Platform.runLater(()->{
                new Alert(Alert.AlertType.CONFIRMATION,"Copied Successfully").show();
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Platform.runLater(()->{
                new Alert(Alert.AlertType.ERROR,"File Not Copied").show();
            });
            e.printStackTrace();
        }

    }

    void updateProgress(long copied,long totalLength){
        Platform.runLater(()->{
            lblProgress.setText(String.format("%.2f",(copied*100)/totalLength*1.0).concat("%"));
            lblStatus.setText(String.format("Copied %d/%d Bytes",copied,totalLength));
            pbBar.setProgress(copied/(totalLength*1.0));
        });
    }

}

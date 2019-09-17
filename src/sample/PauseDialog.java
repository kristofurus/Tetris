package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PauseDialog {

    @FXML
    private Button exitButton;
    @FXML
    private Button returnButton;
    @FXML
    private Button helpButton;

    @FXML
    public void handleExitButton(ActionEvent event) throws Exception {

    }

    @FXML
    public void handleReturnButton(ActionEvent event) throws Exception {

    }

    @FXML
    public void handleHelpButton(ActionEvent event) throws Exception {
        /*TODO
         * make new help scene co user can go to help menu
         * and later return to pauseDialog */
        /*Parent helpParent = FXMLLoader.load(getClass().getResource("helpScene.fxml"));
        Scene helpScene = new Scene(helpParent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(helpScene);
        appStage.show();*/
    }
}

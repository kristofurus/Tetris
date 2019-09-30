package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    Button playButton;
    @FXML
    Button highScoreButton;
    @FXML
    Button settingButton;
    @FXML
    Button helpButton;
    @FXML
    Button exitButton;

    @FXML
    private void handlePlayButton(ActionEvent event) throws Exception {
        Parent playParent = FXMLLoader.load(getClass().getResource("playScene.fxml"));
        Scene playScene = new Scene(playParent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(playScene);
        appStage.show();
    }

    @FXML
    private void handleHighScoreButton(ActionEvent event) throws Exception {
        Parent highScoreParent = FXMLLoader.load(getClass().getResource("highScoreScene.fxml"));
        Scene highScoreScene = new Scene(highScoreParent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(highScoreScene);
        appStage.show();
    }

    @FXML
    private void handleSettingButton(ActionEvent event) throws Exception {
        Parent settingParent = FXMLLoader.load(getClass().getResource("settingScene.fxml"));
        Scene settingScene = new Scene(settingParent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(settingScene);
        appStage.show();
    }

    @FXML
    private void handleHelpButton(ActionEvent event) throws Exception {
        Parent helpParent = FXMLLoader.load(getClass().getResource("helpScene.fxml"));
        Scene helpScene = new Scene(helpParent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(helpScene);
        appStage.show();
    }

    @FXML
    private void handleExitButton() {
        Main.save();
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

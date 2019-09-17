package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.media.MediaPlayer.INDEFINITE;


public class SettingScene implements Initializable {

    @FXML
    CheckBox musicButton;
    @FXML
    CheckBox soundButton;
    @FXML
    Button returnButton;

    @FXML
    private void handleReturnButton(ActionEvent event) throws Exception {
        Parent mainMenuParent = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene mainMenuScene = new Scene(mainMenuParent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(mainMenuScene);
        appStage.show();
    }

    //TODO
    // error with stopping music when stopped it plays for few more seconds
    // when leaving settings music stops after about 5 sec
    private MediaPlayer mediaPlayer;

    @FXML
    private void handleMusicButton() {
        mediaPlayer = new MediaPlayer(new Media(getClass().getResource("../resources/Tetris.mp3").toString()));
        if (musicButton.isSelected()) {
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.setAutoPlay(true);
                mediaPlayer.setCycleCount(INDEFINITE);
            });
        } else {
            mediaPlayer.stop();
            mediaPlayer.setAutoPlay(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

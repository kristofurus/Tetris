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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class SettingScene implements Initializable {

    //FXML variables
    @FXML
    CheckBox musicButton;
    @FXML
    CheckBox soundButton;
    @FXML
    Button returnButton;

    /*TODO
     * download options from file
     * add possibility of changing block colour*/

    //sound and music variables
    static boolean IS_MUSIC = false;
    static boolean IS_SOUND = false;

    //tetromino colours variables
    static Color IBlockColour;
    static Color JBlockColour;
    static Color LBlockColour;
    static Color SBlockColour;
    static Color ZBlockColour;
    static Color OBlockColour;
    static Color TBlockColour;

    @FXML
    private void handleReturnButton(ActionEvent event) throws Exception {
        Parent mainMenuParent = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene mainMenuScene = new Scene(mainMenuParent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(mainMenuScene);
        appStage.show();
    }

    @FXML
    private void handleMusicButton() {
        IS_MUSIC = musicButton.isSelected();
    }

    @FXML
    private void handleSoundButton(){
        IS_SOUND = soundButton.isSelected();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

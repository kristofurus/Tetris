package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class SettingScene implements Initializable {

    //FXML variables
    @FXML
    ColorPicker colorPicker;
    @FXML
    CheckBox musicButton;
    @FXML
    CheckBox soundButton;
    @FXML
    Button returnButton;
    @FXML
    Button defaultButton;

    @FXML
    ToggleButton IBlock;
    @FXML
    ToggleButton JBlock;
    @FXML
    ToggleButton LBlock;
    @FXML
    ToggleButton SBlock;
    @FXML
    ToggleButton ZBlock;
    @FXML
    ToggleButton OBlock;
    @FXML
    ToggleButton TBlock;
    @FXML
    ToggleGroup blockGroup;

    /*TODO
     * download options from file
     * add possibility of changing block colour*/

    //sound and music variables
    static boolean IS_MUSIC = false;
    static boolean IS_SOUND = false;

    //tetromino colours variables
    static Color IBlockColour = Color.INDIGO;
    static Color JBlockColour = Color.VIOLET;
    static Color LBlockColour = Color.BLUE;
    static Color SBlockColour = Color.GREEN;
    static Color ZBlockColour = Color.RED;
    static Color OBlockColour = Color.YELLOW;
    static Color TBlockColour = Color.ORANGE;

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

    @FXML
    private void handleColorPicker(){
        if (IBlock.equals(blockGroup.getSelectedToggle())) {
            IBlockColour = colorPicker.getValue();
        } else if(LBlock.equals(blockGroup.getSelectedToggle())){
            LBlockColour = colorPicker.getValue();
        } else if(JBlock.equals(blockGroup.getSelectedToggle())){
            JBlockColour = colorPicker.getValue();
        } else if(ZBlock.equals(blockGroup.getSelectedToggle())){
            ZBlockColour = colorPicker.getValue();
        } else if(SBlock.equals(blockGroup.getSelectedToggle())){
            SBlockColour = colorPicker.getValue();
        } else if(TBlock.equals(blockGroup.getSelectedToggle())){
            TBlockColour = colorPicker.getValue();
        } else if(OBlock.equals(blockGroup.getSelectedToggle())){
            OBlockColour = colorPicker.getValue();
        }
    }

    @FXML
    private void handleDefaultButton(){
        IBlockColour = Color.INDIGO;
        JBlockColour = Color.VIOLET;
        LBlockColour = Color.BLUE;
        SBlockColour = Color.GREEN;
        ZBlockColour = Color.RED;
        OBlockColour = Color.YELLOW;
        TBlockColour = Color.ORANGE;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

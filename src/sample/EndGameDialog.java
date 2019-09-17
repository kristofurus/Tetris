package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EndGameDialog {

    @FXML
    private Button exitButton;
    @FXML
    private Button playAgainButton;
    @FXML
    private Label scoreLabel;
    //maybe change to just score label and make it look "score: score"
    @FXML
    private Label scoreTextLabel;
    //time: mm:ss
    @FXML
    private Label timeLabel;

    @FXML
    public void handleExitButton(){
        Platform.exit();
    }

    @FXML
    public void handlePlayAgainButton(){

    }

}

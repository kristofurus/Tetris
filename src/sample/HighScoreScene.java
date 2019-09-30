package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static sample.PlayScene.TEXT_LENGTH;
import static sample.PlayScene.highScores;

public class HighScoreScene implements Initializable {

    @FXML
    Button returnButton;
    @FXML
    FlowPane highScorePane;

    @FXML
    private void handleReturnButton(ActionEvent event) throws Exception {
        Parent mainMenuParent = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene mainMenuScene = new Scene(mainMenuParent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(mainMenuScene);
        appStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //sorting list by comparing values
        highScores.sort((c1, c2)-> c2.getValue().compareTo(c1.getValue()));

        //list of scores converted to text
        List<Text> scores = new ArrayList<>();

        //converting scores to text
        for (Pair<String, Integer> highScore : highScores) {
            StringBuilder tmp;
            tmp = new StringBuilder(highScore.getKey());
            tmp.append("_".repeat(Math.max(0, TEXT_LENGTH - highScore.getKey().length() - highScore.getValue().toString().length())));
            tmp.append(highScore.getValue().toString());
            scores.add(new Text(tmp.toString()));
        }
        highScorePane.getChildren().setAll(scores);
        highScorePane.getChildren().add(returnButton);
    }
}

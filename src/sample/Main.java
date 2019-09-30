package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import static sample.PlayScene.highScores;
import static sample.SettingScene.*;

public class Main extends Application {

    private static Scanner scanner;
    static {
        try {
            scanner = new Scanner(new File("src/resources/data.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void load(){
        //downloading high scores
        String tmpString;
        int tmpInt;
        Pair<String, Integer> tmpPair;
        for(int i = 0; i < 5; i++){
            tmpString = scanner.nextLine();
            tmpInt = scanner.nextInt();
            scanner.nextLine();
            tmpPair = new Pair<>(tmpString, tmpInt);
            highScores.add(tmpPair);
        }

        //downloading settings
        IS_MUSIC = scanner.nextBoolean();
        scanner.nextLine();
        IS_SOUND = scanner.nextBoolean();
        scanner.nextLine();

        //downloading colours
        IBlockColour = Color.valueOf(scanner.nextLine());
        JBlockColour = Color.valueOf(scanner.nextLine());
        LBlockColour = Color.valueOf(scanner.nextLine());
        SBlockColour = Color.valueOf(scanner.nextLine());
        ZBlockColour = Color.valueOf(scanner.nextLine());
        OBlockColour = Color.valueOf(scanner.nextLine());
        TBlockColour = Color.valueOf(scanner.nextLine());
    }

    static void save(){
        //preparing writer
        PrintWriter save = null;
        try {
            save = new PrintWriter(new File("src/resources/data.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert save != null;

        //saving high scores
        String tmpString;
        int tmpInt;
        for (Pair<String, Integer> highScore : highScores) {
            tmpString = highScore.getKey();
            tmpInt = highScore.getValue();
            save.println(tmpString);
            save.println(tmpInt);
        }

        //saving settings
        save.println(IS_MUSIC);
        save.println(IS_SOUND);

        //saving colours
        
        save.println(RGBToHex(IBlockColour));
        save.println(RGBToHex(JBlockColour));
        save.println(RGBToHex(LBlockColour));
        save.println(RGBToHex(SBlockColour));
        save.println(RGBToHex(ZBlockColour));
        save.println(RGBToHex(OBlockColour));
        save.println(RGBToHex(TBlockColour));

        //closing writer
        save.close();
    }

     private static String RGBToHex( Color color ) {
            return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Tetris");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.getIcons().add(new Image("resources/icon.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        //System.out.println("\u262D");
        load();
        launch(args);
    }

}

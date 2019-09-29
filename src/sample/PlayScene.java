package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

import static javafx.scene.media.MediaPlayer.INDEFINITE;
import static javafx.scene.paint.Color.*;
import static sample.SettingScene.*;

public class PlayScene implements Initializable {

    //FXML variables
    @FXML
    private AnchorPane centerPane;
    @FXML
    private AnchorPane nextBlockPane;
    @FXML
    private Label nextBlockLabel;
    @FXML
    private Label scoreTextLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label timeLabelText;
    @FXML
    private Label timeLabel;
    @FXML
    private Label comboLabelText;
    @FXML
    private Label comboLabel;
    @FXML
    private Button movementButton;

    //size variables
    static final int SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    //checking board variables
    private int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];

    //creating tetromino variables
    private GraphicsContext gc;
    private GraphicsContext gcNextBlock;
    private List<Tetromino> original = new ArrayList<>();
    private List<Tetromino> tetrominos = new ArrayList<>();
    private Tetromino nextTetromino;
    private Tetromino selected;
    private final Random random = new Random();
    private int lastTetromino = -1;

    //speed variables
    private static final double MAX_SPEED = 0.3d;
    private static final double MIN_SPEED = 0.15d;
    private static final double SPEED_UPDATE_SECONDS = 15;
    private double secondsSinceUpdate = 0;
    private double speed = MAX_SPEED;

    //time variables
    private double milliseconds = 0;
    private double time = 0;

    //score variables
    private int score = 0;
    private double combo = 1;

    //pause dialog variables
    private boolean isPaused = false;

    //settings variables

    //music variables
    private boolean isMusic = IS_MUSIC;
    private MediaPlayer mediaPlayer = new MediaPlayer(new Media(getClass().getResource("../resources/Tetris.mp3").toString()));

    //sound variables
    private boolean isSound = IS_SOUND;

    /**method: createContent
     * prepares board seen from user and shapes that will be generated
     * it also starts timer that will measure time and update board*/
    private void createContent() {
        centerPane.setPrefSize(BOARD_WIDTH * SIZE, BOARD_HEIGHT * SIZE);

        Canvas canvas = new Canvas(BOARD_WIDTH * SIZE, BOARD_HEIGHT * SIZE);
        gc = canvas.getGraphicsContext2D();
        centerPane.getChildren().addAll(canvas);

        Canvas nextBlockCanvas = new Canvas(4 * SIZE, 4 * SIZE);
        gcNextBlock = nextBlockCanvas.getGraphicsContext2D();
        nextBlockPane.getChildren().addAll(nextBlockCanvas);
        gcNextBlock.clearRect(0, 0, 4 * SIZE, 4 * SIZE);

        //L shape
        original.add(new Tetromino(LBlockColour,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.RIGHT),
                new Block(1, Direction.LEFT),
                new Block(1, Direction.DOWN, Direction.LEFT)));
        //O shape
        original.add(new Tetromino(OBlockColour,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.RIGHT),
                new Block(1, Direction.RIGHT, Direction.DOWN),
                new Block(1, Direction.DOWN)));
        //I shape
        original.add(new Tetromino(IBlockColour,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.LEFT),
                new Block(1, Direction.RIGHT),
                new Block(2, Direction.RIGHT)));
        //J shape
        original.add(new Tetromino(JBlockColour,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.LEFT),
                new Block(1, Direction.RIGHT),
                new Block(1, Direction.DOWN, Direction.RIGHT)));
        //T shape
        original.add(new Tetromino(TBlockColour,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.RIGHT),
                new Block(1, Direction.LEFT),
                new Block(1, Direction.DOWN)));
        //Z shape
        original.add(new Tetromino(ZBlockColour,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.DOWN),
                new Block(1, Direction.RIGHT, Direction.DOWN),
                new Block(1, Direction.LEFT)));
        //S shape
        original.add(new Tetromino(SBlockColour,
                new Block(0, Direction.UP),
                new Block(1, Direction.DOWN),
                new Block(1, Direction.LEFT, Direction.DOWN),
                new Block(1, Direction.RIGHT)));
        spawn();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if(!isPaused) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    time += 0.01f;
                    secondsSinceUpdate += 0.01f;
                    if(secondsSinceUpdate >= SPEED_UPDATE_SECONDS){
                        if(speed > MIN_SPEED) {
                            speed -= 0.01f;
                            secondsSinceUpdate = 0.00f;
                        }
                    }
                    if (time >= speed) {
                        update();
                        time = 0.0f;
                        milliseconds += speed * 1000;
                    }
                    render();
                }
            }
        };
        timer.start();
    }

    /**method: update
     * moves block one row down and updates time*/
    private void update() {
        makeMove(block -> block.move(Direction.DOWN), block -> block.move(Direction.UP), true);
        Date d = new Date((long) milliseconds);
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        String timeText = df.format(d);
        timeLabel.setText(timeText);
        showDroppedTetromino();
    }

    /**method: makeMove
     * checks if tetromino can move in given direction
     * and if check is positives moves it in given direction*/
    private void makeMove(Consumer<Tetromino> onSuccess, Consumer<Tetromino> onFail, boolean endMove) {
        selected.blocks.forEach(this::removeBlock);
        onSuccess.accept(selected);
        boolean offscreen = selected.blocks.stream().anyMatch(this::isOffScreen);
        MediaPlayer fallPlayer;
        if (!offscreen) {
            selected.blocks.forEach(this::placeBlock);
        } else {
            onFail.accept(selected);
            selected.blocks.forEach(this::placeBlock);
            if (endMove) {
                score += 1;
                scoreLabel.setText(Integer.toString(score));
                if(isSound){
                    fallPlayer = new MediaPlayer(new Media(getClass().getResource("../resources/fall.wav").toString()));
                    fallPlayer.play();
                }
                sweep();
            }
            return;
        }
        if (isInvalidState()) {
            selected.blocks.forEach(this::removeBlock);
            onFail.accept(selected);
            selected.blocks.forEach(this::placeBlock);
            if (endMove) {
                score += 1;
                scoreLabel.setText(Integer.toString(score));
                if(isSound){
                    fallPlayer = new MediaPlayer(new Media(getClass().getResource("../resources/fall.wav").toString()));
                    fallPlayer.play();
                }
                sweep();
            }
        }
    }

    /**method: removeBlock
     * changes value from 1 to 0 in correct element of matrix board*/
    private void removeBlock(Block block) {
        board[(int)block.x][(int)block.y]--;
    }

    /**method: removeBlock
     * changes value form 0 to 1 in correct element of matrix board*/
    private void placeBlock(Block block) {
        board[(int)block.x][(int)block.y]++;
    }

    /**method: isOffScreen
     * checks position od the block and return true if block is off screen
     * or false if block is inside board*/
    private boolean isOffScreen(Block block) {
        return block.x < 0 || block.x >= BOARD_WIDTH ||
                block.y < 0 || block.y >= BOARD_HEIGHT;
    }

    /**method: sweep
     * */
    private void sweep() {
        List<Integer> rows = sweepRows();
        rows.forEach(row -> {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                for (Tetromino tetromino : tetrominos) {
                    tetromino.detach(x, row);
                }
                board[x][row]--;
            }
        });
        rows.forEach(row -> tetrominos.forEach(tetromino -> tetromino.blocks.stream()
                .filter(block -> block.y < row)
                .forEach(block -> {
                    removeBlock(block);
                    block.y++;
                    placeBlock(block);
                })));
        spawn();
    }

    private List<Integer> sweepRows() {
        List<Integer> rows = new ArrayList<>();

        outer:
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[x][y] != 1) {
                    continue outer;
                }
            }
            rows.add(y);
        }
        /*TODO
         * add perfect clear bonus for clearing whole board?*/
        int level = 1;
        if(rows.size() > 0 && isSound){
            MediaPlayer linePlayer = new MediaPlayer(new Media(getClass().getResource("../resources/line.wav").toString()));
            linePlayer.play();
        }
        switch (rows.size()) {
            case 1:
                score += 100* level;
                if(combo > 1){
                    comboLabelText.setVisible(false);
                    comboLabel.setVisible(false);
                    combo = 1;
                }
                break;
            case 2:
                score += 300* level;
                if(combo > 1){
                    comboLabelText.setVisible(false);
                    comboLabel.setVisible(false);
                    combo = 1;
                }
                break;
            case 3:
                score += 500* level;
                if(combo > 1){
                    comboLabelText.setVisible(false);
                    comboLabel.setVisible(false);
                    combo = 1;
                }
                break;
            case 4:
                score += 800* level;
                if (combo < 2) {
                    combo += 0.1;
                } else {
                    combo = 2;
                }
                break;
            default:
                break;
        }
        if(combo > 1){
            score += 50*combo;
            if(combo >= 1.5){
                comboLabelText.setVisible(true);
                comboLabel.setText(Double.toString(combo));
                comboLabel.setVisible(true);
            }
        }
        scoreLabel.setText(Integer.toString(score));
        return rows;
    }

    private boolean isInvalidState() {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[x][y] > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void render() {
        gc.clearRect(0, 0, BOARD_WIDTH * SIZE, BOARD_HEIGHT * SIZE);
        tetrominos.forEach(block -> block.setColor(gc));
    }

    private void spawn() {
        if (nextTetromino == null) {
            Tetromino tetromino = original.get(getRandomTetromino()).copy();
            tetromino.move(BOARD_WIDTH / 2 - 1, 0);
            selected = tetromino;
            tetrominos.add(tetromino);
            tetromino.blocks.forEach(this::placeBlock);
            tetromino = original.get(getRandomTetromino()).copy();
            tetromino.move(BOARD_WIDTH / 2 - 1, 0);
            nextTetromino = tetromino;
        } else {
            selected = nextTetromino;
            tetrominos.add(nextTetromino);
            nextTetromino.blocks.forEach(this::placeBlock);
            nextTetromino = original.get(getRandomTetromino()).copy();
            nextTetromino.move(BOARD_WIDTH / 2 - 1, 0);
        }
        setNextBlockPane();
        if (isInvalidState()) {
            gameOver();
        }
    }

    private void gameOver(){
        /*TODO
         * make end gameOver menu with showing final score and time (Done)
         * also make it possible to choose if user want to return(WIP) or play again(Done)*/
        //pausing game
        isPaused = true;
        //creating dialog
        Dialog<ButtonType> gameOverDialog = new Dialog<>();
        gameOverDialog.setTitle("GameOver");
        gameOverDialog.setHeaderText("GAME OVER");
        //preparing time
        Date d = new Date((long) milliseconds);
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        //preparing buttons
        ButtonType buttonTypePlayAgain = new ButtonType("PlayAgain");
        ButtonType buttonTypeExit = new ButtonType("Exit");
        //text
        gameOverDialog.setHeaderText("GAME OVER\n"+ "score: " + score + "\ntime: " + df.format(d));
        //setting buttons
        gameOverDialog.getDialogPane().getButtonTypes().setAll(buttonTypePlayAgain, buttonTypeExit);

        gameOverDialog.setOnCloseRequest(e -> {
            if (gameOverDialog.getResult().equals(buttonTypePlayAgain)) {
                gcNextBlock.clearRect(0, 0, 4 * SIZE, 4 * SIZE);
                startGame();
                isPaused = false;
            } else if (gameOverDialog.getResult().equals(buttonTypeExit)) {
                Platform.exit();
            }
        });
        //showing dialog
        gameOverDialog.show();
    }

    private void setNextBlockPane(){
        Tetromino nextTetrominoCopy = nextTetromino.copy();
        gcNextBlock.clearRect(0, 0, 4 * SIZE, 4 * SIZE);
        if (original.get(0).getColor().equals(nextTetromino.getColor())) {
            //L shape original[0]
            nextTetrominoCopy.move(1.5, 1);
        } else if(original.get(1).getColor().equals(nextTetromino.getColor())){
            //O shape original[1]
            nextTetrominoCopy.move(1, 1);
        } else if(original.get(2).getColor().equals(nextTetromino.getColor())){
            //I shape original[2]
            nextTetrominoCopy.move(1d, 1.5d);
        } else if(original.get(3).getColor().equals(nextTetromino.getColor())){
            //J shape original[3]
            nextTetrominoCopy.move(1.5, 1);
        } else if(original.get(4).getColor().equals(nextTetromino.getColor())){
            //T shape original[4]
            nextTetrominoCopy.move(1.5, 1);
        } else if(original.get(5).getColor().equals(nextTetromino.getColor())){
            //Z shape original[5]
            nextTetrominoCopy.move(1.5, 1);
        } else if(original.get(6).getColor().equals(nextTetromino.getColor())){
            //S shape original[6]
            nextTetrominoCopy.move(1.5, 1);
        }
        nextTetrominoCopy.setColor(gcNextBlock);
    }

    private int getRandomTetromino(){
        if(lastTetromino == -1){
            lastTetromino = random.nextInt(original.size());
        } else if(lastTetromino == 0){
            lastTetromino = random.nextInt(original.size()-1)+1;
        } else if(lastTetromino == original.size()-1){
            lastTetromino = random.nextInt(original.size()-1);
        } else{
            if(random.nextBoolean()){
                lastTetromino = random.nextInt(lastTetromino);
            } else{
                lastTetromino = random.nextInt(original.size()-lastTetromino)+lastTetromino;
            }
        }
        return lastTetromino;
    }

    private void startGame(){
        setVariables();
        setLabels();
        loadBoards();
        createContent();
    }

    private void setVariables() {
        milliseconds = 0;
        time = 0;
        score = 0;
        combo = 1;
        speed = MAX_SPEED;
        lastTetromino = -1;
        for(int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                board[x][y] = 0;
            }
        }
        tetrominos.clear();
        original.clear();
        nextTetromino = null;
        selected = null;
    }

    private void setLabels(){
        nextBlockLabel.setFont(new Font("Times New Roman", 20));
        scoreTextLabel.setFont(new Font("Times New Roman", 20));
        scoreLabel.setFont(new Font("Times New Roman", 20));
        scoreLabel.setText(Integer.toString(score));
        timeLabelText.setFont(new Font("Times New Roman", 20));
        timeLabel.setFont(new Font("Times New Roman", 20));
        comboLabelText.setFont(new Font("Times New Roman", 20));
        comboLabel.setFont(new Font("Times New Roman", 20));
        comboLabelText.setVisible(false);
        comboLabel.setVisible(false);
    }

    private void loadBoards(){
        Rectangle[][] rectangles = new Rectangle[10][20];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                rectangles[i][j] = new Rectangle(SIZE * i, SIZE * j, SIZE, SIZE);
                rectangles[i][j].setFill(LIGHTGRAY);
                rectangles[i][j].setStroke(BLACK);
                centerPane.getChildren().add(rectangles[i][j]);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (isMusic) {
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(INDEFINITE);
        }
        startGame();
    }

    public void handleKeyPressed(){
        movementButton.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                case W:
                    if(selected.getColor() != original.get(1).getColor()) {
                        makeMove(Tetromino::rotate, Tetromino::rotateBack, false);
                    }
                    break;
                case LEFT:
                case A:
                    makeMove(p -> p.move(Direction.LEFT), p -> p.move(Direction.RIGHT), false);
                    break;
                case RIGHT:
                case D:
                    makeMove(p -> p.move(Direction.RIGHT), p -> p.move(Direction.LEFT), false);
                    break;
                case DOWN:
                case S:
                    makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
                    break;
                case SPACE:
                    //this one is ok but does not allow player to instantly drop a tetromino
                    while(selected.y != 0){
                        makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
                    }
                    score++;
                    break;
                case ESCAPE:
                    isPaused = true;
                    pauseMenu();
                    break;
            }
            render();
        });
    }

    private void showDroppedTetromino(){
        /*TODO
         * showing where tetromino will fall if pressed space*/

    }

    private void pauseMenu() {

        //pausing game
        isPaused = true;

        //size variables
        final int DIALOG_WIDTH = 350;
        final int DIALOG_HEIGHT = 200;

        //creating dialog
        Dialog<Boolean> pauseDialog = new Dialog<>();
        pauseDialog.setTitle("Pause");
        pauseDialog.setHeaderText("GAME PAUSED");

        //declaring panes
        FlowPane helpPane = new FlowPane();
        BorderPane pausePane = new BorderPane();
        HBox settingsPane = new HBox();
        HBox pauseButtonsPane = new HBox();
        VBox highScorePane = new VBox();

        //Pause buttons content
        Button returnButton = new Button("Return");
        Button helpButton = new Button("Help");
        Button highScoreButton = new Button("HighScore");
        Button settingsButton = new Button("Settings");
        Button exitButton = new Button("Exit");

        //Pause buttons onAction
        returnButton.setOnAction(e->{
            pauseDialog.setResult(true);
            pauseDialog.close();
            isPaused = false;
        });
        helpButton.setOnAction(e-> pausePane.setCenter(helpPane));
        settingsButton.setOnAction(e-> pausePane.setCenter(settingsPane));
        exitButton.setOnAction(e->Platform.exit());
        highScoreButton.setOnAction(e-> pausePane.setCenter(highScorePane));

        //pause buttons pane
        pauseButtonsPane.setAlignment(Pos.CENTER);
        pauseButtonsPane.setSpacing(10);
        pauseButtonsPane.getChildren().addAll(returnButton, helpButton, highScoreButton, settingsButton, exitButton);

        //Settings content
        CheckBox musicCheckBox = new CheckBox("Music");
        musicCheckBox.setSelected(isMusic);
        musicCheckBox.setOnAction(e->{
            isMusic = musicCheckBox.isSelected();
            if (isMusic) {
                mediaPlayer.setAutoPlay(true);
                mediaPlayer.setCycleCount(INDEFINITE);
            } else {
                mediaPlayer.stop();
                mediaPlayer.setAutoPlay(false);
            }
        });
        CheckBox soundCheckBox = new CheckBox("Sound");
        soundCheckBox.setSelected(isSound);
        soundCheckBox.setOnAction(e->isSound = !isSound);

        //setting pane
        settingsPane.setAlignment(Pos.CENTER);
        settingsPane.setSpacing(10);
        settingsPane.getChildren().addAll(musicCheckBox, soundCheckBox);

        //help content
        Text helpText = new Text("w/up - rotate\na/left - move left\n" +
                "d/right - move right\ns/down - move down");
        helpText.setTextAlignment(TextAlignment.CENTER);

        //help pane
        helpPane.setAlignment(Pos.CENTER);
        helpPane.setOrientation(Orientation.VERTICAL);
        helpPane.setVgap(10);
        helpPane.getChildren().addAll(helpText);

        //highScoreContent
        //size variables
        final int TEXT_LENGTH = 20;

        //list of high scores
        List<Pair<String, Integer>> highScores = new ArrayList<>();
        highScores.add(new Pair<>("John", 20000));
        highScores.add(new Pair<>("Tim", 40000));
        highScores.add(new Pair<>("Dwayne", 300));
        highScores.add(new Pair<>("Mark", 50));
        highScores.add(new Pair<>("Chris", 25));

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

        //highScore pane
        highScorePane.setAlignment(Pos.CENTER);
        highScorePane.setSpacing(10);
        highScorePane.getChildren().addAll(scores);

        //pause dialog pane
        pausePane.setPrefSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        pausePane.setCenter(settingsPane);
        pausePane.setBottom(pauseButtonsPane);

        //setting pausePane as a pause dialog content
        pauseDialog.getDialogPane().setContent(pausePane);

        //showing dialog pane
        pauseDialog.show();
    }
}
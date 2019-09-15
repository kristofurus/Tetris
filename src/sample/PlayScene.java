package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

import static javafx.scene.paint.Color.*;

public class PlayScene implements Initializable {

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

    static final int SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    private GraphicsContext gc;
    private GraphicsContext gcNextBlock;
    private int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];
    private List<Tetromino> original = new ArrayList<>();
    private List<Tetromino> tetrominos = new ArrayList<>();
    private Tetromino nextTetromino;
    private Tetromino selected;

    private double milliseconds = 0;
    private double time = 0;
    private double maxTime = 0.3;
    private int score = 0;
    private double combo = 1;
    private int lastTetromino = -1;
    private Random random = new Random();

    private void createContent() {
        score = 0;
        scoreLabel.setText(Integer.toString(score));
        centerPane.setPrefSize(BOARD_WIDTH * SIZE, BOARD_HEIGHT * SIZE);
        Canvas canvas = new Canvas(BOARD_WIDTH * SIZE, BOARD_HEIGHT * SIZE);
        gc = canvas.getGraphicsContext2D();
        centerPane.getChildren().addAll(canvas);
        Canvas nextBlockCanvas = new Canvas(4 * SIZE, 4 * SIZE);
        gcNextBlock = nextBlockCanvas.getGraphicsContext2D();
        nextBlockPane.getChildren().addAll(nextBlockCanvas);
        //L shape
        original.add(new Tetromino(BLUE,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.RIGHT),
                new Block(2, Direction.RIGHT),
                new Block(1, Direction.DOWN)));
        //O shape
        original.add(new Tetromino(GREEN,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.RIGHT),
                new Block(1, Direction.RIGHT, Direction.DOWN),
                new Block(1, Direction.DOWN)));
        //I shape
        original.add(new Tetromino(YELLOW,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.DOWN),
                new Block(2, Direction.DOWN),
                new Block(3, Direction.DOWN)));
        //J shape
        original.add(new Tetromino(VIOLET,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.LEFT),
                new Block(2, Direction.LEFT),
                new Block(1, Direction.DOWN)));
        //T shape
        original.add(new Tetromino(ORANGE,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.RIGHT),
                new Block(1, Direction.RIGHT, Direction.DOWN),
                new Block(2, Direction.RIGHT)));
        //Z shape
        original.add(new Tetromino(RED,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.DOWN),
                new Block(1, Direction.RIGHT, Direction.DOWN),
                new Block(1, Direction.LEFT)));
        //S shape
        original.add(new Tetromino(INDIGO,
                new Block(0, Direction.DOWN),
                new Block(1, Direction.DOWN),
                new Block(1, Direction.LEFT, Direction.DOWN),
                new Block(1, Direction.RIGHT)));
        spawn();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                time += 0.01;
                if(time >= maxTime) {
                    update();
                    time = 0;
                    milliseconds += maxTime*1000;
                }
                render();
            }
        };
        timer.start();
    }

    private void update() {
        makeMove(block -> block.move(Direction.DOWN), block -> block.move(Direction.UP), true);
        Date d = new Date((long) milliseconds);
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        String timeText = df.format(d);
        timeLabel.setText(timeText);
    }

    private void makeMove(Consumer<Tetromino> onSuccess, Consumer<Tetromino> onFail, boolean endMove) {
        selected.blocks.forEach(this::removeBlock);
        onSuccess.accept(selected);
        boolean offscreen = selected.blocks.stream().anyMatch(this::isOffScreen);
        if (!offscreen) {
            selected.blocks.forEach(this::placeBlock);
        } else {
            onFail.accept(selected);
            selected.blocks.forEach(this::placeBlock);
            if (endMove) {
                sweep();
                score += 1;
                scoreLabel.setText(Integer.toString(score));
            }
            return;
        }
        if (isInvalidState()) {
            selected.blocks.forEach(this::removeBlock);
            onFail.accept(selected);
            selected.blocks.forEach(this::placeBlock);
            if (endMove) {
                sweep();
                score += 1;
                scoreLabel.setText(Integer.toString(score));
            }
        }
    }

    private void removeBlock(Block block) {
        board[block.x][block.y]--;
    }

    private void placeBlock(Block block) {
        board[block.x][block.y]++;
    }

    private boolean isOffScreen(Block block) {
        return block.x < 0 || block.x >= BOARD_WIDTH ||
                block.y < 0 || block.y >= BOARD_HEIGHT;
    }

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
        int level = 1;
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
        Tetromino nextTetrominoCopy = nextTetromino.copy();
        /*TODO
         * make showing next block to look better */
        gcNextBlock.clearRect(0, 0, 4 * SIZE, 4 * SIZE);
        nextTetrominoCopy.move(1, 0);
        nextTetrominoCopy.setColor(gcNextBlock);
        if (isInvalidState()) {
            System.out.println("Game Over");
            for(int i = 0; i < 100; i++){
                System.out.println(getRandomTetromino());
            }
            Platform.exit();
        }
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
        nextBlockLabel.setFont(new Font("Times New Roman", 20));
        scoreTextLabel.setFont(new Font("Times New Roman", 20));
        scoreLabel.setFont(new Font("Times New Roman", 20));
        timeLabelText.setFont(new Font("Times New Roman", 20));
        timeLabel.setFont(new Font("Times New Roman", 20));
        comboLabelText.setFont(new Font("Times New Roman", 20));
        comboLabel.setFont(new Font("Times New Roman", 20));
        comboLabelText.setVisible(false);
        comboLabel.setVisible(false);
        loadBoards();
        createContent();
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
        Rectangle[][] nextTetromino = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextTetromino[i][j] = new Rectangle(SIZE * i, SIZE * j, SIZE, SIZE);
                nextTetromino[i][j].setFill(LIGHTGRAY);
                nextTetromino[i][j].setStroke(BLACK);
                nextBlockPane.getChildren().add(nextTetromino[i][j]);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startGame();
    }

    public void handleKeyPressed(){
        movementButton.setOnKeyPressed(e -> {
            System.out.println(e.getCode());
            switch (e.getCode()) {
                case UP:
                case W:
                    makeMove(Tetromino::rotate, Tetromino::rotateBack, false);
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
                case ESCAPE:
                    /*TODO
                     * add pause */
                    break;
            }
            render();
        });
    }

}
